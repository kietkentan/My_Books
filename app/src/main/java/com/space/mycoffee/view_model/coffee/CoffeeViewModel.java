package com.space.mycoffee.view_model.coffee;

import android.annotation.SuppressLint;
import android.content.Context;

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

public class CoffeeViewModel extends ViewModel {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    MutableLiveData<CoffeeDetail> _coffeeDetail = new MutableLiveData<>();
    public LiveData<CoffeeDetail> coffeeDetail = _coffeeDetail;

    MutableLiveData<UiState> _stateAddCart = new MutableLiveData<>();
    public LiveData<UiState> stateAddCart = _stateAddCart;

    MutableLiveData<UiState> _stateBuyNow = new MutableLiveData<>();
    public LiveData<UiState> stateBuyNow = _stateBuyNow;

    MutableLiveData<Boolean> _isFavorite = new MutableLiveData<>(false);
    public LiveData<Boolean> isFavorite = _isFavorite;

    MutableLiveData<Boolean> _isEnableBuy = new MutableLiveData<>(false);
    public LiveData<Boolean> isEnableBuy = _isEnableBuy;

    MutableLiveData<Boolean> _isSell = new MutableLiveData<>(false);
    public LiveData<Boolean> isSell = _isSell;

    public Order tempOrder = null;

    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public CoffeeViewModel(@NonNull Context context) {
        this.context = context;
    }

    public void fetchCoffeeDetail(String idCoffee) {
        database.getReference(Constants.COFFEE)
                .child(idCoffee).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CoffeeDetail detail = snapshot.getValue(CoffeeDetail.class);
                            _coffeeDetail.postValue(detail);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setStateAddCart(UiState state) {
        _stateAddCart.postValue(state);
    }

    public void setStateBuyNow(UiState state) {
        _stateBuyNow.postValue(state);
    }

    public void addToCart(int numAdd) {
        CoffeeDetail detail = _coffeeDetail.getValue();
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

    public void buyNow(int numAdd) {
        CoffeeDetail detail = _coffeeDetail.getValue();
        if (detail == null) return;
        String idCoffee = detail.getId();
        tempOrder = null;
        _stateBuyNow.postValue(UiState.Loading);
        database.getReference(Constants.COFFEE).child(idCoffee).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amount = snapshot.child(Constants.AMOUNT).getValue(Integer.class);
                        if (amount >= numAdd) {
                            tempOrder = new Order(detail, numAdd);
                            tempOrder.setSelected(true);
                        } else Extensions.toast(context, String.format(context.getString(R.string.limit_product), amount));
                        _stateBuyNow.postValue(UiState.Success);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _stateBuyNow.postValue(UiState.Failure);
                    }
                });
    }

    public void setCheckFavorite(boolean check) {
        _isFavorite.postValue(check);
    }

    public void setEnableBuy(boolean check) {
        _isEnableBuy.postValue(check);
    }

    public void setSell(boolean check) {
        _isSell.postValue(check);
    }

    public void addToFavorite() {
        CoffeeDetail coffee = _coffeeDetail.getValue();
        if (coffee == null) return;
        database.getReference(Constants.USER).child(AppSingleton.mode[AppSingleton.modeLogin - 1]).
                child(AppSingleton.currentUser.getId()).child(Constants.FAVORITE).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CoffeeDetail> list = new ArrayList<>();
                        boolean check = false;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            CoffeeDetail detail = snapshot1.getValue(CoffeeDetail.class);
                            if (!detail.getId().equals(coffee.getId())) {
                                list.add(detail);
                                continue;
                            }
                            check = true;
                        }
                        if (!check) {
                            snapshot.child(String.valueOf(snapshot.getChildrenCount())).getRef().setValue(coffee);
                            if (AppSingleton.currentUser.getList_favorite() == null)
                                AppSingleton.currentUser.setList_favorite(new ArrayList<>());
                            AppSingleton.currentUser.getList_favorite().add(coffee);
                            Extensions.toast(context, R.string.added_favorite);
                            setCheckFavorite(true);
                            return;
                        }
                        snapshot.getRef().removeValue();
                        if (list.size() > 0)
                            snapshot.getRef().setValue(list);
                        Extensions.toast(context, R.string.un_added_favorite);
                        AppSingleton.currentUser.removeFavoriteById(coffee.getId());
                        setCheckFavorite(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
