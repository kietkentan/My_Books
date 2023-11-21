package com.space.mycoffee.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoffeeDetail {
    private List<String> images; // ảnh review
    private int originalPrice; // giá gốc
    private int discount; // % giảm
    private int amount; // số lượng tồn kho
    private int sold;
    private Rating rating;
    private String datePosted;
    private String timeSell;
    private String name;
    private String id;
    private boolean hide;
    private String describe;

    public CoffeeDetail() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.images = new ArrayList<>();
        this.originalPrice = 0;
        this.discount = 0;
        this.amount = 0;
        this.sold = 0;
        this.rating = new Rating();
        this.datePosted = dateFormat.format(new Date());
        this.timeSell = null;
        this.name = "";
        this.id = "TN" + System.currentTimeMillis();
        this.hide = false;
        this.describe = "";
    }

    public CoffeeDetail(@NonNull CoffeeDetail detail) {
        this.images = detail.getImage();
        this.originalPrice = detail.getOriginalPrice();
        this.discount = detail.getDiscount();
        this.amount = detail.getAmount();
        this.sold = detail.getSold();
        this.rating = detail.getRating();
        this.datePosted = detail.getDatePosted();
        this.timeSell = detail.getTimeSell();
        this.name = detail.getName();
        this.id = detail.getId();
        this.hide = detail.isHide();
        this.describe = detail.getDescribe();
    }

    public CoffeeDetail(List<String> images, int originalPrice, int discountPercentage, int amount, int sold, Rating rating, String timeSell, String name, String datePosted, String id, boolean hide, String describe) {
        this.images = images;
        this.originalPrice = originalPrice;
        this.discount = discountPercentage;
        this.amount = amount;
        this.sold = sold;
        this.rating = rating;
        this.datePosted = datePosted;
        this.timeSell = timeSell;
        this.name = name;
        this.id = id;
        this.hide = hide;
        this.describe = describe;
    }

    public List<String> getImage() {
        return images;
    }

    public void setImage(List<String> images) {
        this.images = images;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getTimeSell() {
        return timeSell;
    }

    public void setTimeSell(String timeSell) {
        this.timeSell = timeSell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe() {
        this.describe = describe;
    }

    public int getReducedPrice() {
        return this.originalPrice * (100 - this.discount) / 100;
    }
}