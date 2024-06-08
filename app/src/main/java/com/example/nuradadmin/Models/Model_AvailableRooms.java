package com.example.nuradadmin.Models;

public class Model_AvailableRooms {
    private String roomName, lastDateUsed, lastTimeUsed, status;

    public Model_AvailableRooms() {
    }

    public Model_AvailableRooms(String roomName, String lastDateUsed, String lastTimeUsed, String status) {
        this.roomName = roomName;
        this.lastDateUsed = lastDateUsed;
        this.lastTimeUsed = lastTimeUsed;
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastDateUsed() {
        return lastDateUsed;
    }

    public void setLastDateUsed(String lastDateUsed) {
        this.lastDateUsed = lastDateUsed;
    }

    public String getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(String lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
