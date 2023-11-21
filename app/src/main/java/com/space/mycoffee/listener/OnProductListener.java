package com.space.mycoffee.listener;

import android.view.View;

import com.space.mycoffee.model.CoffeeDetail;

public interface OnProductListener {
    void openListOption(View view, int position, CoffeeDetail detail);
}
