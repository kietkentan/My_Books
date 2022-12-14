package com.khtn.mybooks.model;

import java.util.List;

public class User {
    private String avatar;
    private String background;
    private String name;
    private String password;
    private String staffFrom;
    private String id;
    private String email;
    private String phone;
    private boolean staff;
    private List<Order> cartList;
    private List<Address> addressList;
    private List<Book> list_favorite;
    private List<String> list_shopFollow;

    public User() {}

    public User(String avatar, String background, String name, String password, String id, String email, String phone, String staffFrom) {
        this.avatar = avatar;
        this.background = background;
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.phone = phone;
        if (staffFrom != null){
            this.staffFrom = staffFrom;
            this.staff = true;
        } else
            this.staff = false;
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

    public String getStaffFrom() {
        return staffFrom;
    }

    public void setStaffFrom(String staffFrom) {
        this.staffFrom = staffFrom;
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

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public List<Order> getCartList() {
        return cartList;
    }

    public void setCartList(List<Order> cartList) {
        this.cartList = cartList;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<Book> getList_favorite() {
        return list_favorite;
    }

    public void removeFavoriteById(String id){
        for (int i = 0; i < list_favorite.size(); ++i)
            if (id.equals(list_favorite.get(i).getId())) {
                list_favorite.remove(i);
                break;
            }
    }

    public void setList_favorite(List<Book> list_favorite) {
        this.list_favorite = list_favorite;
    }

    public List<String> getList_shopFollow() {
        return list_shopFollow;
    }

    public void setList_shopFollow(List<String> list_shopFollow) {
        this.list_shopFollow = list_shopFollow;
    }
}
