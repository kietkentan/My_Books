package com.khtn.mybooks.model;

import java.util.List;

public class BookItem {
    private List<String> images; // ảnh review
    private int originalPrice; // giá gốc
    private int discount; // % giảm
    private int amount; // số lượng tồn kho
    private String name; // tên sách
    private String datePosted;
    private String id;
    private String publisher;

    public BookItem() {}

    public BookItem(List<String> images, int originalPrice, int discountPercentage, int amount, String name, String datePosted, String id, String publisher) {
        this.images = images;
        this.originalPrice = originalPrice;
        this.discount = discountPercentage;
        this.amount = amount;
        this.name = name;
        this.datePosted = datePosted;
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

    public int getReducedPrice() {
        if (this.discount == 0)
            return 0;
        else {
            return this.originalPrice - this.originalPrice*this.discount /100;

        }
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
}
