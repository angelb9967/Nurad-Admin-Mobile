package com.example.nuradadmin.Models;

public class Model_History {
    private String history_id, booking_id, roomName, durationOfStay;
    private String actualCheckInDateTime;
    private String actualCheckOutDateTime;

    public Model_History() {
    }

    public Model_History(String history_id, String booking_id, String roomName, String durationOfStay, String actualCheckOutDateTime, String actualCheckInDateTime) {
        this.history_id = history_id;
        this.booking_id = booking_id;
        this.roomName = roomName;
        this.durationOfStay = durationOfStay;
        this.actualCheckOutDateTime = actualCheckOutDateTime;
        this.actualCheckInDateTime = actualCheckInDateTime;
    }

    public String getHistory_id() {
        return history_id;
    }

    public void setHistory_id(String history_id) {
        this.history_id = history_id;
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

    public String getDurationOfStay() {
        return durationOfStay;
    }

    public void setDurationOfStay(String durationOfStay) {
        this.durationOfStay = durationOfStay;
    }

    public String getActualCheckOutDateTime() {
        return actualCheckOutDateTime;
    }

    public void setActualCheckOutDateTime(String actualCheckOutDateTime) {
        this.actualCheckOutDateTime = actualCheckOutDateTime;
    }

    public String getActualCheckInDateTime() {
        return actualCheckInDateTime;
    }

    public void setActualCheckInDateTime(String actualCheckInDateTime) {
        this.actualCheckInDateTime = actualCheckInDateTime;
    }
}
