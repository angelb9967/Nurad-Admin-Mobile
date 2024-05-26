package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Activities.SideMenu.Activity_BookingCalendar;
import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Activity_CreateBooking extends AppCompatActivity {
    private DatabaseReference booking_DBref , rooms_DBref;
    private ArrayList<String> rooms_list;
    private CustomArrayAdapter rooms_adapter;
    private ImageView back_icon, CheckIn_Img, CheckOut_Img, Adult_Plus, Adult_Minus, Child_Plus, Child_Minus;
    private EditText CustomerName_Etxt, PhoneNum_Etxt, CheckIn_Etxt, CheckOut_Etxt, BookingPrice_Etxt, Adult_Etxt, Child_Etxt, Note_Etxt;
    private Button saveBtn;
    private TextView title;
    private Spinner rooms_spinner;
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
        CustomerName_Etxt = findViewById(R.id.CustomerName_Etxt);
        PhoneNum_Etxt = findViewById(R.id.PhoneNumber_Etxt);
        rooms_spinner = findViewById(R.id.Rooms_Spinner);
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
        BookingPrice_Etxt = findViewById(R.id.BookingPrice_Etxt);
        Note_Etxt = findViewById(R.id.Notes_Txt);
        saveBtn = findViewById(R.id.Save_Btn);

        title.setText("Create");
        booking_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        rooms_DBref = FirebaseDatabase.getInstance().getReference("Rooms");

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

        // Prevent keyboard from appearing when EditText is touched
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

        rooms_list = new ArrayList<>();
        rooms_adapter = new CustomArrayAdapter(Activity_CreateBooking.this, R.layout.style_spinner, rooms_list);
        setupSpinner(rooms_spinner);
        rooms_spinner.setAdapter(rooms_adapter);
        Showdata(rooms_DBref, rooms_adapter, rooms_list);

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });

        saveBtn.setOnClickListener(view ->{
            final String customerName = CustomerName_Etxt.getText().toString();
            final String phoneNumber = PhoneNum_Etxt.getText().toString();
            final String checkInDate = CheckIn_Etxt.getText().toString();
            final String checkOutDate = CheckOut_Etxt.getText().toString();
            final String bookingPriceStr = BookingPrice_Etxt.getText().toString();
            final String extraAdultStr = Adult_Etxt.getText().toString();
            final String extraChildStr = Child_Etxt.getText().toString();
            final String note = Note_Etxt.getText().toString();
            final String room = rooms_spinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(customerName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkOutDate) || TextUtils.isEmpty(bookingPriceStr) || TextUtils.isEmpty(extraAdultStr) || TextUtils.isEmpty(extraChildStr) || TextUtils.isEmpty(note) ||rooms_spinner.getSelectedItemPosition() == 0 ){
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            final double bookingPrice = Double.parseDouble(bookingPriceStr);
            final int extraAdult = Integer.parseInt(extraAdultStr);
            final int extraChild = Integer.parseInt(extraChildStr);

            String bookingId = booking_DBref.push().getKey();
            Model_Booking modelBooking = new Model_Booking(customerName, phoneNumber, checkInDate, checkOutDate, bookingPrice, extraAdult, extraChild, note, room);
            booking_DBref.child(bookingId).setValue(modelBooking)
                    .addOnSuccessListener(aVoid -> {
                        CustomerName_Etxt.setText("");
                        PhoneNum_Etxt.setText("");
                        CheckIn_Etxt.setText("");
                        CheckOut_Etxt.setText("");
                        BookingPrice_Etxt.setText("");
                        Adult_Etxt.setText("");
                        Child_Etxt.setText("");
                        Note_Etxt.setText("");
                        rooms_spinner.setSelection(0);
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

    }

    private void setupSpinner(Spinner spinner){
        // Set default selection to "Select Room [Room Spinner]"
        spinner.setSelection(0);
        spinner.post(() -> {
            View selectedView = spinner.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                ((TextView) selectedView).setTextColor(Color.GRAY);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void Showdata(DatabaseReference dbRef, CustomArrayAdapter adapter, ArrayList<String> list){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.add("Select Room");

                for(DataSnapshot item:snapshot.getChildren()){
                    list.add(item.getKey().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
