package com.space.mycoffee.listener;

import com.space.mycoffee.model.CoffeeDetail;

public interface ProductManagerListener {
    void onItemRemoveClicked(int position);
    void onItemClicked(CoffeeDetail detail);
}
