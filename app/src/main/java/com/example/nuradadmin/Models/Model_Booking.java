package com.example.nuradadmin.Models;

public class Model_Booking {
    private String booking_id, contact_id, payment_id, address_id;
    private String checkInDate, checkOutDate, note, room;
    private double bookingPrice;
    private int extraAdult, extraChild;

    public Model_Booking() {
    }

    public Model_Booking(String booking_id, String contact_id, String address_id, String payment_id, String checkInDate, String checkOutDate, double bookingPrice, int extraAdult, int extraChild, String note, String room) {
        this.booking_id = booking_id;
        this.contact_id = contact_id;
        this.address_id = address_id;
        this.payment_id = payment_id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingPrice = bookingPrice;
        this.extraAdult = extraAdult;
        this.extraChild = extraChild;
        this.note = note;
        this.room = room;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
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
