package com.khtn.mybooks.model;

public class Staff {
    private String id;
    private String userId;
    private String name;
    private String publisherId;
    private StaffPermission permission;

    public Staff() {}

    public Staff(String userId, String name, String publisherId) {
        this.id = "ST" + publisherId.substring(6) + String.valueOf(System.currentTimeMillis()).substring(6);
        this.userId = userId;
        this.name = name;
        this.publisherId = publisherId;
        this.permission = new StaffPermission(false, false, false, false);
    }

    public Staff(String userId, String name, String publisherId, StaffPermission permission) {
        this.userId = userId;
        this.name = name;
        this.publisherId = publisherId;
        this.permission = permission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public StaffPermission getPermission() {
        return permission;
    }

    public void setPermission(StaffPermission permission) {
        this.permission = permission;
    }

    public void resetPermission() {
        this.permission = new StaffPermission(false, false, false, false);
    }

    public void fullPermission() {
        this.permission = new StaffPermission(true, true, true, true);
    }
}
