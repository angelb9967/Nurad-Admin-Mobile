package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Activity_CreatePriceRules extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private EditText ruleName_Etxt, price_Etxt, extraAdultprice_Etxt, extraChildprice_Etxt, friday_Etxt, saturday_Etxt, sunday_Etxt;
    private Button saveBtn;
    private ImageView back_icon;
    private TextView title;
    private String purpose = "";
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_price_rules);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        ruleName_Etxt = findViewById(R.id.RuleName_Etxt);
        price_Etxt = findViewById(R.id.Price_Etxt);
        extraAdultprice_Etxt = findViewById(R.id.Adult_Etxt);
        extraChildprice_Etxt = findViewById(R.id.Child_Etxt);
        friday_Etxt = findViewById(R.id.FridayPrice_Etxt);
        saturday_Etxt = findViewById(R.id.SaturdayPrice_Etxt);
        sunday_Etxt = findViewById(R.id.SundayPrice_Etxt);
        saveBtn = findViewById(R.id.Save_Btn);

        // Retrieve the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){ //If not null, extract all the data
            purpose = bundle.getString("Purpose");
            ruleName_Etxt.setText(bundle.getString("Rule Name"));
            price_Etxt.setText(bundle.getString("Price"));
            extraAdultprice_Etxt.setText(bundle.getString("Extra Adult Price"));
            extraChildprice_Etxt.setText(bundle.getString("Extra Child Price"));
            friday_Etxt.setText(bundle.getString("Friday Price"));
            saturday_Etxt.setText(bundle.getString("Saturday Price"));
            sunday_Etxt.setText(bundle.getString("Sunday Price"));
        }

        if(purpose.equalsIgnoreCase("View Details")){
            title.setText("Edit");
            saveBtn.setText("Update");
        }else{
            title.setText("Create");
            saveBtn.setText("Save");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Price Rules");

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_PriceRules.class);
            startActivity(i);
            finish();
        });

        saveBtn.setOnClickListener(view ->{
            final String rule_name = ruleName_Etxt.getText().toString();
            final String price = price_Etxt.getText().toString();
            final String extra_AdultPrice = extraAdultprice_Etxt.getText().toString();
            final String extra_ChildPrice = extraChildprice_Etxt.getText().toString();
            final String friday_price = friday_Etxt.getText().toString();
            final String saturday_price = saturday_Etxt.getText().toString();
            final String sunday_price = sunday_Etxt.getText().toString();

            if (TextUtils.isEmpty(rule_name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(extra_AdultPrice) || TextUtils.isEmpty(extra_ChildPrice)  || TextUtils.isEmpty(friday_price) || TextUtils.isEmpty(saturday_price) || TextUtils.isEmpty(sunday_price)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Model_PriceRule modelPriceRule = new Model_PriceRule(rule_name, price, extra_AdultPrice, extra_ChildPrice, friday_price, saturday_price, sunday_price);

            if(purpose.equalsIgnoreCase("View Details")){
                // Update Data to Database
            }else{
                // Add and Save Data to Database
                databaseReference.child(rule_name).setValue(modelPriceRule)
                        .addOnSuccessListener(aVoid -> {
                            ruleName_Etxt.setText("");
                            price_Etxt.setText("");
                            extraAdultprice_Etxt.setText("");
                            extraChildprice_Etxt.setText("");
                            friday_Etxt.setText("");
                            saturday_Etxt.setText("");
                            sunday_Etxt.setText("");
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}