package com.khtn.mybooks.model;

public class Publisher {
    private String logo;
    private String name;
    private String location;
    private Float rating;
    private String worked;
    private String id;
    private int reply;

    public Publisher() {}

    public Publisher(String logo, String name, String location, Float rating, String worked, String id, int reply) {
        this.logo = logo;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.worked = worked;
        this.id = id;
        this.reply = reply;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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
}
