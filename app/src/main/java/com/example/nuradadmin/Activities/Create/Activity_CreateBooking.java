package com.example.nuradadmin.Activities.Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Activities.SideMenu.Activity_BookingCalendar;
import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Activity_CreateBooking extends AppCompatActivity {
    private ImageView back_icon, CheckIn_Img, CheckOut_Img, Adult_Plus, Adult_Minus, Child_Plus, Child_Minus;
    private EditText CheckIn_Etxt, CheckOut_Etxt, Adult_Etxt, Child_Etxt;
    private TextView title;
    private Spinner rooms_spinner, roomsType_spinner;
    private Calendar calendar;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        rooms_spinner = findViewById(R.id.Rooms_Spinner);
        roomsType_spinner = findViewById(R.id.RoomsType_Spinner);
        CheckIn_Etxt = findViewById(R.id.CheckInDate_Etxt);
        CheckOut_Etxt = findViewById(R.id.CheckOutDate_Etxt);
        CheckIn_Img = findViewById(R.id.CalendarPicker_In_Img);
        CheckOut_Img = findViewById(R.id.CalendarPicker_Out_Img);
        Adult_Etxt = findViewById(R.id.Adult_Etxt);
        Adult_Plus = findViewById(R.id.Adult_Plus_Img);
        Adult_Minus = findViewById(R.id.Adult_Minus_Img);
        Child_Etxt = findViewById(R.id.Child_Etxt);
        Child_Plus = findViewById(R.id.Child_Plus_Img);
        Child_Minus = findViewById(R.id.Child_Minus_Img);

        title.setText("Create");

        Adult_Plus.setOnClickListener(view -> {
            addOrSubtract_Quantity(Adult_Etxt, "plus", String.valueOf(Adult_Etxt.getText()));
        });

        Adult_Minus.setOnClickListener(view -> {
            addOrSubtract_Quantity(Adult_Etxt, "minus", String.valueOf(Adult_Etxt.getText()));
        });

        Child_Plus.setOnClickListener(view -> {
            addOrSubtract_Quantity(Child_Etxt, "plus", String.valueOf(Child_Etxt.getText()));
        });

        Child_Minus.setOnClickListener(view -> {
            addOrSubtract_Quantity(Child_Etxt, "minus", String.valueOf(Child_Etxt.getText()));
        });

        calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Prevent keyboard from appearing when EditText is touched
        CheckIn_Etxt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker_and_setDate(CheckIn_Etxt, year, month, day);
                return true;
            }
            return false;
        });

        CheckIn_Img.setOnClickListener(view -> {
            showDatePicker_and_setDate(CheckIn_Etxt, year, month, day);
        });

        CheckIn_Etxt.setOnFocusChangeListener((view, hasFocus) ->{
            if(hasFocus){
                showDatePicker_and_setDate(CheckIn_Etxt, year, month, day);
            }
        });

        CheckIn_Etxt.setOnClickListener(view ->{
            showDatePicker_and_setDate(CheckIn_Etxt, year, month, day);
        });

        CheckOut_Etxt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
                return true;
            }
            return false;
        });

        CheckOut_Img.setOnClickListener(view -> {
            showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
        });

        CheckOut_Etxt.setOnFocusChangeListener((view, hasFocus) ->{
            if(hasFocus){
                showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
            }
        });

        CheckOut_Etxt.setOnClickListener(view ->{
            showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
        });

        String[] value1 = {"Select Room", "Room 1", "Room 2", "Room 3"};
        ArrayList<String> arrayList1 = new ArrayList<>(Arrays.asList(value1));
        CustomArrayAdapter arrayAdapter1 = new CustomArrayAdapter(this, R.layout.style_spinner, arrayList1);
        rooms_spinner.setAdapter(arrayAdapter1);

        String[] value2 = {"Select Room Type", "Single", "Double"};
        ArrayList<String> arrayList2 = new ArrayList<>(Arrays.asList(value2));
        CustomArrayAdapter arrayAdapter2 = new CustomArrayAdapter(this, R.layout.style_spinner, arrayList2);
        roomsType_spinner.setAdapter(arrayAdapter2);

        // Set default selection to "Select Room"
        rooms_spinner.setSelection(0);
        rooms_spinner.post(() -> {
            View selectedView = rooms_spinner.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                ((TextView) selectedView).setTextColor(Color.GRAY);
            }
        });

        rooms_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (view != null && view instanceof TextView) {
                    if (position == 0) {
                        ((TextView) view).setTextColor(Color.GRAY);
                    } else {
                        ((TextView) view).setTextColor(Color.BLACK);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });
    }

    private void showDatePicker_and_setDate(EditText field, int year, int month, int day){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Activity_CreateBooking.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                field.setText(date);
            }
        }, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        datePickerDialog.show();
    }

    private void addOrSubtract_Quantity(EditText editText, String operation, String curr_value){
        curr_value = editText.getText().toString().trim();

        if(curr_value.isEmpty() && operation.equalsIgnoreCase("plus")){
            editText.setText("1");
        }else if (curr_value.equals("0") && operation.equalsIgnoreCase("minus") || curr_value.isEmpty() && operation.equalsIgnoreCase("minus")){
            editText.setText("0");
        }else{
            int intValue = Integer.parseInt(curr_value);
            if(operation.equalsIgnoreCase("plus")){
                intValue++;
                editText.setText(String.valueOf(intValue));
            }else{
                intValue--;
                editText.setText(String.valueOf(intValue));
            }
        }
    }
}
