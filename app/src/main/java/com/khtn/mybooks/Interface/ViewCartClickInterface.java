package com.khtn.mybooks.Interface;

import com.khtn.mybooks.model.Order;

import java.util.List;

public interface ViewCartClickInterface {
    void OnCheckedChanged(List<Integer> selectedCart);
    void OnSaveAllCart(List<Order> orderList);
    void OnChangeDataCart(int position, int quantity);
}
