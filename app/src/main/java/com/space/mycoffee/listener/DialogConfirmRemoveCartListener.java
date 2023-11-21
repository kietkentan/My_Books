package com.space.mycoffee.listener;

import java.util.List;

public interface DialogConfirmRemoveCartListener {
    void onCancelClick();
    void onConfirmClick(List<String> listRemove);
}
