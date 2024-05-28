package com.example.nuradadmin.Models;

public class Model_Room {
    private String roomName, title, roomType, priceRule, description, imageUrl;
    private boolean depositRequired, isRecommended;

    public Model_Room() {
    }

    public Model_Room(String roomName, String title, boolean isRecommended, boolean depositRequired, String roomType, String priceRule, String description) {
        this.title = title;
        this.depositRequired = depositRequired;
        this.roomName = roomName;
        this.roomType = roomType;
        this.priceRule = priceRule;
        this.description = description;
        this.isRecommended = isRecommended;
    }

    public Model_Room(String roomName, String title, boolean isRecommended, boolean depositRequired, String roomType, String priceRule, String description, String imageUrl) {
        this.title = title;
        this.depositRequired = depositRequired;
        this.roomName = roomName;
        this.roomType = roomType;
        this.priceRule = priceRule;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isRecommended = isRecommended;
    }

    public boolean isDepositRequired() {
        return depositRequired;
    }

    public void setDepositRequired(boolean depositRequired) {
        this.depositRequired = depositRequired;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
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
