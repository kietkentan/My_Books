package com.space.mycoffee.model;

public class SavedLogin {
    private String savedId;
    private String savedPassword;
    private int modeLogin;

    public SavedLogin() {}

    public SavedLogin(String savedId, String savedPassword, int modeLogin) {
        this.savedId = savedId;
        this.savedPassword = savedPassword;
        this.modeLogin = modeLogin;
    }

    public String getSavedId() {
        return savedId;
    }

    public void setSavedId(String savedId) {
        this.savedId = savedId;
    }

    public String getSavedPassword() {
        return savedPassword == null ? "" : savedPassword;
    }

    public void setSavedPassword(String savedPassword) {
        this.savedPassword = savedPassword;
    }

    public int getModeLogin() {
        return modeLogin;
    }

    public void setModeLogin(int modeLogin) {
        this.modeLogin = modeLogin;
    }
}
