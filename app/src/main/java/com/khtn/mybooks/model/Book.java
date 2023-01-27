package com.khtn.mybooks.model;

import android.annotation.SuppressLint;
import android.icu.number.Scale;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Book {
    private List<String> images; // ảnh review
    private Detail detail;
    private int originalPrice; // giá gốc
    private int discount; // % giảm
    private int amount; // số lượng tồn kho
    private int sold;
    private Rating rating;
    private String datePosted;
    private String timeSell;
    private String name; // tên sách
    private String id;
    private String publisher;

    public Book() {}

    public Book(List<String> images, Detail detail, int originalPrice, int discountPercentage, int amount, int sold, Rating rating, String timeSell, String name, String datePosted, String id, String publisher) {
        this.images = images;
        this.detail = detail;
        this.originalPrice = originalPrice;
        this.discount = discountPercentage;
        this.amount = amount;
        this.sold = sold;
        this.rating = rating;
        this.datePosted = datePosted;
        this.timeSell = timeSell;
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

    public Detail getDetail() {
        return detail;
    }

    public List<List<String>> getListDetail(){
        List<List<String>> lists = new ArrayList<>();
        if (detail.getAuthor() != null){
            List<String> list = new ArrayList<>();
            list.add("author");
            list.add(detail.getAuthor());
            lists.add(list);
        }

        if (detail.getAgeRange() != null){
            List<String> list = new ArrayList<>();
            list.add("ageRange");
            list.add(detail.getAgeRange());
            lists.add(list);
        }

        if (detail.getPages() > 0){
            List<String> list = new ArrayList<>();
            list.add("pages");
            list.add(String.valueOf(detail.getPages()));
            lists.add(list);
        }

        if (detail.getSize() != null){
            List<String> list = new ArrayList<>();
            list.add("size");
            list.add(detail.getSize());
            lists.add(list);
        }

        if (detail.getType() != null){
            List<String> list = new ArrayList<>();
            list.add("type");
            list.add(detail.getType());
            lists.add(list);
        }

        if (detail.getWeight() > 0){
            List<String> list = new ArrayList<>();
            list.add("weight");
            list.add(String.valueOf(detail.getWeight()));
            lists.add(list);
        }

        return lists;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
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