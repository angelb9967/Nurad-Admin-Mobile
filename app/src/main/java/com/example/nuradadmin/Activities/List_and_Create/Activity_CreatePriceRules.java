package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.example.nuradadmin.Utilities.TrimUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Activity_CreatePriceRules extends AppCompatActivity {
    private DatabaseReference priceRules_DBref, allRooms_DBref, recommendedRooms_DBref;
    private EditText ruleName_Etxt, price_Etxt, extraAdultprice_Etxt, extraChildprice_Etxt, friday_Etxt, saturday_Etxt, sunday_Etxt;
    private Button saveBtn;
    private ImageView back_icon;
    private TextView title;
    private String purpose = "", old_priceRuleName = "";
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

            old_priceRuleName = bundle.getString("Rule Name");
        }

        if(purpose.equalsIgnoreCase("View Details")){
            title.setText(R.string.Edit);
            saveBtn.setText(R.string.Update);
        }else{
            title.setText(R.string.Create);
            saveBtn.setText(R.string.Save);
        }
        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
        allRooms_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommendedRooms_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_PriceRules.class);
            startActivity(i);
            finish();
        });

        saveBtn.setOnClickListener(view ->{
            final String rule_name = TrimUtil.trimRight(ruleName_Etxt.getText().toString());
            final String priceStr = price_Etxt.getText().toString().trim();
            final String extraAdultPriceStr = extraAdultprice_Etxt.getText().toString().trim();
            final String extraChildPriceStr = extraChildprice_Etxt.getText().toString().trim();
            String fridayPriceStr = friday_Etxt.getText().toString().trim();
            String saturdayPriceStr = saturday_Etxt.getText().toString().trim();
            String sundayPriceStr = sunday_Etxt.getText().toString().trim();

            if (TextUtils.isEmpty(rule_name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(extraAdultPriceStr) || TextUtils.isEmpty(extraChildPriceStr)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(fridayPriceStr)){
                fridayPriceStr = priceStr;
            }

            if(TextUtils.isEmpty(saturdayPriceStr)){
                saturdayPriceStr = priceStr;
            }

            if(TextUtils.isEmpty(sundayPriceStr)){
                sundayPriceStr = priceStr;
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
                priceRules_DBref.child(rule_name).setValue(modelPriceRule).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(!old_priceRuleName.equals(rule_name)){
                                priceRules_DBref.child(old_priceRuleName).removeValue();
                            }
                            updateRooms(old_priceRuleName, rule_name);
                            Toast.makeText(Activity_CreatePriceRules.this, "Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Activity_CreatePriceRules.this, "Failed to update: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                // Add and Save Data to Database
                priceRules_DBref.child(rule_name).setValue(modelPriceRule)
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

    private void updateRooms(String old_priceRuleName, String new_priceRuleName) {
        // Query to find all rooms with old_priceRuleName
        Query query1 = allRooms_DBref.orderByChild("priceRule").equalTo(old_priceRuleName);
        Query query2 = recommendedRooms_DBref.orderByChild("priceRule").equalTo(old_priceRuleName);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update each room record to use new_priceRuleName
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String roomKey = snapshot.getKey();
                        if (roomKey != null) {
                            dataSnapshot.getRef().child(roomKey).child("priceRule").setValue(new_priceRuleName)
                                    .addOnSuccessListener(aVoid -> {
                                        // Room record updated successfully
                                        Log.d("Activity_CreatePriceRules", "Room record updated successfully. From " + old_priceRuleName + " to " + new_priceRuleName);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure to update room record
                                        Log.e("Activity_CreatePriceRules", "Failed to update room record: " + e.getMessage());
                                    });
                        }
                    }
                } else {
                    Log.d("Activity_CreatePriceRules", "No rooms found with priceRule: " + old_priceRuleName + ". No need to update any rooms.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Activity_CreatePriceRules", "Database error: " + databaseError.getMessage());
            }
        };

        query1.addListenerForSingleValueEvent(listener);
        query2.addListenerForSingleValueEvent(listener);
    }
}