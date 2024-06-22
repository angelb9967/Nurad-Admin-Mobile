package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.Activity_BookingSummary;
import com.example.nuradadmin.Activities.CheckOutDateValidator;
import com.example.nuradadmin.Activities.FutureDateValidator;
import com.example.nuradadmin.Activities.MyCompositeDateValidator;
import com.example.nuradadmin.Activities.SideMenu.Activity_BookingCalendar;
import com.example.nuradadmin.Activities.UnavailableDateValidator;
import com.example.nuradadmin.Adapters.Adapter_AddOn;
import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Model_AddOns;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.Models.Model_Voucher;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Activity_CreateBooking extends AppCompatActivity {
    private AutoCompleteTextView emailAutoCompleteTextView;
    private String roomName, roomTitle, roomType, priceRule, userUID;
    private ImageView back_icon, ExpirationDte_Img, CheckIn_Img, CheckOut_Img, Adult_Plus, Adult_Minus, Child_Plus, Child_Minus;
    private TextView roomDetailsTextView, roomNameTextView,
            roomTitleTextView, roomTypeTextView, roomPriceTextView,
            voucherPrice, selectedAddOnsPrice,
            adultGuestPrice, childGuestPrice,
            bookStatus, bookSubTotal, bookVatVal,
            checkOutTimeTextView, userUIDTextView;
    private double price, extraChildPrice, extraAdultPrice;
    private Map<Long, Long> unavailableDateRanges = new HashMap<>();
    private DatabaseReference allRoomsRef, recommendedRef, user_DBref,
            addOns_DBref, priceRules_DBref;
    private EditText FirstName_Etxt, LastName_Etxt, PhoneNum_Etxt, MobilePhone_Etxt, Email_Etxt,
            Country_Etxt, Address1_Etxt, Address2_Etxt, ZipPostalCode_Etxt,
            CardNum_Etxt, ExpirationDte_Etxt, CVV_Etxt, NameOnTheCard_Etxt,
            CheckIn_Etxt, CheckOut_Etxt, Adult_Etxt, Child_Etxt, Note_Etxt,
            voucherEditText;
    private Spinner rooms_spinner, prefix_spinner, city_spinner, region_spinner, checkInTimeSpinner;
    private static final Map<String, List<String>> cities = new HashMap<>();
    private CustomArrayAdapter roomsAdapter, prefixAdapter;
    private CheckBox applyVoucherCheckBox;
    private List<Model_AddOns> modelAddOnsList;
    private ArrayList<String> roomsList;
    private ArrayList<String> prefix_list;
    private List<String> availableCheckInTimes;
    private Adapter_AddOn addOnAdapter;
    private RecyclerView recyclerView;
    private Button nextBtn;
    private TextView title;
    private Calendar checkInDateCalendar, checkOutDateCalendar;
    private String checkInDate;
    private String checkOutDate;
    private String status = "Booked";
    private boolean isVoucherValid = false;
    private boolean isVoucherChecked = false;

    //for region and city spinner
    static {
        cities.put("ARMM", Arrays.asList("Lamitan City", "Marawi City"));
        cities.put("CAR", Arrays.asList("Baguio City", "Tabuk City"));
        cities.put("NCR", Arrays.asList(
                "Caloocan City", "Las Piñas City", "Makati City", "Malabon City",
                "Mandaluyong City", "Manila City", "Marikina City", "Muntinlupa City",
                "Navotas City", "Parañaque City", "Pasay City", "Pasig City", "Quezon City",
                "San Juan City", "Taguig City", "Valenzuela City"
        ));
        cities.put("Region 1", Arrays.asList(
                "Batac City", "Laoag City", "Candon City", "Vigan City", "San Fernando City",
                "Alaminos City", "Dagupan City", "San Carlos City", "Urdaneta City"
        ));
        cities.put("Region 2", Arrays.asList(
                "Tuguegarao City", "Cauayan City", "Ilagan City", "Santiago City"
        ));
        cities.put("Region 3", Arrays.asList(
                "Balanga City", "Malolos City", "Meycauayan City", "San Jose del Monte City",
                "Cabanatuan City", "Gapan City", "Muñoz City", "Palayan City", "Angeles City",
                "Mabalacat City", "San Fernando City", "Tarlac City", "Olongapo City",
                "San Jose City"
        ));
        cities.put("Region 4A", Arrays.asList(
                "Batangas City", "Lipa City", "Tanauan City", "Bacoor City", "Cavite City",
                "Dasmariñas City", "Imus City", "Tagaytay City", "Trece Martires City",
                "Biñan City", "Cabuyao City", "San Pablo City", "Santa Rosa City",
                "Lucena City", "Tayabas City", "Antipolo City", "Calamba City"
        ));
        cities.put("Region 4B", Arrays.asList("Calapan City", "Puerto Princesa City"));
        cities.put("Region 5", Arrays.asList(
                "Legazpi City", "Ligao City", "Tabaco City", "Iriga City", "Naga City",
                "Masbate City", "Sorsogon City"
        ));
        cities.put("Region 6", Arrays.asList(
                "Roxas City", "Iloilo City", "Passi City", "Bacolod City", "Bago City",
                "Cadiz City", "Escalante City", "Himamaylan City", "Kabankalan City",
                "La Carlota City", "Sagay City", "San Carlos City", "Silay City", "Sipalay City",
                "Talisay City", "Victorias City"
        ));
        cities.put("Region 7", Arrays.asList(
                "Tagbilaran City", "Bogo City", "Carcar City", "Cebu City", "Danao City",
                "Lapu-Lapu City", "Mandaue City", "Naga City", "Talisay City", "Bais City",
                "Bayawan City", "Canlaon City", "Dumaguete City", "Guihulngan City",
                "Tanjay City", "Toledo City"
        ));
        cities.put("Region 8", Arrays.asList(
                "Borongan City", "Baybay City", "Ormoc City", "Tacloban City", "Calbayog City",
                "Catbalogan City", "Maasin City"
        ));
        cities.put("Region 9", Arrays.asList(
                "Isabela City", "Dapitan City", "Dipolog City", "Pagadian City", "Zamboanga City"
        ));
        cities.put("Region 10", Arrays.asList(
                "Malaybalay City", "Valencia City", "Iligan City", "Oroquieta City", "Ozamiz City",
                "Tangub City", "Cagayan de Oro City", "El Salvador City", "Gingoog City"
        ));
        cities.put("Region 11", Arrays.asList(
                "Panabo City", "Samal City", "Tagum City", "Davao City", "Digos City", "Mati City"
        ));
        cities.put("Region 12", Arrays.asList(
                "Kidapawan City", "Cotabato City", "General Santos City", "Koronadal City",
                "Tacurong City"
        ));
        cities.put("Region 13", Arrays.asList(
                "Butuan City", "Cabadbaran City", "Bayugan City", "Surigao City", "Bislig City",
                "Tandag City"
        ));
    }

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

        initializeViews();
        setDatabaseReferences();
        fetchAddOnsFromDatabase();
        displayAddOns(new ArrayList<>());
        populateSpinners();

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });

        setupEventListeners();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs() && (!isVoucherChecked || (isVoucherChecked && isVoucherValid))) {
                    // All conditions including voucher validation are met
                    continueBookingProcess();
                } else {
                    // Handle invalid inputs, such as displaying error messages or alerts
                    Toast.makeText( Activity_CreateBooking.this, "Please ensure all fields are filled correctly and voucher code is valid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        emailAutoCompleteTextView = findViewById(R.id.emailAutoCompleteTextView);
        userUIDTextView = findViewById(R.id.UserUID_TxtView);
        userUIDTextView.setText("UID not found");
        user_DBref = FirebaseDatabase.getInstance().getReference("Users");
        emailAutoCompleteTextView.setThreshold(1);
        emailAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userUIDTextView.setText("UID not found");
                userUIDTextView.setTextColor(ContextCompat.getColor(Activity_CreateBooking.this, R.color.red));
                if (s.length() >= 1) {
                    getUserUid(s.toString()); // Fetch UID immediately for the current input
                    fetchEmailSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        emailAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String email = (String) parent.getItemAtPosition(position);
            getUserUid(email);
        });

    }

    private void fetchEmailSuggestions(String query) {
        user_DBref.orderByChild("email").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> emails = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    if (email != null) {
                        emails.add(email);
                    }
                }
                updateEmailSuggestions(emails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Activity_CreateBooking.this, "Error fetching emails", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmailSuggestions(List<String> emails) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, emails);
        emailAutoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getUserUid(String email) {
        user_DBref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uid = snapshot.getKey();
                        if (uid != null) {
                            userUIDTextView.setText("UID found");
                            userUID = uid;
                            userUIDTextView.setTextColor(ContextCompat.getColor(Activity_CreateBooking.this, R.color.green));
                            Log.d("Activity_CreateBooking", "User UID: " + uid);
                        } else {
                            userUID = "";
                            userUIDTextView.setText("UID not found");
                            userUIDTextView.setTextColor(ContextCompat.getColor(Activity_CreateBooking.this, R.color.red));
                            Log.e("Activity_CreateBooking", "UID not found");
                        }
                    }
                } else {
                    Log.e("Activity_CreateBooking", "Email not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Activity_CreateBooking", "Error fetching UID");
            }
        });
    }

    private void initializeViews(){
        rooms_spinner = findViewById(R.id.Rooms_Spinner);

        // Room Details
        roomTitleTextView = findViewById(R.id.roomTitle);
        roomDetailsTextView = findViewById(R.id.roomDetailsTextView);
        roomNameTextView = findViewById(R.id.roomNameTextView);
        roomTypeTextView = findViewById(R.id.roomTypeTextView);
        roomPriceTextView = findViewById(R.id.roomPriceTextView);
        availableCheckInTimes = new ArrayList<>();
        nextBtn = findViewById(R.id.NextStep_Btn);

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
        city_spinner = findViewById(R.id.City_Spinner);
        region_spinner = findViewById(R.id.Region_Spinner);
        ZipPostalCode_Etxt = findViewById(R.id.ZipCode_Etxt);

        // Payment Information
        CardNum_Etxt = findViewById(R.id.CardNumber_Etxt);
        ExpirationDte_Etxt = findViewById(R.id.ExpirationDate_Etxt);
        ExpirationDte_Img = findViewById(R.id.ExpirationCalendarPicker_Img);
        CVV_Etxt = findViewById(R.id.CVV_Etxt);
        NameOnTheCard_Etxt = findViewById(R.id.NameOntheCard_Etxt);

        //date and time
        CheckIn_Etxt = findViewById(R.id.CheckInDate_Etxt);
        CheckOut_Etxt = findViewById(R.id.CheckOutDate_Etxt);
        CheckIn_Img = findViewById(R.id.CalendarPicker_In_Img);
        CheckOut_Img = findViewById(R.id.CalendarPicker_Out_Img);
        checkInTimeSpinner = findViewById(R.id.checkInTimeSpinner);
        checkOutTimeTextView = findViewById(R.id.checkTimeTextView);

        // Guest Inclusion
        childGuestPrice = findViewById(R.id.childGuestPrice);
        adultGuestPrice = findViewById(R.id.adultGuestPrice);
        Adult_Etxt = findViewById(R.id.Adult_Etxt);
        Adult_Plus = findViewById(R.id.Adult_Plus_Img);
        Adult_Minus = findViewById(R.id.Adult_Minus_Img);
        Child_Etxt = findViewById(R.id.Child_Etxt);
        Child_Plus = findViewById(R.id.Child_Plus_Img);
        Child_Minus = findViewById(R.id.Child_Minus_Img);
        Note_Etxt = findViewById(R.id.Notes_Txt);
        recyclerView = findViewById(R.id.recyclerView);

        title.setText("Create");

        // Set initial values
        Adult_Etxt.setText("1");
        Child_Etxt.setText("0");

        // Voucher
        voucherPrice = findViewById(R.id.voucherPrice);
        applyVoucherCheckBox = findViewById(R.id.applyVoucherCheckBox);
        voucherEditText = findViewById(R.id.voucherEditText);

        bookStatus = findViewById(R.id.bookStatus);
        bookSubTotal = findViewById(R.id.bookSubTotal);
        bookVatVal = findViewById(R.id.bookVatVal);

        // Addon
        selectedAddOnsPrice = findViewById(R.id.selectedAddOnsPrice);

        Note_Etxt = findViewById(R.id.Notes_Txt);

        Country_Etxt.setText("Philippines");
        Country_Etxt.setEnabled(false);
    }

    private void setDatabaseReferences(){
        allRoomsRef = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommendedRef = FirebaseDatabase.getInstance().getReference("RecommRooms");
        addOns_DBref = FirebaseDatabase.getInstance().getReference("AddOns");
        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
    }

    private void populateSpinners(){
        prefix_list = new ArrayList<>(Arrays.asList("Select Prefix", "Mr", "Ms", "Mrs"));
        prefixAdapter = new CustomArrayAdapter(this, R.layout.style_spinner, prefix_list);
        prefix_spinner.setAdapter(prefixAdapter);

        roomsList = new ArrayList<>();
        roomsAdapter = new CustomArrayAdapter(Activity_CreateBooking.this, R.layout.style_spinner, roomsList);
        setupSpinner(rooms_spinner);
        rooms_spinner.setAdapter(roomsAdapter);

        // Show data from both databases
        Showdata(allRoomsRef, roomsAdapter, roomsList);
        Showdata(recommendedRef, roomsAdapter, roomsList);

        List<String> regions = Arrays.asList(
                "ARMM",
                "CAR",
                "NCR",
                "Region 1",
                "Region 2",
                "Region 3",
                "Region 4A",
                "Region 4B",
                "Region 5",
                "Region 6",
                "Region 7",
                "Region 8",
                "Region 9",
                "Region 10",
                "Region 11",
                "Region 12",
                "Region 13"
        );

        // Initialize region spinner
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, R.layout.style_spinner, new ArrayList<>(cities.keySet()));
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region_spinner.setAdapter(regionAdapter);

        // Initialize city spinner with empty list, will be updated based on selected region
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.style_spinner, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city_spinner.setAdapter(cityAdapter);

        // Set up region spinner listener to update city spinner
        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                List<String> citiesList = cities.get(selectedRegion);
                if (citiesList != null) {
                    // Clear the existing city list
                    cityAdapter.clear();
                    // Add the cities for the selected region
                    cityAdapter.addAll(citiesList);
                    // Notify the adapter that the data set has changed
                    cityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Clear the existing city list when no region is selected
                cityAdapter.clear();
                // Notify the adapter that the data set has changed
                cityAdapter.notifyDataSetChanged();
            }
        });
    }

    private void populateRoomDetails(){

    }

    private void fetchAddOnsFromDatabase() {
        addOns_DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Model_AddOns> addOnList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Model_AddOns addOn = dataSnapshot.getValue(Model_AddOns.class);
                    if (addOn != null) {
                        addOnList.add(addOn);
                    }
                }
                displayAddOns(addOnList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(Activity_CreateBooking.this, "Could Not Fetch the Add Ons", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAddOns(List<Model_AddOns> addOnList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addOnAdapter = new Adapter_AddOn(this, addOnList, new Adapter_AddOn.OnAddOnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(double totalPrice) {
                selectedAddOnsPrice.setText(formatPrice(totalPrice));
                updateSubtotalAndVat();
            }
        });
        recyclerView.setAdapter(addOnAdapter);
    }

    private void updateSubtotalAndVat() {
        double roomPrice = parsePrice(roomPriceTextView.getText().toString());
        double totalChildPrice = parsePrice(childGuestPrice.getText().toString());
        double totalAdultPrice = parsePrice(adultGuestPrice.getText().toString());
        double addOnsPrice = parsePrice(selectedAddOnsPrice.getText().toString());

        double subtotal = roomPrice + totalChildPrice + totalAdultPrice + addOnsPrice;
        double vatVal = subtotal * 0.12;

        bookSubTotal.setText(formatPrice(subtotal));
        bookVatVal.setText(formatPrice(vatVal));

        // Optionally update visibility
        bookSubTotal.setVisibility(View.VISIBLE);
        bookVatVal.setVisibility(View.VISIBLE);
    }

    private double parsePrice(String priceText) {
        // Remove everything except numbers and decimal point
        String price = priceText.replaceAll("[^\\d.]", "");
        return price.isEmpty() ? 0.0 : Double.parseDouble(price);
    }

    //fetching price of room
    private void fetchPriceRule(String priceRuleName) {
        priceRules_DBref.child(priceRuleName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_PriceRule priceRule = snapshot.getValue(Model_PriceRule.class);
                    if (priceRule != null) {
                        // Get the appropriate price based on the current day of the week
                        double dailyPrice = getPriceForCurrentDay(priceRule);
                        price = dailyPrice;
                        extraChildPrice = priceRule.getExtraChild_price();
                        extraAdultPrice = priceRule.getExtraAdult_price();
                        updateRoomPrice();
                        // Display the extra child and adult prices
                        updateGuestPrices();
                    } else {
                        // Handle null price rule scenario
                        setDefaultPrices();
                    }
                } else {
                    // Handle non-existent price rule scenario
                    setDefaultPrices();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error scenario
                setDefaultPrices();
            }
        });
    }

    private void setDefaultPrices() {
        price = 0;
        extraAdultPrice = 0;
        extraChildPrice = 0;
        updateRoomPrice();
        updateGuestPrices();
    }

    private void updateGuestPrices() {
        int childCount = Integer.parseInt(Child_Etxt.getText().toString());
        int adultCount = Integer.parseInt(Adult_Etxt.getText().toString());

        double totalChildPrice = childCount * extraChildPrice;
        double totalAdultPrice = adultCount * extraAdultPrice;

        childGuestPrice.setText(formatPrice(totalChildPrice));
        adultGuestPrice.setText(formatPrice(totalAdultPrice));

        updateSubtotalAndVat();
    }

    private void updateRoomPrice() {
        if (checkInDateCalendar != null && checkOutDateCalendar != null) {
            long diffInMillis = checkOutDateCalendar.getTimeInMillis() - checkInDateCalendar.getTimeInMillis();
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            double totalPrice = price * days;
            roomPriceTextView.setText(formatPrice(totalPrice));
        } else {
            roomPriceTextView.setText(formatPrice(price));
        }
        updateSubtotalAndVat();
    }

    private void setupEventListeners() {
        applyVoucherCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CompoundButtonCompat.setButtonTintList(applyVoucherCheckBox, ColorStateList.valueOf(Color.parseColor("#882065")));
                    voucherEditText.setVisibility(View.VISIBLE);
                    isVoucherChecked = true;
                } else {
                    CompoundButtonCompat.setButtonTintList(applyVoucherCheckBox, ColorStateList.valueOf(Color.parseColor("#000000")));
                    voucherEditText.setVisibility(View.GONE);
                    isVoucherChecked = false;
                    voucherPrice.setText("No Voucher Selected"); // Clear voucher value display
                }
            }
        });

        setupExpirationDatePicker();
        setupDatePickers();
        setupTimeSpinner();
        setupGuestControls();
    }

    private void setupExpirationDatePicker() {
        ExpirationDte_Img.setOnClickListener(v -> showDatePicker_and_setMonthYear(ExpirationDte_Etxt));
    }

    private void showDatePicker_and_setMonthYear(EditText field) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                (view, year, month, dayOfMonth) -> {
                    month = month + 1;
                    String date = month + "/" + (year % 100);
                    field.setText(date);
                }, currentYear, currentMonth, 1);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void setupDatePickers() {
        CheckIn_Img.setOnClickListener(v -> showDatePicker(CheckIn_Etxt, true));
        CheckOut_Img.setOnClickListener(v -> {
            if (checkInDateCalendar != null) {
                showDatePicker(CheckOut_Etxt, false);
            } else {
                Toast.makeText(Activity_CreateBooking.this, "Please select a check-in date first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker(EditText field, boolean isCheckIn) {
        Calendar calendar = Calendar.getInstance();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(calendar.getTimeInMillis());
        builder.setTitleText(isCheckIn ? "Select Check-In Date" : "Select Check-Out Date");

        // Define constraints
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Create a list of validators
        List<CalendarConstraints.DateValidator> validators = new ArrayList<>();
        validators.add(new FutureDateValidator());  // Allow only future dates
        validators.add(new UnavailableDateValidator(unavailableDateRanges));  // Add the custom UnavailableDateValidator

        if (!isCheckIn && checkInDateCalendar != null) {
            validators.add(new CheckOutDateValidator(checkInDateCalendar.getTimeInMillis()));  // Ensure check-out date is after check-in date
        }

        MyCompositeDateValidator compositeValidator = new MyCompositeDateValidator(validators);
        constraintsBuilder.setValidator(compositeValidator);

        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            String selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
            field.setText(selectedDate);

            if (isCheckIn) {
                checkInDateCalendar = calendar;
                checkInDate = selectedDate;
                CheckOut_Etxt.setText(""); // Clear check-out date if check-in date changes
                checkOutDate = null;
            } else {
                checkOutDateCalendar = calendar;
                checkOutDate = selectedDate;
            }

            // Only check room availability if both dates are set
            if (checkInDate != null && checkOutDate != null) {
                checkRoomAvailability();
            }

            updateRoomPrice();
        });

        // Fetch unavailable dates before showing the date picker
        fetchUnavailableDates(() -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"), isCheckIn);
    }

    private void fetchUnavailableDates(Runnable onSuccess, boolean isCheckIn) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bookingsRef = database.getReference("Booking");

        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unavailableDateRanges.clear();
                String selectedRoomName = roomNameTextView.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                    Model_Booking booking = bookingSnapshot.getValue(Model_Booking.class);
                    if (booking != null && selectedRoomName.equals(booking.getRoom())) {
                        try {
                            Date existingCheckIn = sdf.parse(booking.getCheckInDate());
                            Date existingCheckOut = sdf.parse(booking.getCheckOutDate());
                            unavailableDateRanges.put(existingCheckIn.getTime(), existingCheckOut.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                onSuccess.run(); // Run the success callback
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void checkRoomAvailability() {
        if (checkInDate == null || checkOutDate == null) {
            return; // Avoid null pointer exceptions
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date newCheckIn = sdf.parse(checkInDate);
            Date newCheckOut = sdf.parse(checkOutDate);

            for (Map.Entry<Long, Long> entry : unavailableDateRanges.entrySet()) {
                Date existingCheckIn = new Date(entry.getKey());
                Date existingCheckOut = new Date(entry.getValue());

                if ((newCheckIn.before(existingCheckOut) && newCheckOut.after(existingCheckIn))) {
                    Toast.makeText(Activity_CreateBooking.this, "Room is not available for the selected dates", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(Activity_CreateBooking.this, "Room is available", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupTimeSpinner() {
        List<String> timeSlots = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int hour = i % 12;
            if (hour == 0) hour = 12;
            String amPm = (i < 12) ? "AM" : "PM";
            String timeSlot = String.format("%02d:00 %s", hour, amPm);
            timeSlots.add(timeSlot);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.style_spinner, timeSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkInTimeSpinner.setAdapter(adapter);

        checkInTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTime = parent.getItemAtPosition(position).toString();
                checkOutTimeTextView.setText(selectedTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                checkOutTimeTextView.setText("Not set");
            }
        });
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
                        setDefaultPrices();;
                        roomName = "";
                        roomTitle = "";
                        roomType = "";
                        priceRule = "";

                        roomTitleTextView.setText("Room Title");
                        roomNameTextView.setText("Room Name");
                        roomDetailsTextView.setText("");
                        roomTypeTextView.setText("Room Type");
                        roomPriceTextView.setText("₱ 0.00");
                        Toast.makeText(Activity_CreateBooking.this, "Please make sure to choose a room before proceeding.", Toast.LENGTH_SHORT).show();
                    } else {
                        ((TextView) view).setTextColor(Color.BLACK);
                        fetchRoomDetails(value);
                        roomName = value;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void fetchRoomDetails(String roomName){
        allRoomsRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_Room modelRoom = snapshot.getValue(Model_Room.class);
                    if (modelRoom != null) {
                        roomTitle = modelRoom.getTitle();
                        roomType = modelRoom.getRoomType();
                        priceRule = modelRoom.getPriceRule();

                        roomTitleTextView.setText(roomTitle);
                        roomNameTextView.setText("Room Number: " + roomName + "\n");
                        roomTypeTextView.setText("Room Type: " + roomType + "\n");
                        roomDetailsTextView.setText("Description: " + modelRoom.getDescription() + "\n");
                        fetchPriceRule(modelRoom.getPriceRule());
                    }
                } else {
                    recommendedRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Model_Room modelRoom = snapshot.getValue(Model_Room.class);
                                if (modelRoom != null) {
                                    roomTitle = modelRoom.getTitle();
                                    roomType = modelRoom.getRoomType();
                                    priceRule = modelRoom.getPriceRule();

                                    roomTitleTextView.setText(roomTitle);
                                    roomNameTextView.setText("Room Number: " + roomName + "\n");
                                    roomTypeTextView.setText("Room Type: " + roomType + "\n");
                                    roomDetailsTextView.setText("Description: " + modelRoom.getDescription() + "\n");
                                    fetchPriceRule(modelRoom.getPriceRule());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Activity_CreateBooking", "Firebase: Retrieval of Room Details cancelled", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Activity_CreateBooking", "Firebase: Retrieval of Room Details cancelled", error.toException());
            }
        });
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

    //VALIDATIONS
    //getting price of the day
    private double getPriceForCurrentDay(Model_PriceRule priceRule) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.FRIDAY:
                return priceRule.getFriday_price();
            case Calendar.SATURDAY:
                return priceRule.getSaturday_price();
            case Calendar.SUNDAY:
                return priceRule.getSunday_price();
            default:
                return priceRule.getPrice();
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }

    private boolean isValidPhilippineMobileNumber(String mobileNumber) {
        // Philippine mobile number format: 09XX-XXX-YYYY
        return mobileNumber.matches("^(09|\\+639)\\d{2}\\d{3}\\d{4}$");
    }

    private boolean isValidPhilippineLandlineNumber(String landlineNumber) {
        // Remove hyphens from the landline number
        String formattedLandlineNumber = landlineNumber.replaceAll("-", "");

        // Check if the landline number is empty or matches the Philippine landline number formats
        return formattedLandlineNumber.isEmpty() || formattedLandlineNumber.matches("^(\\d{1,2}\\d{7}|\\d{8})$");
    }

    private boolean isValidPhilippinePostalCode(String zipCode) {
        // Check if the length of the zip code is exactly 4 characters
        if (zipCode.length() != 4) {
            return false;
        }

        // Check if the zip code consists of four digits
        for (char c : zipCode.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private void setupGuestControls() {
        Adult_Plus.setOnClickListener(v -> changeGuestCount(Adult_Etxt, 1, 1, 20));
        Adult_Minus.setOnClickListener(v -> changeGuestCount(Adult_Etxt, -1, 1, 20));

        Child_Plus.setOnClickListener(v -> changeGuestCount(Child_Etxt, 1, 0, 20));
        Child_Minus.setOnClickListener(v -> changeGuestCount(Child_Etxt, -1, 0, 20));
    }

    private void changeGuestCount(EditText editText, int change, int min, int max) {
        int currentCount = Integer.parseInt(editText.getText().toString());
        int newCount = currentCount + change;

        if (newCount >= min && newCount <= max) {
            editText.setText(String.valueOf(newCount));
            updateGuestPrices(); // Update guest prices after changing the count
        } else if (newCount < min) {
            Toast.makeText(this, "Value cannot be lower than " + min, Toast.LENGTH_SHORT).show();
        } else if (newCount > max) {
            Toast.makeText(this, "Value cannot be higher than " + max, Toast.LENGTH_SHORT).show();
        }
    }

    private void validateVoucherCode(final String code, final OnVoucherValidationListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference vouchersRef = database.getReference("Vouchers");

        // Fetch user UID
        String checkUserUID = (String) userUIDTextView.getText();
        if (checkUserUID == null || checkUserUID.equalsIgnoreCase("UID not found")) {
            emailAutoCompleteTextView.setError("Enter a valid email account");
            emailAutoCompleteTextView.requestFocus();
            return;
        }

        // Check if the voucher code exists in Vouchers node
        vouchersRef.orderByChild("code").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean voucherFound = false;
                double voucherValue = 0;

                for (DataSnapshot voucherSnapshot : dataSnapshot.getChildren()) {
                    Model_Voucher voucher = voucherSnapshot.getValue(Model_Voucher.class);
                    if (voucher != null) {
                        voucherFound = true;
                        voucherValue = voucher.getValue();
                        break;
                    }
                }

                if (voucherFound) {
                    // Check if the voucher code is already used by the user in UserVouchers node
                    DatabaseReference userVouchersRef = database.getReference("UserVouchers").child(userUID).child(code);
                    double finalVoucherValue = voucherValue;
                    userVouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Voucher code is already used by the user
                                listener.onValidationFailure();
                            } else {
                                // Voucher code is valid and not used by the user
                                listener.onValidationSuccess(finalVoucherValue);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            listener.onValidationError();
                        }
                    });
                } else {
                    listener.onValidationFailure();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onValidationError();
            }
        });
    }

    private boolean validateInputs() {

        // we need to make sure subtotal, vat, and status is also included in this
        String AddOnsPrice = selectedAddOnsPrice.getText().toString().trim();
        String voucherValue = voucherPrice.getText().toString().trim();
        Map<String, Model_AddOns> selectedAddOns = addOnAdapter.getSelectedAddOns();
        String roomPrice = roomPriceTextView.getText().toString().trim();
        String extraAdultPrice = adultGuestPrice.getText().toString().trim();
        String extraChildPrice = childGuestPrice.getText().toString().trim();
        String country = Country_Etxt.getText().toString().trim();
        String prefix = prefix_spinner.getSelectedItem().toString();
        String checkInDate = CheckIn_Etxt.getText().toString().trim();
        String checkOutDate = CheckOut_Etxt.getText().toString().trim();
        String checkInTime = checkInTimeSpinner.getSelectedItem().toString();
        String checkOutTime = checkInTimeSpinner.getSelectedItem().toString();
        String room = roomName;
        String roomTitle1 = roomTitle;
        String adultCount = Adult_Etxt.getText().toString();
        String childCount = Child_Etxt.getText().toString();
        String firstName = FirstName_Etxt.getText().toString().trim();
        String lastName = LastName_Etxt.getText().toString().trim();
        String phone = PhoneNum_Etxt.getText().toString().trim();
        String mobilePhone = MobilePhone_Etxt.getText().toString().trim();
        String email = Email_Etxt.getText().toString().trim();
        String address1 = Address1_Etxt.getText().toString().trim();
        String address2 = Address2_Etxt.getText().toString().trim();
        String selectedRegion = region_spinner.getSelectedItem().toString();
        String selectedCity = city_spinner.getSelectedItem().toString();
        String zipCode = ZipPostalCode_Etxt.getText().toString().trim();
        String cardNumber = CardNum_Etxt.getText().toString().trim();
        String expirationDate = ExpirationDte_Etxt.getText().toString().trim();
        String cvv = CVV_Etxt.getText().toString().trim();
        String nameOnCard = NameOnTheCard_Etxt.getText().toString().trim();
        String notes = Note_Etxt.getText().toString().trim();
        String uid = userUIDTextView.getText().toString().trim();

        if(rooms_spinner.getSelectedItem() == null || rooms_spinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (prefix_spinner.getSelectedItem() == null || prefix_spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a prefix", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (firstName.isEmpty()) {
            FirstName_Etxt.setError("First name is required");
            FirstName_Etxt.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            LastName_Etxt.setError("Last name is required");
            LastName_Etxt.requestFocus();
            return false;
        }

        // Set landline number to "N/A" if empty
        if (phone.isEmpty()) {
            phone = "N/A";
        } else {
            // Validate Philippine landline phone number
            if (!isValidPhilippineLandlineNumber(phone)) {
                PhoneNum_Etxt.setError("Invalid Philippine landline phone number");
                PhoneNum_Etxt.requestFocus();
                return false;
            }
        }

        if (mobilePhone.isEmpty() && phone.equals("N/A")) {
            MobilePhone_Etxt.setError("At least one phone number is required");
            MobilePhone_Etxt.requestFocus();
            return false;
        } else {
            // Validate Philippine mobile phone number
            if (!mobilePhone.isEmpty() && !isValidPhilippineMobileNumber(mobilePhone)) {
                MobilePhone_Etxt.setError("Invalid Philippine mobile phone number");
                MobilePhone_Etxt.requestFocus();
                return false;
            }
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email_Etxt.setError("Invalid email address");
            Email_Etxt.requestFocus();
            return false;
        }

        if (address1.isEmpty()) {
            Address1_Etxt.setError("Address 1 is required");
            Address1_Etxt.requestFocus();
            return false;
        }

        if (address2.isEmpty()) {
            Address2_Etxt.setText("N/A");
        }

        if (selectedRegion.isEmpty()) {
            Toast.makeText(this, "Please select a region", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCity.isEmpty()) {
            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zipCode.isEmpty() || !isValidPhilippinePostalCode(zipCode)) {
            ZipPostalCode_Etxt.setError("Invalid Zip/Postal Code");
            ZipPostalCode_Etxt.requestFocus();
            return false;
        }

        if (cardNumber.isEmpty() || cardNumber.length() != 16) {
            CardNum_Etxt.setError("Card number must be 16 digits");
            CardNum_Etxt.requestFocus();
            return false;
        }

        if (expirationDate.isEmpty()) {
            ExpirationDte_Etxt.setError("Expiration date is required");
            ExpirationDte_Etxt.requestFocus();
            return false;
        }

        if (cvv.isEmpty() || cvv.length() != 3) {
            CVV_Etxt.setError("CVV must be 3 digits");
            CVV_Etxt.requestFocus();
            return false;
        }

        if (uid == null || uid.equalsIgnoreCase("") || uid.equalsIgnoreCase("UID not found")) {
            emailAutoCompleteTextView.setError("Enter a valid Nurad email account");
            emailAutoCompleteTextView.requestFocus();
            return false;
        }

        if (nameOnCard.isEmpty()) {
            NameOnTheCard_Etxt.setError("Name on the card is required");
            NameOnTheCard_Etxt.requestFocus();
            return false;
        }

        if (notes.isEmpty()) {
            notes = "No Note";
        } else if (notes.split("\\s+").length > 150) {
            Note_Etxt.setError("Notes cannot exceed 250 words");
            Note_Etxt.requestFocus();
            return false;
        }

        if (isVoucherChecked) {
            String voucherCode = voucherEditText.getText().toString().trim();
            if (voucherCode.isEmpty()) {
                voucherEditText.setError("Voucher code is required");
                voucherEditText.requestFocus();
                return false;
            } else {
                validateVoucherCode(voucherCode, new OnVoucherValidationListener() {
                    @Override
                    public void onValidationSuccess(double voucherValue) {
                        voucherPrice.setText(formatPrice(voucherValue));
                        isVoucherValid = true;
                        continueBookingProcess();
                    }

                    @Override
                    public void onValidationFailure() {
                        voucherEditText.setError("Invalid voucher code. Please try again.");
                        voucherEditText.requestFocus();
                        isVoucherValid = false;
                    }

                    @Override
                    public void onValidationError() {
                        Toast.makeText(Activity_CreateBooking.this, "Error checking voucher code. Please try again.", Toast.LENGTH_SHORT).show();
                        isVoucherValid = false;
                    }
                });

                return false;
            }
        } else {
            double defaultVoucherValue = 0.0;
            voucherPrice.setText(formatPrice(defaultVoucherValue));
            voucherEditText.setText("N/A");
            continueBookingProcess();
        }
        return true;
    }

    private void continueBookingProcess() {
        Intent intent = new Intent(this, Activity_BookingSummary.class);

        intent.putExtra("addOnsPrice", selectedAddOnsPrice.getText().toString().trim());
        intent.putExtra("voucherValue", voucherPrice.getText().toString().trim());
        intent.putExtra("roomPrice", roomPriceTextView.getText().toString().trim());
        intent.putExtra("extraAdultPrice", adultGuestPrice.getText().toString().trim());
        intent.putExtra("extraChildPrice", childGuestPrice.getText().toString().trim());
        intent.putExtra("subtotal", bookSubTotal.getText().toString().trim());
        intent.putExtra("vat", bookVatVal.getText().toString().trim());
        intent.putExtra("status", status);
        intent.putExtra("country", Country_Etxt.getText().toString().trim());
        intent.putExtra("prefix", prefix_spinner.getSelectedItem().toString());
        intent.putExtra("checkInDate", CheckIn_Etxt.getText().toString().trim());
        intent.putExtra("checkOutDate", CheckOut_Etxt.getText().toString().trim());
        intent.putExtra("checkInTime", checkInTimeSpinner.getSelectedItem().toString());
        intent.putExtra("checkOutTime", checkInTimeSpinner.getSelectedItem().toString());
        intent.putExtra("room", rooms_spinner.getSelectedItem().toString());
        intent.putExtra("roomTitle", roomTitleTextView.getText().toString().trim());
        intent.putExtra("adultCount", Adult_Etxt.getText().toString());
        intent.putExtra("childCount", Adult_Etxt.getText().toString());
        intent.putExtra("firstName", FirstName_Etxt.getText().toString().trim());
        intent.putExtra("lastName", LastName_Etxt.getText().toString().trim());
        intent.putExtra("phone", PhoneNum_Etxt.getText().toString().trim());
        intent.putExtra("mobilePhone", MobilePhone_Etxt.getText().toString().trim());
        intent.putExtra("email", Email_Etxt.getText().toString().trim());
        intent.putExtra("address1", Address1_Etxt.getText().toString().trim());
        intent.putExtra("address2", Address2_Etxt.getText().toString().trim());
        intent.putExtra("selectedRegion", region_spinner.getSelectedItem().toString());
        intent.putExtra("selectedCity", city_spinner.getSelectedItem().toString());
        intent.putExtra("zipCode", ZipPostalCode_Etxt.getText().toString().trim());
        intent.putExtra("cardNumber", CardNum_Etxt.getText().toString().trim());
        intent.putExtra("expirationDate", ExpirationDte_Etxt.getText().toString().trim());
        intent.putExtra("cvv", CVV_Etxt.getText().toString().trim());
        intent.putExtra("nameOnCard", NameOnTheCard_Etxt.getText().toString().trim());
        intent.putExtra("notes", Note_Etxt.getText().toString().trim());
        intent.putExtra("UserUID", userUID);

        StringBuilder selectedAddOnsBuilder = new StringBuilder();
        Map<String, Model_AddOns> selectedAddOnsMap = addOnAdapter.getSelectedAddOns();
        for (Model_AddOns addOn : selectedAddOnsMap.values()) {
            selectedAddOnsBuilder.append(addOn.getTitle()).append(": ₱").append(addOn.getPrice()).append("\n");
        }
        intent.putExtra("selectedAddOns", selectedAddOnsBuilder.toString());
        String voucherCode = voucherEditText.getText().toString().trim();
        intent.putExtra("voucherCode", voucherCode);
        startActivity(intent);
    }

    private interface DatePickerListener {
        void onDateSet(int date, int month, int year);
    }

    public interface DateValidator {
        boolean isValid(long date);
    }

    public interface OnVoucherValidationListener {
        void onValidationSuccess(double voucherValue);
        void onValidationFailure();
        void onValidationError();
    }
}
