package com.space.mycoffee.view_model.cart;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.R;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.model.Request;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    MutableLiveData<List<Order>> _orderList = new MutableLiveData<>();
    public LiveData<List<Order>> orderList = _orderList;

    MutableLiveData<List<Order>> _orderListChecked = new MutableLiveData<>();
    public LiveData<List<Order>> orderListChecked = _orderListChecked;

    MutableLiveData<Address> _addressNow = new MutableLiveData<>();
    public LiveData<Address> addressNow = _addressNow;

    MutableLiveData<UiState> _stateAddRequest = new MutableLiveData<>();
    public LiveData<UiState> stateAddRequest = _stateAddRequest;

    public boolean isBuyNow = false;

    public void setOrderList(List<Order> list) {
        if (list == null) return;
        for (Order order : list) {
            order.setSelected(false);
        }
        changeOrderList(list);
    }

    public void clearViewModel() {
        _orderListChecked.postValue(null);
        _orderList.postValue(null);
        isBuyNow = false;
    }

    public void setAddressNow(Address address) {
        _addressNow.postValue(address);
    }

    public void setStateAddRequest(UiState state) {
        _stateAddRequest.postValue(state);
    }

    public void checkedAll(boolean isChecked) {
        List<Order> list = _orderList.getValue();
        for (Order order : list) {
            order.setSelected(isChecked);
        }
        changeOrderList(list);
        _orderListChecked.postValue(isChecked ? list : null);
    }

    public void changeOrderList(List<Order> list) {
        _orderList.postValue(list);
        AppSingleton.currentUser.setCartList(list);
    }

    public void changeChecked(String idCoffee, boolean isCheck) {
        List<Order> list = _orderList.getValue();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdCoffee().equals(idCoffee)) {
                list.get(i).setSelected(isCheck);
                break;
            }
        }
        changeOrderList(list);
    }

    public void getListChecked(List<String> listId) {
        List<Order> list = _orderList.getValue();
        List<Order> listChecked = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (listId.contains(list.get(i).getIdCoffee()))
                listChecked.add(list.get(i));
        }
        _orderListChecked.postValue(listChecked);
    }

    public void removeItemInListChecked(String idCoffee) {
        List<Order> list = _orderListChecked.getValue();
        if (list != null) {
            int pos = -1;
            for (int i = 0; i < list.size(); i++) {
                if (idCoffee.equals(list.get(i).getIdCoffee())) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) list.remove(pos);
        }
        _orderListChecked.postValue(list);
    }

    public void setTempListBuyNow(Order order) {
        List<Order> list = new ArrayList<>();
        list.add(order);
        isBuyNow = true;
        _orderListChecked.postValue(list);
    }

    public void changeItemInListChecked(String idCoffee, int newQuantity) {
        List<Order> list = _orderListChecked.getValue();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (idCoffee.equals(list.get(i).getIdCoffee())) {
                    list.get(i).setCoffeeQuantity(newQuantity);
                    _orderListChecked.postValue(list);
                    break;
                }
            }
        }
    }

    public int getTotalQuantity() {
        List<Order> list = _orderListChecked.getValue();
        int total = 0;
        if (list != null) {
            for (Order order : list) {
                total += order.getCoffeeQuantity();
            }
        }
        return total;
    }

    public int getTotalMoney() {
        List<Order> list = _orderListChecked.getValue();
        int total = 0;
        if (list != null) {
            for (Order order : list) {
                total += (order.getReducedPrice() * order.getCoffeeQuantity());
            }
        }
        return total;
    }

    public int getTotalShipCost() {
        return 20000;
    }

    public int getTotalPayment() {
        return getTotalMoney() + getTotalShipCost();
    }

    public void changeQuantity(String idCoffee, int newQuantity, Context context) {
        database.getReference(Constants.COFFEE)
                .child(idCoffee)
                .child(Constants.AMOUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amount = snapshot.getValue(Integer.class);
                        List<Order> list = _orderList.getValue();
                        int nQu = newQuantity;
                        if (amount >= newQuantity) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getIdCoffee().equals(idCoffee)) {
                                    list.get(i).setCoffeeQuantity(newQuantity);
                                    break;
                                }
                            }
                        } else {
                            Extensions.toast(context, String.format(context.getString(R.string.limit_product_2), amount));
                            if (amount <= 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getIdCoffee().equals(idCoffee)) {
                                        boolean isCheck = list.get(i).isSelected();
                                        list.get(i).setCoffeeQuantity(1);
                                        nQu = 1;
                                        if (isCheck) {
                                            list.get(i).setSelected(false);
                                            removeItemInListChecked(idCoffee);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getIdCoffee().equals(idCoffee)) {
                                        list.get(i).setCoffeeQuantity(amount);
                                        nQu = amount;
                                        break;
                                    }
                                }
                            }
                        }
                        changeItemInListChecked(idCoffee, nQu);
                        changeOrderList(list);
                        changeValueInDatabase(idCoffee, nQu);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void changeValueInDatabase(String idCoffee, int newQuantity) {
        database.getReference(Constants.USER)
                .child(AppSingleton.mode[AppSingleton.modeLogin - 1])
                .child(AppSingleton.currentUser.getId())
                .child(Constants.CART_LIST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.child(Constants.ID_COFFEE).getValue(String.class).equals(idCoffee)) {
                                snapshot1.child(Constants.QUANTITY).getRef().setValue(newQuantity);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void changeListCartInDatabase(@NonNull List<String> listRemove) {
        List<Order> listAll = _orderList.getValue();
        List<Order> listSelected = _orderListChecked.getValue();

        if (listSelected == null || listRemove.size() != listSelected.size()) {
            String singleId = listRemove.get(0);
            int pos = -1;
            for (int i = 0; i < listAll.size(); i++) {
                if (listAll.get(i).getIdCoffee().equals(singleId)) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) listAll.remove(pos);

            if (listSelected != null) {
                pos = -1;
                for (int i = 0; i < listSelected.size(); i++) {
                    if (listSelected.get(i).getIdCoffee().equals(singleId)) {
                        pos = i;
                        break;
                    }
                }
                if (pos > -1) listSelected.remove(pos);
            }
        } else {
            listSelected = null;
            for (String idCoffee : listRemove) {
                int pos = -1;
                for (int i = 0; i < listAll.size(); i++) {
                    if (listAll.get(i).getIdCoffee().equals(idCoffee)) {
                        pos = i;
                        break;
                    }
                }
                if (pos > -1) listAll.remove(pos);
            }
        }
        changeOrderList(listAll);
        _orderListChecked.postValue(listSelected);
        database.getReference(Constants.USER)
                .child(AppSingleton.mode[AppSingleton.modeLogin - 1])
                .child(AppSingleton.currentUser.getId())
                .child(Constants.CART_LIST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        for (int i = 0; i < listAll.size(); ++i) {
                            snapshot.child(String.valueOf(i)).child(Constants.ID_COFFEE).getRef().setValue(listAll.get(i).getIdCoffee());
                            snapshot.child(String.valueOf(i)).child(Constants.QUANTITY).getRef().setValue(listAll.get(i).getCoffeeQuantity());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void createOrder() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String date = dtf.format(now);
        String idRequest = String.valueOf(System.currentTimeMillis());
        Address address = _addressNow.getValue();
        List<Order> orders = _orderListChecked.getValue();
        if (address == null || orders == null) return;
        Request request = new Request(
                AppSingleton.currentUser.getId(),
                Extensions.getStringFromAddress(address),
                address.getName(),
                address.getPhone(),
                orders,
                getTotalMoney(),
                getTotalShipCost(),
                getTotalPayment(),
                1
        );
        request.setIdRequest(idRequest);
        request.setDateTime(date);
        setStateAddRequest(UiState.Loading);
        database.getReference(Constants.COFFEE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isValid = true;
                for (Order order : orders) {
                    int amount = snapshot.child(order.getIdCoffee()).child(Constants.AMOUNT).getValue(Integer.class);
                    boolean hide = snapshot.child(order.getIdCoffee()).child(Constants.HIDE).getValue(Boolean.class);
                    if (amount < order.getCoffeeQuantity() || hide) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    database.getReference(Constants.REQUEST).child(idRequest)
                            .setValue(request)
                            .addOnSuccessListener(unused -> removeListOrderSelected(orders))
                            .addOnFailureListener(e -> setStateAddRequest(UiState.Failure));
                } else setStateAddRequest(UiState.Failure);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setStateAddRequest(UiState.Failure);
            }
        });
    }

    private void removeListOrderSelected(List<Order> list) {
        List<Order> listOrder = _orderList.getValue();
        database.getReference(Constants.COFFEE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> tmp = new ArrayList<>(list);
                for (Order order : tmp) {
                    int amount = snapshot.child(order.getIdCoffee()).child(Constants.AMOUNT).getValue(Integer.class) - order.getCoffeeQuantity();
                    snapshot.child(order.getIdCoffee()).child(Constants.AMOUNT).getRef().setValue(Math.max(amount, 0));
                    if (listOrder != null) {
                        for (int i = 0; i < listOrder.size(); i++) {
                            if (listOrder.get(i).getIdCoffee().equals(order.getIdCoffee())) {
                                listOrder.remove(i);
                                break;
                            }
                        }
                    }
                }
                setStateAddRequest(UiState.Success);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setStateAddRequest(UiState.Failure);
            }
        });

        if (!isBuyNow) _orderList.postValue(listOrder);
        _orderListChecked.postValue(null);
    }
}
