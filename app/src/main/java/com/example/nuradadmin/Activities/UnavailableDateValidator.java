package com.example.nuradadmin.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.HashMap;
import java.util.Map;

public class UnavailableDateValidator implements CalendarConstraints.DateValidator {
    private final Map<Long, Long> unavailableDateRanges;

    public UnavailableDateValidator(Map<Long, Long> unavailableDateRanges) {
        this.unavailableDateRanges = unavailableDateRanges;
    }

    @Override
    public boolean isValid(long date) {
        for (Map.Entry<Long, Long> entry : unavailableDateRanges.entrySet()) {
            if (date >= entry.getKey() && date <= entry.getValue()) {
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
        dest.writeInt(unavailableDateRanges.size());
        for (Map.Entry<Long, Long> entry : unavailableDateRanges.entrySet()) {
            dest.writeLong(entry.getKey());
            dest.writeLong(entry.getValue());
        }
    }

    public static final Parcelable.Creator<UnavailableDateValidator> CREATOR = new Parcelable.Creator<UnavailableDateValidator>() {
        @Override
        public UnavailableDateValidator createFromParcel(Parcel in) {
            int size = in.readInt();
            Map<Long, Long> unavailableDateRanges = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                long key = in.readLong();
                long value = in.readLong();
                unavailableDateRanges.put(key, value);
            }
            return new UnavailableDateValidator(unavailableDateRanges);
        }

        @Override
        public UnavailableDateValidator[] newArray(int size) {
            return new UnavailableDateValidator[size];
        }
    };
}