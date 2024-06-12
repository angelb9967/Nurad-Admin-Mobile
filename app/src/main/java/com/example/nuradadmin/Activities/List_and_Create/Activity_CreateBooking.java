package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.SideMenu.Activity_BookingCalendar;
import com.example.nuradadmin.Adapters.Adapter_AddOn;
import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Model_AddOns;
import com.example.nuradadmin.Models.Model_AddressInfo;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_PaymentInfo;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Activity_CreateBooking extends AppCompatActivity {
    private DatabaseReference booking_DBref, allRoomsRef, recommendedRef,
            contact_DBref, address_DBref, payment_DBref, addOns_DBref;
    private EditText FirstName_Etxt, LastName_Etxt, PhoneNum_Etxt, MobilePhone_Etxt, Email_Etxt,
            Country_Etxt, Address1_Etxt, Address2_Etxt, City_Etxt, ZipPostalCode_Etxt,
            CardNum_Etxt, ExpirationDte_Etxt, CVV_Etxt, NameOnTheCard_Etxt,
            CheckIn_Etxt, CheckOut_Etxt, BookingPrice_Etxt, Adult_Etxt, Child_Etxt, Note_Etxt;
    private ArrayList<String> roomsList;
    private List<Model_AddOns> modelAddOnsList;
    private Adapter_AddOn addOnAdapter;
    private RecyclerView recyclerView;
    private CustomArrayAdapter roomsAdapter, prefixAdapter;
    private ImageView back_icon, ExpirationDte_Img, CheckIn_Img, CheckOut_Img, Adult_Plus, Adult_Minus, Child_Plus, Child_Minus;
    private Button saveBtn;
    private TextView title;
    private Spinner rooms_spinner, prefix_spinner;
    private Calendar calendar;
    private ArrayList<String> prefix_list;
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

        // Initialize Views
        // Contact Information
        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        prefix_spinner = findViewById(R.id.Prefix_Spinner);
        FirstName_Etxt = findViewById(R.id.FirstName_Etxt);
        LastName_Etxt = findViewById(R.id.LastName_Etxt);
        PhoneNum_Etxt = findViewById(R.id.Phone_Etxt);
        MobilePhone_Etxt = findViewById(R.id.MobilePhone_Etxt);
        Email_Etxt = findViewById(R.id.Email_Etxt);
        // Address Information
        Country_Etxt = findViewById(R.id.Country_Etxt);
        Address1_Etxt = findViewById(R.id.Address1_Etxt);
        Address2_Etxt = findViewById(R.id.Address2_Etxt);
        City_Etxt = findViewById(R.id.City_Etxt);
        ZipPostalCode_Etxt = findViewById(R.id.ZipCode_Etxt);
        // Payment Information
        CardNum_Etxt = findViewById(R.id.CardNumber_Etxt);
        ExpirationDte_Etxt = findViewById(R.id.ExpirationDate_Etxt);
        ExpirationDte_Img = findViewById(R.id.ExpirationCalendarPicker_Img);
        CVV_Etxt = findViewById(R.id.CVV_Etxt);
        NameOnTheCard_Etxt = findViewById(R.id.NameOntheCard_Etxt);
        // Booking Dates
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
        recyclerView = findViewById(R.id.recyclerView);
        saveBtn = findViewById(R.id.Save_Btn);

        title.setText("Create");
        Adult_Etxt.setText("1");
        Child_Etxt.setText("0");
        booking_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        allRoomsRef = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommendedRef = FirebaseDatabase.getInstance().getReference("RecommRooms");
        contact_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
        address_DBref = FirebaseDatabase.getInstance().getReference("Address Information");
        payment_DBref = FirebaseDatabase.getInstance().getReference("Payment Information");
        addOns_DBref = FirebaseDatabase.getInstance().getReference("AddOns");

        String[] value = {"Select Prefix", "Mr", "Ms"};
        prefix_list = new ArrayList<>(Arrays.asList(value));
        prefixAdapter = new CustomArrayAdapter(this, R.layout.style_spinner, prefix_list);
        prefix_spinner.setAdapter(prefixAdapter);

        Adult_Plus.setOnClickListener(view -> {
            addOrSubtract_Quantity(Adult_Etxt, "plus", String.valueOf(Adult_Etxt.getText()));
        });

        Adult_Minus.setOnClickListener(view -> {
            String curr_value = Adult_Etxt.getText().toString().trim();
            if (curr_value.equals("0") || curr_value.equals("1") || curr_value.equals("2") || curr_value.isEmpty()) {
                Adult_Etxt.setText("1");
            } else {
                int intValue = Integer.parseInt(curr_value);
                intValue--;
                Adult_Etxt.setText(String.valueOf(intValue));
            }
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
        ExpirationDte_Etxt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker_and_setMonthYear(ExpirationDte_Etxt, year, month, day);
                return true;
            }
            return false;
        });

        ExpirationDte_Img.setOnClickListener(view -> {
            showDatePicker_and_setMonthYear(ExpirationDte_Etxt, year, month, day);
        });

        ExpirationDte_Etxt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDatePicker_and_setMonthYear(ExpirationDte_Etxt, year, month, day);
            }
        });

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

        CheckIn_Etxt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDatePicker_and_setDate(CheckIn_Etxt, year, month, day);
            }
        });

        CheckIn_Etxt.setOnClickListener(view -> {
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

        CheckOut_Etxt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
            }
        });

        CheckOut_Etxt.setOnClickListener(view -> {
            showDatePicker_and_setDate(CheckOut_Etxt, year, month, day);
        });

        modelAddOnsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addOnAdapter = new Adapter_AddOn(Activity_CreateBooking.this, modelAddOnsList);
        recyclerView.setAdapter(addOnAdapter);

        roomsList = new ArrayList<>();
        roomsAdapter = new CustomArrayAdapter(Activity_CreateBooking.this, R.layout.style_spinner, roomsList);
        setupSpinner(rooms_spinner);
        rooms_spinner.setAdapter(roomsAdapter);

        // Show data from both databases
        Showdata(allRoomsRef, roomsAdapter, roomsList);
        Showdata(recommendedRef, roomsAdapter, roomsList);
        ShowAddOns(addOns_DBref, addOnAdapter, modelAddOnsList);

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });

        saveBtn.setOnClickListener(view -> {
            final String prefix = prefix_spinner.getSelectedItem().toString();
            final String firstName = FirstName_Etxt.getText().toString();
            final String lastName = LastName_Etxt.getText().toString();
            final String phone = PhoneNum_Etxt.getText().toString();
            final String mobilePhone = MobilePhone_Etxt.getText().toString();
            final String email = Email_Etxt.getText().toString();

            final String country = Country_Etxt.getText().toString();
            final String address1 = Address1_Etxt.getText().toString();
            final String address2 = Address2_Etxt.getText().toString();
            final String city = City_Etxt.getText().toString();
            final String zipCode = ZipPostalCode_Etxt.getText().toString();

            final String cardNumber = CardNum_Etxt.getText().toString();
            final String expirationDate = ExpirationDte_Etxt.getText().toString();
            final String cvv = CVV_Etxt.getText().toString();
            final String nameOnTheCard = NameOnTheCard_Etxt.getText().toString();

            final String checkInDate = CheckIn_Etxt.getText().toString();
            final String checkOutDate = CheckOut_Etxt.getText().toString();
            final String bookingPriceStr = BookingPrice_Etxt.getText().toString();
            final String AdultStr = Adult_Etxt.getText().toString();
            final String ChildStr = Child_Etxt.getText().toString();
            final String note = Note_Etxt.getText().toString();
            final String room = rooms_spinner.getSelectedItem().toString();

            if (prefix_spinner.getSelectedItemPosition() == 0 || (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(mobilePhone)|| TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(country) || TextUtils.isEmpty(address1) || TextUtils.isEmpty(address2) || TextUtils.isEmpty(city) || TextUtils.isEmpty(zipCode) ||
                    TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(expirationDate) || TextUtils.isEmpty(cvv) || TextUtils.isEmpty(nameOnTheCard) ||
                    TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkOutDate) || TextUtils.isEmpty(bookingPriceStr) || TextUtils.isEmpty(AdultStr) || TextUtils.isEmpty(ChildStr) || TextUtils.isEmpty(note) || rooms_spinner.getSelectedItemPosition() == 0)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (AdultStr.equalsIgnoreCase("0")){
                Toast.makeText(this, "Please indicate at least one adult!", Toast.LENGTH_SHORT).show();
                return;
            }

            final double bookingPrice = Double.parseDouble(bookingPriceStr);
            final int adultNum = Integer.parseInt(AdultStr);
            final int childNum = Integer.parseInt(ChildStr);

            Map<String, String> selectedAddOns = addOnAdapter.getSelectedAddOns();

            String bookingId = booking_DBref.push().getKey();
            String contactId = contact_DBref.push().getKey();
            String addressId = address_DBref.push().getKey();
            String paymentId = payment_DBref.push().getKey();

            Model_Booking modelBooking = new Model_Booking(bookingId, contactId, addressId, paymentId, checkInDate, checkOutDate, bookingPrice, adultNum, childNum, note, room, selectedAddOns);
            saveToBookingFirebase(bookingId, modelBooking);

            Model_ContactInfo modelContactInfo = new Model_ContactInfo(contactId, prefix, firstName, lastName, phone, mobilePhone, email);
            saveToContactInfoFirebase(contactId, modelContactInfo);

            Model_AddressInfo modelAddressInfo = new Model_AddressInfo(addressId, country, address1, address2, city, zipCode);
            saveToAddressInfoFirebase(addressId, modelAddressInfo);

            Model_PaymentInfo modelPaymentInfo = new Model_PaymentInfo(paymentId, cardNumber, expirationDate, cvv, nameOnTheCard);
            saveToPaymentInfoFirebase(paymentId, modelPaymentInfo);
        });
    }

    private void saveToBookingFirebase(String booking_ID, Model_Booking model_booking){
        booking_DBref.child(booking_ID).setValue(model_booking)
                .addOnSuccessListener(aVoid -> {
                    CheckIn_Etxt.setText("");
                    CheckOut_Etxt.setText("");
                    BookingPrice_Etxt.setText("");
                    Adult_Etxt.setText("");
                    Child_Etxt.setText("");
                    Note_Etxt.setText("");
                    rooms_spinner.setSelection(0);
                    addOnAdapter.clearSelectedAddOns();
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveToContactInfoFirebase(String contact_ID, Model_ContactInfo model_contactInfo){
        contact_DBref.child(contact_ID).setValue(model_contactInfo)
                .addOnSuccessListener(aVoid -> {
                    prefix_spinner.setSelection(0);
                    FirstName_Etxt.setText("");
                    LastName_Etxt.setText("");
                    PhoneNum_Etxt.setText("");
                    MobilePhone_Etxt.setText("");
                    Email_Etxt.setText("");
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveToAddressInfoFirebase(String address_ID, Model_AddressInfo model_addressInfo){
        address_DBref.child(address_ID).setValue(model_addressInfo)
                .addOnSuccessListener(aVoid -> {
                    Country_Etxt.setText("");
                    Address1_Etxt.setText("");
                    Address2_Etxt.setText("");
                    City_Etxt.setText("");
                    ZipPostalCode_Etxt.setText("");
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveToPaymentInfoFirebase(String payment_ID, Model_PaymentInfo model_paymentInfo){
        payment_DBref.child(payment_ID).setValue(model_paymentInfo)
                .addOnSuccessListener(aVoid -> {
                    CardNum_Etxt.setText("");
                    ExpirationDte_Etxt.setText("");
                    CVV_Etxt.setText("");
                    NameOnTheCard_Etxt.setText("");
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupSpinner(Spinner spinner) {
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

    private void showDatePicker_and_setDate(EditText field, int year, int month, int day) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Activity_CreateBooking.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                field.setText(date);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        datePickerDialog.show();
    }

    private void showDatePicker_and_setMonthYear(EditText field, int year, int month, int day){
        // Get the current year, month, and day
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // A DatePickerDialog with a custom style to show only the year and month
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Activity_CreateBooking.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    }
                }, year, month, 1);

        if (year == currentYear) {
            // If the selected year is the current year, set the minimum date to the current month
            datePickerDialog.getDatePicker().setMinDate(getTimeInMillis(currentDay, currentMonth, currentYear));
        } else {
            // If the selected year is a future year, set the minimum date to the first day of the year
            datePickerDialog.getDatePicker().setMinDate(getTimeInMillis(1, 0, year));
        }

        // Set the DatePicker mode to show only the year and month
        datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + (year % 100);
                field.setText(date);
            }
        });
    }

    // Helper method to get time in milliseconds for a given date
    private long getTimeInMillis(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void addOrSubtract_Quantity(EditText editText, String operation, String curr_value) {
        curr_value = editText.getText().toString().trim();

        if (curr_value.isEmpty() && operation.equalsIgnoreCase("plus")) {
            editText.setText("2");
        } else if (curr_value.equals("0") && operation.equalsIgnoreCase("minus") || curr_value.isEmpty() && operation.equalsIgnoreCase("minus")) {
            editText.setText("0");
        } else {
            int intValue = Integer.parseInt(curr_value);
            if (operation.equalsIgnoreCase("plus")) {
                intValue++;
                editText.setText(String.valueOf(intValue));
            } else {
                intValue--;
                editText.setText(String.valueOf(intValue));
            }
        }
    }

    private void Showdata(DatabaseReference dbRef, CustomArrayAdapter adapter, ArrayList<String> list) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.isEmpty()) {
                    list.add("Select Room");
                }
                for (DataSnapshot item : snapshot.getChildren()) {
                    list.add(item.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void ShowAddOns(DatabaseReference dbRef, Adapter_AddOn adapter, List<Model_AddOns> list) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear existing data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Model_AddOns addOn = dataSnapshot.getValue(Model_AddOns.class);
                    if (addOn != null) {
                        list.add(addOn);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Log.e("ShowAddOns", "Error: " + error.getMessage());
            }
        });
    }
}
