package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Activities.SideMenu.Activity_OtherRevenue;
import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Models_RevenueCost;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class Activity_CreateRevenueCost extends AppCompatActivity {
    private DatabaseReference revenueCost_DBref;
    private EditText Date_Etxt, Time_Etxt, Amount_Etxt, Note_Etxt;
    private Button saveBtn;
    private ImageView Date_Img, Time_Img;
    private Spinner selector_spinner;
    private ArrayList<String> selector_list;
    private CustomArrayAdapter selector_adapter;
    private ImageView back_icon;
    private TextView title;
    private Calendar calendar;
    private int mHour, mMinute;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_revenue_cost);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        Date_Etxt = findViewById(R.id.Date_Etxt);
        Time_Etxt = findViewById(R.id.Time_Etxt);
        selector_spinner = findViewById(R.id.EntryType_Spinner);
        Amount_Etxt = findViewById(R.id.Amount_Etxt);
        Note_Etxt = findViewById(R.id.Notes_Etxt);
        Date_Img = findViewById(R.id.CalendarPicker_Img);
        Time_Img = findViewById(R.id.Time_Img);
        saveBtn = findViewById(R.id.Save_Btn);

        title.setText("Create");
        revenueCost_DBref = FirebaseDatabase.getInstance().getReference("Revenue and Expenses");

        calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String[] value = {"Select Option", "Revenue", "Cost"};
        selector_list = new ArrayList<>(Arrays.asList(value));
        selector_adapter = new CustomArrayAdapter(this, R.layout.style_spinner, selector_list);
        selector_spinner.setAdapter(selector_adapter);

        // Prevent keyboard from appearing when EditText is touched
        Date_Etxt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker_and_setDate(Date_Etxt, year, month, day);
                return true;
            }
            return false;
        });

        Date_Img.setOnClickListener(view -> {
            showDatePicker_and_setDate(Date_Etxt, year, month, day);
        });

        Date_Etxt.setOnFocusChangeListener((view, hasFocus) ->{
            if(hasFocus){
                showDatePicker_and_setDate(Date_Etxt, year, month, day);
            }
        });

        // Prevent keyboard from appearing when EditText is touched
        Time_Etxt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showTimePickerDialog(Time_Etxt);
                return true;
            }
            return false;
        });

        Time_Etxt.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                showTimePickerDialog(Time_Etxt);
            }
        });

        Time_Img.setOnClickListener(view ->{
            showTimePickerDialog(Time_Etxt);
        });

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_OtherRevenue.class);
            startActivity(i);
            finish();
        });

        saveBtn.setOnClickListener(view ->{
            final String date = Date_Etxt.getText().toString();
            final String time = Time_Etxt.getText().toString();
            final String selected_option = selector_spinner.getSelectedItem().toString();
            final String amount = Amount_Etxt.getText().toString();
            final String note = Note_Etxt.getText().toString();

            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || selector_spinner.getSelectedItemPosition() == 0 || TextUtils.isEmpty(amount) || TextUtils.isEmpty(note)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String revenueCostID = revenueCost_DBref.push().getKey();
            Models_RevenueCost modelRevenueCost = new Models_RevenueCost(date, time, selected_option, amount, note);
            revenueCost_DBref.child(revenueCostID).setValue(modelRevenueCost)
                    .addOnSuccessListener(aVoid -> {
                        Date_Etxt.setText("");
                        Time_Etxt.setText("");
                        selector_spinner.setSelection(0);
                        Amount_Etxt.setText("");
                        Note_Etxt.setText("");
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void showDatePicker_and_setDate(EditText field, int year, int month, int day){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Activity_CreateRevenueCost.this, new DatePickerDialog.OnDateSetListener() {
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

    private void showTimePickerDialog(EditText editText){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String formattedTime = formatTime(hourOfDay, minute);
                editText.setText(formattedTime);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String formatTime(int hourOfDay, int minute) {
        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(datetime.getTime());
    }
}