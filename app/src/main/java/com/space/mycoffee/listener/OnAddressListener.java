package com.space.mycoffee.listener;

import android.view.View;

import com.space.mycoffee.model.Address;

public interface OnAddressListener {
    void openListOption(View view, int position, Address address);
}
