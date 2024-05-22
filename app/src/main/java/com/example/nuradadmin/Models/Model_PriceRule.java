package com.example.nuradadmin.Models;

public class Model_PriceRule {
    private String ruleName, price, extraAdult_price, extraChild_price, friday_price, saturday_price, sunday_price;

    public Model_PriceRule() {
    }

    public Model_PriceRule(String ruleName, String price, String extraAdult_price, String extraChild_price,  String friday_price, String saturday_price, String sunday_price) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExtraAdult_price() {
        return extraAdult_price;
    }

    public void setExtraAdult_price(String extraAdult_price) {
        this.extraAdult_price = extraAdult_price;
    }

    public String getExtraChild_price() {
        return extraChild_price;
    }

    public void setExtraChild_price(String extraChild_price) {
        this.extraChild_price = extraChild_price;
    }

    public String getFriday_price() {
        return friday_price;
    }

    public void setFriday_price(String friday_price) {
        this.friday_price = friday_price;
    }

    public String getSaturday_price() {
        return saturday_price;
    }

    public void setSaturday_price(String saturday_price) {
        this.saturday_price = saturday_price;
    }

    public String getSunday_price() {
        return sunday_price;
    }

    public void setSunday_price(String sunday_price) {
        this.sunday_price = sunday_price;
    }
}
