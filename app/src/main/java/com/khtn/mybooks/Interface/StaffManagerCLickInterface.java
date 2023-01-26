package com.khtn.mybooks.Interface;

import com.khtn.mybooks.model.StaffPermission;

public interface StaffManagerCLickInterface {
    void onClickShowInfo(int position);
    void onClickSaveInfo(int position, StaffPermission permission);
    void onClickRemoveInfo(String userId);
}
