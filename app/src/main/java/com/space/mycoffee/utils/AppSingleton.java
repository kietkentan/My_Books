package com.space.mycoffee.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.space.mycoffee.model.Address;
import com.space.mycoffee.model.SavedLogin;
import com.space.mycoffee.model.User;

import java.util.List;

public class AppSingleton {
    public static User currentUser;
    public static Address addressNow;
    public static int modeLogin;
    public static String[] mode;

    public static void signOut(){
        currentUser = null;
        addressNow = null;
        modeLogin = 0;
    }

    public static void signIn(User user, Context context, int mode) {
        currentUser = user;
        modeLogin = mode;
        saveUser(context);
        currentUser.setPassword(null);
        addressNow = (currentUser.getAddressList() == null) ? null : getAddressNow(currentUser.getAddressList());
    }

    private static Address getAddressNow(@NonNull List<Address> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDefaultAddress()) return list.get(i);
        }
        return list.get(0);
    }

    public static void setAddressNow() {
        addressNow = (currentUser.getAddressList() == null) ? null : currentUser.getAddressList().get(0);
    }

    public static void saveUser(Context context) {
        SharedPreferencesManager preferences = new SharedPreferencesManager(context);
        SavedLogin savedLogin = new SavedLogin(currentUser.getId(), currentUser.getPassword(), modeLogin);
        preferences.setSavedLogin(savedLogin);
    }

    public static void clearUser(Context context) {
        SharedPreferencesManager preferences = new SharedPreferencesManager(context);
        preferences.clearData();
    }
}
