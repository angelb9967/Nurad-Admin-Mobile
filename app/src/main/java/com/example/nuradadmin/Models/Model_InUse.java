package com.example.nuradadmin.Models;

public class Model_InUse {
    private String booking_id, roomName;

    public Model_InUse() {
    }

    public Model_InUse(String booking_id, String roomName) {
        this.booking_id = booking_id;
        this.roomName = roomName;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
