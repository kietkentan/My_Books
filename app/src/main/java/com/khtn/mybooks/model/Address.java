package com.khtn.mybooks.model;

public class Address {
    private String name;
    private String phone;
    private String provinces_cities;
    private String districts;
    private String precinct;
    private String address;
    private boolean defaultAddress;

    public Address() {}

    public Address(String name, String phone, String provinces_cities, String districts, String precinct, String address, boolean defaultAddress) {
        this.name = name;
        this.phone = phone;
        this.provinces_cities = provinces_cities;
        this.districts = districts;
        this.precinct = precinct;
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

    public String getProvinces_cities() {
        return provinces_cities;
    }

    public void setProvinces_cities(String provinces_cities) {
        this.provinces_cities = provinces_cities;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getPrecinct() {
        return precinct;
    }

    public void setPrecinct(String precinct) {
        this.precinct = precinct;
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
