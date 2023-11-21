package com.space.mycoffee.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.space.mycoffee.model.SavedLogin;

public class SharedPreferencesManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesManager(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    private void saveStringByKey(String key, String value) {
        try {
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String retrieveStringByKey(String key) {
        return pref.getString(key, null);
    }

    public void clearData() {
        try {
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSavedLogin(SavedLogin savedLogin) {
        try {
            Gson gson = new Gson();
            String userJson = gson.toJson(savedLogin);
            saveStringByKey(Constants.SAVED_LOGIN, userJson);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public SavedLogin getSavedLogin() {
        String savedLoginJson = retrieveStringByKey(Constants.SAVED_LOGIN);
        if (savedLoginJson == null) {
            return null;
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(savedLoginJson, SavedLogin.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}