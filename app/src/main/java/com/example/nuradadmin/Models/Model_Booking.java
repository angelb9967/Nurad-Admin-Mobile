package com.example.nuradadmin.Models;
import java.util.Map;

public class Model_Booking {
    private String booking_id, contact_id, payment_id, address_id;
    private String checkInDate, checkOutDate, note, room;
    private double bookingPrice;
    private int Adult, Child;
    private String status;
    private String checkInTime, checkOutTime;
    private Map<String, String> selectedAddOns;
    public Model_Booking() {
    }

    public Model_Booking(String booking_id, String contact_id, String address_id, String payment_id, String checkInDate, String checkOutDate, double bookingPrice, int Adult, int Child, String note, String room,  Map<String, String> selectedAddOns) {
        this.booking_id = booking_id;
        this.contact_id = contact_id;
        this.address_id = address_id;
        this.payment_id = payment_id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingPrice = bookingPrice;
        this.Adult = Adult;
        this.Child = Child;
        this.note = note;
        this.room = room;
        this.selectedAddOns = selectedAddOns;
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

    public int getAdult() {
        return Adult;
    }

    public void setAdult(int adult) {
        Adult = adult;
    }

    public int getChild() {
        return Child;
    }

    public void setChild(int child) {
        Child = child;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Map<String, String> getSelectedAddOns() {
        return selectedAddOns;
    }

    public void setSelectedAddOns(Map<String, String> selectedAddOns) {
        this.selectedAddOns = selectedAddOns;
    }
}
