package com.space.mycoffee.listener;

import com.space.mycoffee.model.Request;

public interface OnOrderChangeSizeInterface {
    void onItemCLicked(Request request);
    void changeSize();
}
