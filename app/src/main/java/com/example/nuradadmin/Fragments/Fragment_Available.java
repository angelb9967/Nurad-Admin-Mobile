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
import android.view.ViewStub;
import android.widget.Toast;

import com.example.nuradadmin.Adapters.Adapter_AvailableRooms;
import com.example.nuradadmin.Models.Model_AvailableRooms;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Available extends Fragment {
    private RecyclerView recyclerView;
    private List<Model_AvailableRooms> modelAvailableRoomsList;
    private DatabaseReference availableRooms_DBref;
    private Adapter_AvailableRooms adapter;
    private ViewStub emptyStateViewStub;
    private View inflatedEmptyStateView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_Available() {
        // Required empty public constructor
    }

    public static Fragment_Available newInstance(String param1, String param2) {
        Fragment_Available fragment = new Fragment_Available();
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
        View view = inflater.inflate(R.layout.fragment__available, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyStateViewStub = view.findViewById(R.id.emptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        modelAvailableRoomsList = new ArrayList<>();
        adapter = new Adapter_AvailableRooms(getContext(), modelAvailableRoomsList);
        recyclerView.setAdapter(adapter);

        availableRooms_DBref = FirebaseDatabase.getInstance().getReference("Available Rooms");

        availableRooms_DBref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelAvailableRoomsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model_AvailableRooms availableRooms = snapshot.getValue(Model_AvailableRooms.class);
                    modelAvailableRoomsList.add(availableRooms);
                }
                adapter.notifyDataSetChanged();

                // Show/hide empty state ViewStub based on data
                if (modelAvailableRoomsList.isEmpty()) {
                    if (inflatedEmptyStateView == null) {
                        inflatedEmptyStateView = emptyStateViewStub.inflate();
                    }
                    recyclerView.setVisibility(View.GONE);
                    inflatedEmptyStateView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    if (inflatedEmptyStateView != null) {
                        inflatedEmptyStateView.setVisibility(View.GONE);
                    }
                }
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