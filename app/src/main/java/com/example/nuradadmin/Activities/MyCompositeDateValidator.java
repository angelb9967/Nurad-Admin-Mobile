package com.example.nuradadmin.Activities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.List;

public class MyCompositeDateValidator implements CalendarConstraints.DateValidator {
    private final List<CalendarConstraints.DateValidator> validators;

    public MyCompositeDateValidator(List<CalendarConstraints.DateValidator> validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValid(long date) {
        for (CalendarConstraints.DateValidator validator : validators) {
            if (!validator.isValid(date)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(validators);
    }

    public static final Parcelable.Creator<MyCompositeDateValidator> CREATOR = new Parcelable.Creator<MyCompositeDateValidator>() {
        @Override
        public MyCompositeDateValidator createFromParcel(Parcel in) {
            List<CalendarConstraints.DateValidator> validators = in.readArrayList(CalendarConstraints.DateValidator.class.getClassLoader());
            return new MyCompositeDateValidator(validators);
        }

        @Override
        public MyCompositeDateValidator[] newArray(int size) {
            return new MyCompositeDateValidator[size];
        }
    };
}