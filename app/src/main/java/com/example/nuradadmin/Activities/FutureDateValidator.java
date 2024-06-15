package com.example.nuradadmin.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class FutureDateValidator implements CalendarConstraints.DateValidator {

    @Override
    public boolean isValid(long date) {
        return date >= System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // No state to write
    }

    public static final Parcelable.Creator<FutureDateValidator> CREATOR = new Parcelable.Creator<FutureDateValidator>() {
        @Override
        public FutureDateValidator createFromParcel(Parcel in) {
            return new FutureDateValidator();
        }

        @Override
        public FutureDateValidator[] newArray(int size) {
            return new FutureDateValidator[size];
        }
    };
}