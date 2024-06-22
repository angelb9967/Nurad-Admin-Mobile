package com.example.nuradadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.SideMenu.Activity_SystemManagement;
import com.example.nuradadmin.Adapters.Adapter_Feedback;
import com.example.nuradadmin.Models.Model_Feedback;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Feedbacks extends AppCompatActivity {
    private ImageView back_icon;
    private TextView title;
    private RecyclerView recyclerView;
    private DatabaseReference feedback_DBref;
    private Adapter_Feedback adapter;
    private List<Model_Feedback> modelFeedbackList = new ArrayList<>();
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);

        title.setText("Reviews");

        feedback_DBref = FirebaseDatabase.getInstance().getReference("Customer Feedback");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Set the adapter after initializing the list
        adapter = new Adapter_Feedback(this, modelFeedbackList);
        recyclerView.setAdapter(adapter);

        eventListener = feedback_DBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelFeedbackList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot feedbackSnapshot : userSnapshot.getChildren()) {
                        Model_Feedback feedback = feedbackSnapshot.getValue(Model_Feedback.class);
                        if (feedback != null) {
                            modelFeedbackList.add(feedback);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_SystemManagement.class);
            startActivity(i);
            finish();
        });
    }
}
