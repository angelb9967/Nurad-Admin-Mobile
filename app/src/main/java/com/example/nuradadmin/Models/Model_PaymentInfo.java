package com.example.nuradadmin.Models;

public class Model_PaymentInfo {
    private String payment_id;
    private String cardNumber, expirationDate, cvv, nameOnTheCard;

    public Model_PaymentInfo() {
    }

    public Model_PaymentInfo(String payment_id, String cardNumber, String expirationDate, String cvv, String nameOnTheCard) {
        this.payment_id = payment_id;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.nameOnTheCard = nameOnTheCard;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNameOnTheCard() {
        return nameOnTheCard;
    }

    public void setNameOnTheCard(String nameOnTheCard) {
        this.nameOnTheCard = nameOnTheCard;
    }
}
