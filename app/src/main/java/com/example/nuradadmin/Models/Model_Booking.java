package com.example.nuradadmin.Models;

public class Model_Booking {
    private String customerName, phoneNumber, checkInDate, checkOutDate, bookingPrice, extraAdult, extraChild, note, room;

    public Model_Booking() {
    }

    public Model_Booking(String customerName, String phoneNumber, String checkInDate, String checkOutDate, String bookingPrice, String extraAdult, String extraChild, String note, String room) {
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

    public String getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(String bookingPrice) {
        this.bookingPrice = bookingPrice;
    }

    public String getExtraAdult() {
        return extraAdult;
    }

    public void setExtraAdult(String extraAdult) {
        this.extraAdult = extraAdult;
    }

    public String getExtraChild() {
        return extraChild;
    }

    public void setExtraChild(String extraChild) {
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
