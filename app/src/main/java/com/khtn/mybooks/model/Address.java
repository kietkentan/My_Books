package com.khtn.mybooks.model;

public class Address {
    private String name;
    private String phone;
    private String address;
    private boolean defaultAddress;

    public Address() {
    }

    public Address(String name, String phone, String address, boolean defaultAddress) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.defaultAddress = defaultAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
