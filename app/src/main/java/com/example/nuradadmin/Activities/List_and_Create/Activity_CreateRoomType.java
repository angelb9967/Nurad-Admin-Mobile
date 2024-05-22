package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_CreateRoomType extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private EditText RoomType_Etxt, Description_Etxt;
    private Button Save_Btn;
    private ImageView back_icon;
    private TextView title;
    private String purpose = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        // Initialize views
        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        RoomType_Etxt = findViewById(R.id.RoomTypeName_Etxt);
        Description_Etxt = findViewById(R.id.Description_Etxt);
        Save_Btn = findViewById(R.id.Save_Btn);

        // Retrieve the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){ //If not null, extract all the data
            purpose = bundle.getString("Purpose");
            RoomType_Etxt.setText(bundle.getString("Room Type Name"));
            Description_Etxt.setText(bundle.getString("Room Type Description"));
        }

        if(purpose.equalsIgnoreCase("View Details")){
            title.setText("Details");
            Save_Btn.setText("Update");
        }else{
            title.setText("Create");
            Save_Btn.setText("Save");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Room Types");

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_RoomTypes.class);
            startActivity(i);
            finish();
        });

        Save_Btn.setOnClickListener(view -> {
            String roomTypeName = RoomType_Etxt.getText().toString();
            String description = Description_Etxt.getText().toString();

            if (roomTypeName.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Model_RoomType modelRoomType = new Model_RoomType(roomTypeName, description);

            if(purpose.equalsIgnoreCase("View Details")){
                // Update Data to Database
            }else{
                // Add and Save Data to Database
                databaseReference.child(roomTypeName).setValue(modelRoomType)
                        .addOnSuccessListener(aVoid -> {
                            RoomType_Etxt.setText("");
                            Description_Etxt.setText("");
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
