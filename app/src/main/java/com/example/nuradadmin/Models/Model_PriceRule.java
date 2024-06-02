package com.example.nuradadmin.Models;

public class Model_PriceRule {
    private String ruleName;
    private double price, extraAdult_price, extraChild_price, friday_price, saturday_price, sunday_price;
    public Model_PriceRule() {
    }

    public Model_PriceRule(String ruleName, double price, double extraAdult_price, double extraChild_price, double friday_price, double saturday_price, double sunday_price) {
        this.ruleName = ruleName;
        this.price = price;
        this.extraAdult_price = extraAdult_price;
        this.extraChild_price = extraChild_price;
        this.friday_price = friday_price;
        this.saturday_price = saturday_price;
        this.sunday_price = sunday_price;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getExtraAdult_price() {
        return extraAdult_price;
    }

    public void setExtraAdult_price(double extraAdult_price) {
        this.extraAdult_price = extraAdult_price;
    }

    public double getExtraChild_price() {
        return extraChild_price;
    }

    public void setExtraChild_price(double extraChild_price) {
        this.extraChild_price = extraChild_price;
    }

    public double getFriday_price() {
        return friday_price;
    }

    public void setFriday_price(double friday_price) {
        this.friday_price = friday_price;
    }

    public double getSaturday_price() {
        return saturday_price;
    }

    public void setSaturday_price(double saturday_price) {
        this.saturday_price = saturday_price;
    }

    public double getSunday_price() {
        return sunday_price;
    }

    public void setSunday_price(double sunday_price) {
        this.sunday_price = sunday_price;
    }
}
