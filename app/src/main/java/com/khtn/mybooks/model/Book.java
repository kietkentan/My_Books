package com.khtn.mybooks.model;

import java.util.List;

public class Book {
    private List<String> images; // ảnh review
    private int originalPrice; // giá gốc
    private int discount; // % giảm
    private int amount; // số lượng tồn kho
    private int sold;
    private int totalRatings;
    private float totalRatingScore;
    private String datePosted;
    private String name; // tên sách
    private String id;
    private String publisher;

    public Book() {}

    public Book(List<String> images, int originalPrice, int discountPercentage, int amount, int sold, int totalRatings, float totalRatingScore, String name, String datePosted, String id, String publisher) {
        this.images = images;
        this.originalPrice = originalPrice;
        this.discount = discountPercentage;
        this.amount = amount;
        this.sold = sold;
        this.totalRatings = totalRatings;
        this.totalRatingScore = totalRatingScore;
        this.datePosted = datePosted;
        this.name = name;
        this.id = id;
        this.publisher = publisher;
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

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public float getTotalRatingScore() {
        return totalRatingScore;
    }

    public void setTotalRatingScore(float totalRatingScore) {
        this.totalRatingScore = totalRatingScore;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getReducedPrice() {
        return this.originalPrice * (100 - this.discount) / 100;
    }
}
