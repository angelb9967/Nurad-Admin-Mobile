package com.example.nuradadmin.Models;

public class Model_Booking {
    private String customerName, phoneNumber, checkInDate, checkOutDate, note, room;
    private double bookingPrice;
    private int extraAdult, extraChild;

    public Model_Booking() {
    }

    public Model_Booking(String customerName, String phoneNumber, String checkInDate, String checkOutDate, double bookingPrice, int extraAdult, int extraChild, String note, String room) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingPrice = bookingPrice;
        this.extraAdult = extraAdult;
        this.extraChild = extraChild;
        this.note = note;
        this.room = room;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(double bookingPrice) {
        this.bookingPrice = bookingPrice;
    }

    public int getExtraAdult() {
        return extraAdult;
    }

    public void setExtraAdult(int extraAdult) {
        this.extraAdult = extraAdult;
    }

    public int getExtraChild() {
        return extraChild;
    }

    public void setExtraChild(int extraChild) {
        this.extraChild = extraChild;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
