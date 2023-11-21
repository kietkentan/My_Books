package com.space.mycoffee.listener;

import com.space.mycoffee.model.Request;

public interface RequestItemListener {
    void onItemClicked(Request request);
    void onItemRemove(int position);
    void onItemAccept(Request request, int position);
}
