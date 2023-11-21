package com.space.mycoffee.model;

public class Address {
    private String id;
    private String name;
    private String phone;
    private Location provinces_cities;
    private Location districts;
    private Location precinct;
    private String address;
    private boolean defaultAddress;

    public Address() {}

    public Address(String id, String name, String phone, Location provinces_cities, Location districts, Location precinct, String address, boolean defaultAddress) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.provinces_cities = provinces_cities;
        this.districts = districts;
        this.precinct = precinct;
        this.address = address;
        this.defaultAddress = defaultAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Location getProvinces_cities() {
        return provinces_cities;
    }

    public void setProvinces_cities(Location provinces_cities) {
        this.provinces_cities = provinces_cities;
    }

    public Location getDistricts() {
        return districts;
    }

    public void setDistricts(Location districts) {
        this.districts = districts;
    }

    public Location getPrecinct() {
        return precinct;
    }

    public void setPrecinct(Location precinct) {
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
