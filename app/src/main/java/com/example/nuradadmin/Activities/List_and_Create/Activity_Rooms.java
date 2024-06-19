package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Activity_Rooms extends AppCompatActivity implements Adapter_Room.OnLongItemClickListener, Adapter_Room.OnSelectionChangedListener {
    public static boolean isContextualModeEnabled = false;
    private ImageView back_icon, moveOutDeleteMode, delete_icon;
    private RecyclerView recyclerView;
    private List<String> roomTitles;
    private List<Model_Room> modelRoomList;
    private DatabaseReference room_DBref, recommended_DBref;
    private TextView title, itemCounter;
    private FloatingActionButton floatingBtn;
    private Adapter_Room adapter;
    private View toolbar_normal;
    private View toolbar_deleteMode;
    @SuppressLint("NotifyDataSetChanged")
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
        moveOutDeleteMode = findViewById(R.id.back_btn);
        title = findViewById(R.id.title);
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar_normal = findViewById(R.id.toolbar_normal);
        toolbar_deleteMode = findViewById(R.id.toolbar_deleteMode);
        itemCounter = findViewById(R.id.counter);
        delete_icon = findViewById(R.id.delete_icon);

        modelRoomList = new ArrayList<>();
        roomTitles = new ArrayList<>();

        title.setText("Rooms");

        room_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommended_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new Adapter_Room(Activity_Rooms.this, modelRoomList, this, this);
        recyclerView.setAdapter(adapter);
        fetchRoomsFromDatabase( "AllRooms", "RecommRooms");

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

        moveOutDeleteMode.setOnClickListener(view -> {
            isContextualModeEnabled = false;
            toolbar_deleteMode.setVisibility(View.GONE);
            toolbar_normal.setVisibility(View.VISIBLE);
            adapter.clearSelectedItems();
            adapter.notifyDataSetChanged();
            updateItemCounter(0);
        });

        delete_icon.setOnClickListener(view -> {
            List<Model_Room> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show();
            } else {
                showDeleteItemDialog(this);
            }
        });

        // show or hide floating button while scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && floatingBtn.isShown()) {
                    floatingBtn.hide();
                } else if (dy < 0 && !floatingBtn.isShown()) {
                    floatingBtn.show();
                }
            }
        });
    }

    private void fetchRoomsFromDatabase(String... referencePaths) {
        modelRoomList.clear();
        roomTitles.clear();
        for (String referencePath : referencePaths) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(referencePath);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLongItemClick(int position) {
        if (!adapter.getSelectedItems().contains(position)) {
            isContextualModeEnabled = true;
            updateItemCounter(1);
            toolbar_normal.setVisibility(View.GONE);
            toolbar_deleteMode.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    public void onSelectionChanged(int count) {
        updateItemCounter(count);
    }

    private void updateItemCounter(int count) {
        itemCounter.setText(count + " Item(s) Selected");
    }

    private void showDeleteItemDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete the selected item(s)?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete selected item(s)
                for (Model_Room room : adapter.getSelectedItems()) {
                    checkAndDeleteRoom(room);
                }
                // Reset contextual mode and update UI
                isContextualModeEnabled = false;
                toolbar_deleteMode.setVisibility(View.GONE);
                toolbar_normal.setVisibility(View.VISIBLE);
                adapter.clearSelectedItems();
                adapter.notifyDataSetChanged();
                floatingBtn.show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void checkAndDeleteRoom(Model_Room room) {
        room_DBref.child(room.getRoomName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelRoomList.clear(); // Clear the list before adding new data
                roomTitles.clear();
                if (snapshot.exists()) {
                    room_DBref.child(room.getRoomName()).removeValue();
                    deleteImageFromStorage(room.getImageUrl());
                } else {
                    recommended_DBref.child(room.getRoomName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                recommended_DBref.child(room.getRoomName()).removeValue();
                                deleteImageFromStorage(room.getImageUrl());
                            } else {
                                Log.e("Activity_Rooms", "Room does not exist in database (recommended): " + room.getRoomName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                            Log.e("Activity_Rooms", "Database error (recommended): " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("Activity_Rooms", "Database error: " + error.getMessage());
            }
        });
    }

    private void deleteImageFromStorage(String imageUrl) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Activity_Rooms", "Image deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Activity_Rooms", "Error deleting image: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        isContextualModeEnabled = false;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}