package com.example.nuradadmin.Activities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class CheckOutDateValidator implements CalendarConstraints.DateValidator {
    private final long checkInDate;

    public CheckOutDateValidator(long checkInDate) {
        this.checkInDate = checkInDate;
    }

    @Override
    public boolean isValid(long date) {
        return date > checkInDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(checkInDate);
    }

    public static final Parcelable.Creator<CheckOutDateValidator> CREATOR = new Parcelable.Creator<CheckOutDateValidator>() {
        @Override
        public CheckOutDateValidator createFromParcel(Parcel in) {
            return new CheckOutDateValidator(in.readLong());
        }

        @Override
        public CheckOutDateValidator[] newArray(int size) {
            return new CheckOutDateValidator[size];
        }
    };
}