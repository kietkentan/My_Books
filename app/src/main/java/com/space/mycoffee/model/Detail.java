package com.space.mycoffee.model;

public class Detail {
    private String ageRange;
    private String author;
    private String describe;
    private int pages;
    private String size;
    private String type;
    private int weight;

    public Detail() {}

    public Detail(String ageRange, String author, String describe, int pages, String size, String type, int weight) {
        this.ageRange = ageRange;
        this.author = author;
        this.describe = describe;
        this.pages = pages;
        this.size = size;
        this.type = type;
        this.weight = weight;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
