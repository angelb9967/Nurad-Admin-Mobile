package com.example.nuradadmin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateBooking;
import com.example.nuradadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Booking#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Booking extends Fragment {
    private FloatingActionButton floatingBtn;
    private CalendarView calendarView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_Booking() {
        // Required empty public constructor
    }

    public static Fragment_Booking newInstance(String param1, String param2) {
        Fragment_Booking fragment = new Fragment_Booking();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__booking, container, false);

        floatingBtn = view.findViewById(R.id.floatingActionButton);
        calendarView = view.findViewById(R.id.calendarView);

        floatingBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), Activity_CreateBooking.class);
            startActivity(i);
        });

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Toast.makeText(getActivity(), "You selected: " + date, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}