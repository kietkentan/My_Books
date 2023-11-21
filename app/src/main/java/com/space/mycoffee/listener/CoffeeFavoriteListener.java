package com.space.mycoffee.listener;

import com.space.mycoffee.model.CoffeeDetail;

public interface CoffeeFavoriteListener {
    void onItemClicked(String idCoffee);
    void onFavoriteClicked(String idCoffee);
    void onAddCartClicked(CoffeeDetail coffeeDetail);
}
