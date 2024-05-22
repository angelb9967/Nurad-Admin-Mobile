package com.example.nuradadmin.Models;

public class Model_Room {
    private String roomName, title, roomType, priceRule, description, imageUrl;
    private boolean depositRequired;
    private double price;

    public Model_Room() {
    }

    public Model_Room(String roomName, String title, boolean depositRequired, double price, String roomType, String priceRule, String description) {
        this.price = price;
        this.title = title;
        this.depositRequired = depositRequired;
        this.roomName = roomName;
        this.roomType = roomType;
        this.priceRule = priceRule;
        this.description = description;
    }

    public Model_Room(String roomName, String title, boolean depositRequired, double price, String roomType, String priceRule, String description, String imageUrl) {
        this.price = price;
        this.title = title;
        this.depositRequired = depositRequired;
        this.roomName = roomName;
        this.roomType = roomType;
        this.priceRule = priceRule;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isDepositRequired() {
        return depositRequired;
    }

    public void setDepositRequired(boolean depositRequired) {
        this.depositRequired = depositRequired;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getPriceRule() {
        return priceRule;
    }

    public void setPriceRule(String priceRule) {
        this.priceRule = priceRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
