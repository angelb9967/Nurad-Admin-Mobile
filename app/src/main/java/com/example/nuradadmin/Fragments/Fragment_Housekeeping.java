package com.example.nuradadmin.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nuradadmin.Adapters.Adapter_Housekeeping;
import com.example.nuradadmin.Adapters.Adapter_InUse;
import com.example.nuradadmin.Models.Model_Housekeeping;
import com.example.nuradadmin.Models.Model_InUse;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Housekeeping extends Fragment {
    private RecyclerView recyclerView;
    private List<Model_Housekeeping> modelHousekeepingList;
    private DatabaseReference housekeeping_DBref;
    private Adapter_Housekeeping adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_Housekeeping() {
        // Required empty public constructor
    }

    public static Fragment_Housekeeping newInstance(String param1, String param2) {
        Fragment_Housekeeping fragment = new Fragment_Housekeeping();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__housekeeping, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        modelHousekeepingList = new ArrayList<>();
        adapter = new Adapter_Housekeeping(getContext(), modelHousekeepingList);
        recyclerView.setAdapter(adapter);

        housekeeping_DBref = FirebaseDatabase.getInstance().getReference("Housekeeping");

        housekeeping_DBref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelHousekeepingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model_Housekeeping housekeeping = snapshot.getValue(Model_Housekeeping.class);
                    modelHousekeepingList.add(housekeeping);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}