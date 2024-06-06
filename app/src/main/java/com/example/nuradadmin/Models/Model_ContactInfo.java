package com.example.nuradadmin.Models;

public class Model_ContactInfo {
    private String contact_id;
    private String prefix, firstName, lastName, phone, mobilePhone, email;

    public Model_ContactInfo() {
    }

    public Model_ContactInfo(String contact_id, String prefix, String firstName, String lastName, String phone, String mobilePhone, String email) {
        this.contact_id = contact_id;
        this.prefix = prefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mobilePhone = mobilePhone;
        this.email = email;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
