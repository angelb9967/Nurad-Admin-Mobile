package com.example.nuradadmin.Activities.Create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Activities.SideMenu.Activity_OtherRevenue;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;

public class Activity_CreateRoomType extends AppCompatActivity {
    private ImageView back_icon;
    private TextView title;
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

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);

        title.setText("Create");

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_RoomTypes.class);
            startActivity(i);
            finish();
        });
    }
}