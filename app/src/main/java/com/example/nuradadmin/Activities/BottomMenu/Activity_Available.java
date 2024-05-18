package com.example.nuradadmin.Activities.BottomMenu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nuradadmin.Activities.SideMenu.Activity_Dashboard;
import com.example.nuradadmin.Fragments.Fragment_Available;
import com.example.nuradadmin.Fragments.Fragment_History;
import com.example.nuradadmin.Fragments.Fragment_Housekeeping;
import com.example.nuradadmin.Fragments.Fragment_InUse;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.example.nuradadmin.databinding.ActivityAvailableBinding;

public class Activity_Available extends AppCompatActivity {
    private ActivityAvailableBinding binding;
    private ImageView back_icon;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAvailableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);

        // Set the initial fragment (default) to display when the activity is created
        title.setText("Available");
        replaceFragment(new Fragment_Available());

        // Set listener for bottom navigation view to handle item selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Check which menu item is selected and replace the current fragment accordingly
            if (item.getItemId() == R.id.available) {
                title.setText("Available");
                replaceFragment(new Fragment_Available());
            } else if (item.getItemId() == R.id.inUse){
                title.setText("In Use");
                replaceFragment(new Fragment_InUse());
            } else if (item.getItemId() == R.id.housekeeping){
                title.setText("Housekeeping");
                replaceFragment(new Fragment_Housekeeping());
            } else if (item.getItemId() == R.id.history){
                title.setText("History");
                replaceFragment(new Fragment_History());
            }
            return true;
        });

        back_icon.setOnClickListener(View ->{
            Intent i = new Intent(this, Activity_Dashboard.class);
            startActivity(i);
            finish();
        });
    }

    // Replaces the currently displayed fragment with the specified fragment.
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}