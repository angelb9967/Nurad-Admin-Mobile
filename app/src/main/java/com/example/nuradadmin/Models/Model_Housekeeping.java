package com.example.nuradadmin.Models;

public class Model_Housekeeping {
    private String roomName, requestTime, status;

    public Model_Housekeeping() {
    }

    public Model_Housekeeping(String roomName, String requestTime, String status) {
        this.roomName = roomName;
        this.requestTime = requestTime;
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
