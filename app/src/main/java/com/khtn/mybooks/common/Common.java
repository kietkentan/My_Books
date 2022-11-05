package com.khtn.mybooks.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.khtn.mybooks.model.Address;
import com.khtn.mybooks.model.User;

import java.util.List;

public class Common {
    public static User currentUser;
    public static List<Address> addressLists;
    public static Address addressNow;
    public static int modeLogin;
                            // 1: username, password
                            // 2: google
                            // 3: facebook

    public static void setAddressLists(List<Address> addressList){
        addressLists = addressList;
        addressNow = (addressList == null) ? null:addressList.get(0);
    }

    public static SharedPreferences checkUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        if (preferences.contains("saved_id"))
            return preferences;
        return null;
    }

    public static void saveUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("saved_id", currentUser.getId());
        editor.putString("saved_password", currentUser.getPassword());
        editor.putInt("mode_login", modeLogin);
        editor.apply();
    }

    public static void clearUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
