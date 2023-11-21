package com.space.mycoffee.view_model.favorite;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.R;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;

import java.util.ArrayList;
import java.util.List;

public class FavoriteViewModel extends ViewModel {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final Context context;

    MutableLiveData<UiState> _stateAddCart = new MutableLiveData<>();
    public LiveData<UiState> stateAddCart = _stateAddCart;

    MutableLiveData<List<CoffeeDetail>> _listCoffee = new MutableLiveData<>();
    public LiveData<List<CoffeeDetail>> listCoffee = _listCoffee;

    MutableLiveData<UiState> _stateLoadFavorite = new MutableLiveData<>();
    public LiveData<UiState> stateLoadFavorite = _stateLoadFavorite;

    public CoffeeDetail currentCoffeeAddCart = null;

    public FavoriteViewModel(Context context) {
        this.context = context;
    }

    public void setStateAddCart(UiState state) {
        _stateAddCart.postValue(state);
    }

    public void fetchListFavorite() {
        if (AppSingleton.currentUser == null) return;
        _stateLoadFavorite.postValue(UiState.Loading);
        database.getReference(Constants.USER)
                .child(AppSingleton.mode[AppSingleton.modeLogin - 1])
                .child(AppSingleton.currentUser.getId())
                .child(Constants.FAVORITE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CoffeeDetail> listFavorite = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            listFavorite.add(dataSnapshot.getValue(CoffeeDetail.class));

                        _listCoffee.postValue(listFavorite);
                        _stateLoadFavorite.postValue(UiState.Success);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _listCoffee.postValue(null);
                        _stateLoadFavorite.postValue(UiState.Failure);
                    }
                });
    }

    public void removeFromFavorite(String idCoffee) {
        database.getReference(Constants.USER)
                .child(AppSingleton.mode[AppSingleton.modeLogin - 1]).
                child(AppSingleton.currentUser.getId())
                .child(Constants.FAVORITE).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CoffeeDetail> details = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            CoffeeDetail detail = snapshot1.getValue(CoffeeDetail.class);
                            if (!detail.getId().equals(idCoffee))
                                details.add(detail);
                        }
                        snapshot.getRef().removeValue();
                        if (details.size() > 0)
                            snapshot.getRef().setValue(details);
                        Extensions.toast(context, R.string.un_added_favorite);
                        AppSingleton.currentUser.setList_favorite(details);
                        _listCoffee.postValue(details);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void addToCart(CoffeeDetail detail, int numAdd) {
        if (detail == null) return;
        String idCoffee = detail.getId();
        List<Order> listCart = AppSingleton.currentUser.getCartList();
        boolean exists = false;
        int pos = -1;
        if (listCart != null) {
            for (int i = 0; i < listCart.size(); ++i) {
                if (listCart.get(i).getIdCoffee().equals(idCoffee)) {
                    exists = true;
                    pos = i;
                    break;
                }
            }
        }
        boolean finalExists = exists;
        int finalPos = pos;
        setStateAddCart(UiState.Loading);
        database.getReference(Constants.COFFEE).child(idCoffee).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amount = snapshot.child(Constants.AMOUNT).getValue(Integer.class);
                        if (amount > 0) {
                            database.getReference(Constants.USER).child(AppSingleton.mode[AppSingleton.modeLogin - 1]).
                                    child(AppSingleton.currentUser.getId()).child(Constants.CART_LIST).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (finalExists) {
                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                    if (snapshot1.child(Constants.ID_COFFEE).getValue(String.class).equals(idCoffee)) {
                                                        int quantity = snapshot1.child(Constants.QUANTITY).getValue(Integer.class);
                                                        snapshot1.child(Constants.QUANTITY).getRef().setValue(quantity + numAdd);
                                                        listCart.get(finalPos).setCoffeeQuantity(quantity + numAdd);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child(Constants.ID_COFFEE).getRef().setValue(idCoffee);
                                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child(Constants.QUANTITY).getRef().setValue(numAdd);
                                                AppSingleton.currentUser.addCartList(new Order(detail, numAdd));
                                            }
                                            currentCoffeeAddCart = detail;
                                            setStateAddCart(UiState.Success);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            setStateAddCart(UiState.Failure);
                                        }
                                    });
                        } else Extensions.toast(context, String.format(context.getString(R.string.limit_product), 0));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setStateAddCart(UiState.Failure);
                    }
                });
    }
}
