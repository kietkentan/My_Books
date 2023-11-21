package com.space.mycoffee.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String avatar;
    private String background;
    private String name;
    private String password;
    private String id;
    private String email;
    private String phone;
    private String dateOfBirth;
    private int gender;
    private boolean admin;
    private List<Order> cartList;
    private List<Address> addressList;
    private List<CoffeeDetail> list_favorite;

    public User() {}

    public User(String avatar, String background, String name, String password, String id, String email, String phone) {
        this.avatar = avatar;
        this.background = background;
        this.name = name;
        this.password = password;
        this.admin = false;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Order> getCartList() {
        return cartList;
    }

    public void setCartList(List<Order> cartList) {
        this.cartList = cartList;
    }

    public void addCartList(Order order) {
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        cartList.add(order);
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<CoffeeDetail> getList_favorite() {
        return list_favorite;
    }

    public void removeFavoriteById(String id){
        for (int i = 0; i < list_favorite.size(); ++i)
            if (id.equals(list_favorite.get(i).getId())) {
                list_favorite.remove(i);
                break;
            }
    }

    public void setList_favorite(List<CoffeeDetail> list_favorite) {
        this.list_favorite = list_favorite;
    }
}

