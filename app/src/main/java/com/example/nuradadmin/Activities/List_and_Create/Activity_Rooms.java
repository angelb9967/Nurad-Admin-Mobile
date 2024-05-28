package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

import com.example.nuradadmin.Activities.SideMenu.Activity_SystemManagement;
import com.example.nuradadmin.Adapters.Adapter_Room;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Rooms extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Model_Room> modelRoomList;
    private List<String> roomTitles;
    private Adapter_Room adapter;
    private ImageView back_icon;
    private TextView title;
    private FloatingActionButton floatingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rooms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);

        modelRoomList = new ArrayList<>();
        roomTitles = new ArrayList<>();

        title.setText("Rooms");

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_SystemManagement.class);
            startActivity(i);
            finish();
        });

        floatingBtn.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_CreateRoom.class);
            startActivity(i);
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new Adapter_Room(Activity_Rooms.this, modelRoomList);
        recyclerView.setAdapter(adapter);
        fetchRoomsFromDatabase( "Rooms", "Check");
//        fetchRoomsFromDatabase("AllRooms", "RecommRooms", "Rooms");
    }

    private void fetchRoomsFromDatabase(String... referencePaths) {
        modelRoomList.clear();
        roomTitles.clear();
        for (String referencePath : referencePaths) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(referencePath);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Model_Room room = dataSnapshot.getValue(Model_Room.class);
                        if (room != null) {
                            modelRoomList.add(room);
                            roomTitles.add(room.getTitle());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Activity_Rooms.this, "Failed to load rooms.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}