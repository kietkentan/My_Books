package com.space.mycoffee.model;

import com.space.mycoffee.utils.Extensions;

import java.util.Comparator;
import java.util.List;

public class CoffeeItem implements Comparable<CoffeeItem>{
    private List<String> images; // ảnh review
    private int originalPrice; // giá gốc
    private int discount; // % giảm
    private int amount; // số lượng tồn kho
    private String name;
    private String datePosted;
    private String id;
    private String coffeeType;

    public CoffeeItem() {}

    public CoffeeItem(List<String> images, int originalPrice, int discountPercentage, int amount, String name, String datePosted, String id, String publisher) {
        this.images = images;
        this.originalPrice = originalPrice;
        this.discount = discountPercentage;
        this.amount = amount;
        this.name = name;
        this.datePosted = datePosted;
        this.id = id;
        this.coffeeType = publisher;
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
            return originalPrice;
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

    public String getCoffeeType() {
        return coffeeType;
    }

    public void setCoffeeType(String coffeeType) {
        this.coffeeType = coffeeType;
    }

    @Override
    public int compareTo(CoffeeItem o) {
        return (int) (Extensions.numDays(this.getDatePosted()) - Extensions.numDays(o.getDatePosted()));
    }

    public static Comparator<CoffeeItem> descendingPrice = (o1, o2) -> o2.getReducedPrice() - o1.getReducedPrice();
}
