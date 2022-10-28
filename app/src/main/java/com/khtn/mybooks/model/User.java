package com.khtn.mybooks.model;

public class User {
    private String avatar;
    private String name;
    private String password;
    private String id;
    private String email;
    private String phone;

    public User() {}

    public User(String avatar, String name, String password, String id, String email, String phone) {
        this.avatar = avatar;
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
