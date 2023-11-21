package com.space.mycoffee.model;

import androidx.annotation.NonNull;

public class Order {
    private String idCoffee;
    private String coffeeName;
    private String coffeeImage;
    private int coffeeQuantity;
    private int coffeePrice;
    private int coffeeDiscount;
    private boolean selected;

    public Order() {}

    public Order(String idCoffee, int coffeeQuantity) {
        this.idCoffee = idCoffee;
        this.coffeeQuantity = coffeeQuantity;
    }

    public Order(String idCoffee, String coffeeName, String coffeeImage, int coffeeQuantity, int coffeePrice, int coffeeDiscount) {
        this.idCoffee = idCoffee;
        this.coffeeName = coffeeName;
        this.coffeeImage = coffeeImage;
        this.coffeeQuantity = coffeeQuantity;
        this.coffeePrice = coffeePrice;
        this.coffeeDiscount = coffeeDiscount;
    }

    public Order(@NonNull CoffeeDetail coffeeDetail, int coffeeQuantity){
        idCoffee = coffeeDetail.getId();
        coffeeName = coffeeDetail.getName();
        coffeeImage = coffeeDetail.getImage().get(0);
        this.coffeeQuantity = coffeeQuantity;
        coffeePrice = coffeeDetail.getOriginalPrice();
        coffeeDiscount = coffeeDetail.getDiscount();
    }

    public String getIdCoffee() {
        return idCoffee;
    }

    public void setIdCoffee(String idCoffee) {
        this.idCoffee = idCoffee;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public String getCoffeeImage() {
        return coffeeImage;
    }

    public void setCoffeeImage(String coffeeImage) {
        this.coffeeImage = coffeeImage;
    }

    public int getCoffeeQuantity() {
        return coffeeQuantity;
    }

    public void setCoffeeQuantity(int coffeeQuantity) {
        this.coffeeQuantity = coffeeQuantity;
    }

    public int getCoffeePrice() {
        return coffeePrice;
    }

    public void setCoffeePrice(int coffeePrice) {
        this.coffeePrice = coffeePrice;
    }

    public int getCoffeeDiscount() {
        return coffeeDiscount;
    }

    public void setCoffeeDiscount(int coffeeDiscount) {
        this.coffeeDiscount = coffeeDiscount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getReducedPrice() {
        return this.coffeePrice * (100 - this.coffeeDiscount) / 100;
    }
}
