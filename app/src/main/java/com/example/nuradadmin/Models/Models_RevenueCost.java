package com.example.nuradadmin.Models;

public class Models_RevenueCost {
    private String date, time, selected, amount, note;

    public Models_RevenueCost() {
    }

    public Models_RevenueCost(String date, String time, String selected, String amount, String note) {
        this.date = date;
        this.time = time;
        this.selected = selected;
        this.amount = amount;
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
