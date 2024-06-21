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

import com.example.nuradadmin.Adapters.Adapter_History;
import com.example.nuradadmin.Adapters.Adapter_Housekeeping;
import com.example.nuradadmin.Models.Model_History;
import com.example.nuradadmin.Models.Model_Housekeeping;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_History extends Fragment {
    private RecyclerView recyclerView;
    private List<Model_History> modelHistoryList;
    private DatabaseReference history_DBref;
    private Adapter_History adapter;
    private ViewStub emptyStateViewStub;
    private View inflatedEmptyStateView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_History() {
        // Required empty public constructor
    }

    public static Fragment_History newInstance(String param1, String param2) {
        Fragment_History fragment = new Fragment_History();
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
        View view = inflater.inflate(R.layout.fragment__history, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyStateViewStub = view.findViewById(R.id.emptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        modelHistoryList = new ArrayList<>();
        adapter = new Adapter_History(getContext(), modelHistoryList);
        recyclerView.setAdapter(adapter);

        history_DBref = FirebaseDatabase.getInstance().getReference("History");

        history_DBref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelHistoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model_History history = snapshot.getValue(Model_History.class);
                    modelHistoryList.add(history);
                }
                adapter.notifyDataSetChanged();

                // Show/hide empty state ViewStub based on data
                if (modelHistoryList.isEmpty()) {
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