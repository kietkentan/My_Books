package com.khtn.mybooks.model;

public class Publisher {
    private String logo;
    private String name;
    private Address location;
    private Rating rating;
    private String worked;
    private String id;
    private int reply;
    private int product;
    private String description;
    private int followed;

    public Publisher() {}

    public Publisher(String logo, String name, Address location, Rating rating, String worked, String id, int reply, int product, String description, int followed) {
        this.logo = logo;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.worked = worked;
        this.id = id;
        this.reply = reply;
        this.product = product;
        this.description = description;
        this.followed = followed;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getWorked() {
        return worked;
    }

    public void setWorked(String worked) {
        this.worked = worked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }
}
