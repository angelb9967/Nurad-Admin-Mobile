package com.example.nuradadmin.Models;
import java.util.Map;

public class Model_Booking {
    private String roomTitle;
    private int adultCount, childCount;
    private String booking_id, contact_id, payment_id, address_id, userId;
    private String checkInDate, checkOutDate, checkInTime, checkOutTime, note, room, voucherCode;
    private String bookingDate;
    private String status;
    private double subtotalValue;
    private double voucherValueValue;
    private double roomPrice;
    private double extraAdultPrice;
    private double extraChildPrice;
    private double addOnsPrice;
    private double totalValue;
    private double vatValue;
    private Map<String, String> selectedAddOns;
    public Model_Booking() {
    }

    public Model_Booking(String booking_id, String contact_id, String address_id, String payment_id, String userId, String checkInDate, String checkOutDate, String checkInTime, String checkOutTime, String voucherCode, double subtotalValue, int adultCount, int childCount, String note, String room, Map<String, String> selectedAddOns, String bookingDate, double voucherValueValue, String status, double roomPrice, double extraAdultPrice, double extraChildPrice, double addOnsPrice, double totalValue, double vatValue, String roomTitle) {
        this.booking_id = booking_id;
        this.contact_id = contact_id;
        this.address_id = address_id;
        this.payment_id = payment_id;
        this.userId = userId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.voucherCode = voucherCode;
        this.subtotalValue = subtotalValue;
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.note = note;
        this.room = room;
        this.selectedAddOns = selectedAddOns;
        this.bookingDate = bookingDate;
        this.voucherValueValue = voucherValueValue;
        this.status = status;
        this.roomPrice = roomPrice;
        this.extraAdultPrice = extraAdultPrice;
        this.extraChildPrice = extraChildPrice;
        this.addOnsPrice = addOnsPrice;
        this.totalValue = totalValue;
        this.vatValue = vatValue;
        this.roomTitle = roomTitle;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public int getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(int adultCount) {
        this.adultCount = adultCount;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
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

    public double getSubtotalValue() {
        return subtotalValue;
    }

    public void setSubtotalValue(double subtotalValue) {
        this.subtotalValue = subtotalValue;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getVoucherValueValue() {
        return voucherValueValue;
    }

    public void setVoucherValueValue(double voucherValueValue) {
        this.voucherValueValue = voucherValueValue;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public double getExtraAdultPrice() {
        return extraAdultPrice;
    }

    public void setExtraAdultPrice(double extraAdultPrice) {
        this.extraAdultPrice = extraAdultPrice;
    }

    public double getExtraChildPrice() {
        return extraChildPrice;
    }

    public void setExtraChildPrice(double extraChildPrice) {
        this.extraChildPrice = extraChildPrice;
    }

    public double getAddOnsPrice() {
        return addOnsPrice;
    }

    public void setAddOnsPrice(double addOnsPrice) {
        this.addOnsPrice = addOnsPrice;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getVatValue() {
        return vatValue;
    }

    public void setVatValue(double vatValue) {
        this.vatValue = vatValue;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public Map<String, String> getSelectedAddOns() {
        return selectedAddOns;
    }

    public void setSelectedAddOns(Map<String, String> selectedAddOns) {
        this.selectedAddOns = selectedAddOns;
    }
}
