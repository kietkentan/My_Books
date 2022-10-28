package com.khtn.mybooks.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.khtn.mybooks.model.User;

public class Common {
    public static User currentUser;
    public static int modeLogin;
                            // 1: username, password
                            // 2: google
                            // 3: facebook

    public static SharedPreferences checkUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
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
        editor.commit();
    }

    public static void clearUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
