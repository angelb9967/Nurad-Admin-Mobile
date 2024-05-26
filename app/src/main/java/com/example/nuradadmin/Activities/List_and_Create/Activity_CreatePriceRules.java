package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            price_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Price")));
            extraAdultprice_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Extra Adult Price")));
            extraChildprice_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Extra Child Price")));
            friday_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Friday Price")));
            saturday_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Saturday Price")));
            sunday_Etxt.setText(String.format(Locale.US, "%.2f", bundle.getDouble("Sunday Price")));
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
            final String priceStr = price_Etxt.getText().toString();
            final String extraAdultPriceStr = extraAdultprice_Etxt.getText().toString();
            final String extraChildPriceStr = extraChildprice_Etxt.getText().toString();
            final String fridayPriceStr = friday_Etxt.getText().toString();
            final String saturdayPriceStr = saturday_Etxt.getText().toString();
            final String sundayPriceStr = sunday_Etxt.getText().toString();

            if (TextUtils.isEmpty(rule_name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(extraAdultPriceStr) || TextUtils.isEmpty(extraChildPriceStr) || TextUtils.isEmpty(fridayPriceStr) || TextUtils.isEmpty(saturdayPriceStr) || TextUtils.isEmpty(sundayPriceStr)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            final double price = Double.parseDouble(priceStr);
            final double extra_AdultPrice = Double.parseDouble(extraAdultPriceStr);
            final double extra_ChildPrice = Double.parseDouble(extraChildPriceStr);
            final double friday_price = Double.parseDouble(fridayPriceStr);
            final double saturday_price = Double.parseDouble(saturdayPriceStr);
            final double sunday_price = Double.parseDouble(sundayPriceStr);

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