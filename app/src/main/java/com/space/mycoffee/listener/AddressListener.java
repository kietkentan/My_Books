package com.space.mycoffee.listener;

import com.space.mycoffee.model.Address;

public interface AddressListener {
    void onSelectedChange(Address address);
    void onRemoveItem(int position);
    void onDefaultChange(int position);
    void onItemClicked(Address address);
}
