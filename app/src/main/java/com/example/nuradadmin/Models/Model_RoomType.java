package com.example.nuradadmin.Models;
public class Model_RoomType {
    private String roomType_Name;
    private String description;
    private boolean isChecked;
    private int backgroundColor;
    public Model_RoomType() {
    }

    public Model_RoomType(String roomType_Name, String description) {
        this.roomType_Name = roomType_Name;
        this.description = description;
    }

    public String getRoomType_Name() {
        return roomType_Name;
    }

    public void setRoomType_Name(String roomType_Name) {
        this.roomType_Name = roomType_Name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
