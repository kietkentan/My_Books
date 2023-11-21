package com.space.mycoffee.model;

public class Location {
    private String name_with_type;
    private String type;
    private String code;

    public Location() {}

    public Location(String name_with_type, String type, String code) {
        this.name_with_type = name_with_type;
        this.type = type;
        this.code = code;
    }

    public String getName_with_type() {
        return name_with_type;
    }

    public void setName_with_type(String name_with_type) {
        this.name_with_type = name_with_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
