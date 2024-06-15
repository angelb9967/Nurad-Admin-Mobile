package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.TrimUtil;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Activity_CreateRoomType extends AppCompatActivity {
    private DatabaseReference roomTypes_DBref, allRooms_DBref, recommendedRooms_DBref;
    private EditText RoomType_Etxt, Description_Etxt;
    private Button Save_Btn;
    private ImageView back_icon;
    private TextView title;
    private String purpose = "", old_roomTypeName = "";
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

            old_roomTypeName = bundle.getString("Room Type Name");
        }

        if(purpose.equalsIgnoreCase("View Details")){
            title.setText("Edit");
            Save_Btn.setText("Update");
        }else{
            title.setText("Create");
            Save_Btn.setText("Save");
        }
        roomTypes_DBref = FirebaseDatabase.getInstance().getReference("Room Types");
        allRooms_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommendedRooms_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_RoomTypes.class);
            startActivity(i);
            finish();
        });

        Save_Btn.setOnClickListener(view -> {
            final String roomTypeName = TrimUtil.trimRight(RoomType_Etxt.getText().toString());
            final String description = TrimUtil.trimRight(Description_Etxt.getText().toString());

            if (roomTypeName.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Model_RoomType modelRoomType = new Model_RoomType(roomTypeName, description);

            if(purpose.equalsIgnoreCase("View Details")){
                // Update Data to Firebase
                roomTypes_DBref.child(roomTypeName).setValue(modelRoomType).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(!old_roomTypeName.equals(roomTypeName)){
                                roomTypes_DBref.child(old_roomTypeName).removeValue();
                            }
                            updateRooms(old_roomTypeName, roomTypeName);
                            Toast.makeText(Activity_CreateRoomType.this, "Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Activity_CreateRoomType.this, "Failed to update: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                // Add Record and Save Data to Firebase
                roomTypes_DBref.child(roomTypeName).setValue(modelRoomType)
                        .addOnSuccessListener(aVoid -> {
                            RoomType_Etxt.setText("");
                            Description_Etxt.setText("");
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateRooms(String old_roomTypeName, String new_roomTypeName) {
        // Query to find all rooms with old_roomTypeName
        Query query1 = allRooms_DBref.orderByChild("roomType").equalTo(old_roomTypeName);
        Query query2 = recommendedRooms_DBref.orderByChild("roomType").equalTo(old_roomTypeName);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update each room record to use new_roomTypeName
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String roomKey = snapshot.getKey();
                        if (roomKey != null) {
                            dataSnapshot.getRef().child(roomKey).child("roomType").setValue(new_roomTypeName)
                                    .addOnSuccessListener(aVoid -> {
                                        // Room record updated successfully
                                        Log.d("Activity_CreateRoomType", "Room record updated successfully. From " + old_roomTypeName + " to " + new_roomTypeName);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure to update room record
                                        Log.e("Activity_CreateRoomType", "Failed to update room record: " + e.getMessage());
                                    });
                        }
                    }
                } else {
                    Log.d("Activity_CreateRoomType", "No rooms found with roomType: " + old_roomTypeName + ". No need to update any rooms.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Activity_CreateRoomType", "Database error: " + databaseError.getMessage());
            }
        };

        query1.addListenerForSingleValueEvent(listener);
        query2.addListenerForSingleValueEvent(listener);
    }
}
