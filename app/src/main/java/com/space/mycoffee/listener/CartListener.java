package com.space.mycoffee.listener;

public interface CartListener {
    void onCheckedChange(String idCoffee, boolean isCheck);
    void onQuantityChange(String idCoffee, int newQuantity);
    void onItemViewChecked(String idCoffee);
}
