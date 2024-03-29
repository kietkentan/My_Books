package com.space.mycoffee.model;

import java.util.List;

public class Request {
    private String idRequest;
    private String idUser;
    private String namePublisher;
    private String address;
    private String name;
    private String phone;
    private String dateTime;
    private List<Order> orderList;
    private int tempTotal;
    private int shipCost;
    private int total;
    private int status;
    /*
        1. Đang xác nhận
        2: Chờ vận chuyển
        3: Đang giao
        4: Đã giao
        5: Đợi xác nhận hủy
        6: Đã hủy
    */

    public Request() {}

    public Request(String idUser, String address, String name, String phone, List<Order> orderList, int tempTotal, int shipCost, int total, int status) {
        this.idUser = idUser;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.orderList = orderList;
        this.tempTotal = tempTotal;
        this.shipCost = shipCost;
        this.total = total;
        this.status = status;
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamePublisher() {
        return namePublisher;
    }

    public void setNamePublisher(String namePublisher) {
        this.namePublisher = namePublisher;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public int getTempTotal() {
        return tempTotal;
    }

    public void setTempTotal(int tempTotal) {
        this.tempTotal = tempTotal;
    }

    public int getShipCost() {
        return shipCost;
    }

    public void setShipCost(int shipCost) {
        this.shipCost = shipCost;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
