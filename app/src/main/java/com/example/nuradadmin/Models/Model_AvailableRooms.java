package com.example.nuradadmin.Models;

public class Model_AvailableRooms {
    private String roomName, lastDateTimeUsed, lastCleaned, status;

    public Model_AvailableRooms() {
    }

    public Model_AvailableRooms(String roomName, String lastDateTimeUsed, String lastCleaned, String status) {
        this.roomName = roomName;
        this.lastDateTimeUsed = lastDateTimeUsed;
        this.lastCleaned = lastCleaned;
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastDateTimeUsed() {
        return lastDateTimeUsed;
    }

    public void setLastDateTimeUsed(String lastDateTimeUsed) {
        this.lastDateTimeUsed = lastDateTimeUsed;
    }

    public String getLastCleaned() {
        return lastCleaned;
    }

    public void setLastCleaned(String lastCleaned) {
        this.lastCleaned = lastCleaned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
