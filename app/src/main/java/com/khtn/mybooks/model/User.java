package com.khtn.mybooks.model;

public class User {
    private String avatar;
    private String background;
    private String name;
    private String password;
    private String id;
    private String email;
    private String phone;
    private String address;

    public User() {}

    public User(String avatar, String background, String name, String password, String id, String email, String phone, String address) {
        this.avatar = avatar;
        this.background = background;
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
