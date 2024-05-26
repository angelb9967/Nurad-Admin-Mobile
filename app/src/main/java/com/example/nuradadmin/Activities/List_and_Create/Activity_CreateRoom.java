package com.example.nuradadmin.Activities.List_and_Create;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Activity_CreateRoom extends AppCompatActivity {
    private Uri imageUri;
    private CheckBox depositRequired_chkbox, recommendedRoom_chk_box;
    private DatabaseReference roomTypes_DBref, priceRules_DBref, rooms_DBref;
    private StorageReference rooms_Sref;
    private ArrayList<String> roomTypes_list, priceRules_list;
    private CustomArrayAdapter roomTypes_adapter, priceRules_adapter;
    private EditText roomName_Etxt, description_Etxt, roomTitle_Etxt;
    private Spinner roomsType_spinner, priceRules_spinner;
    private Button saveBtn;
    private ImageView back_icon, uploadImg;
    private TextView title, uploadphototext;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        roomName_Etxt = findViewById(R.id.RoomName_Etxt);
        roomsType_spinner = findViewById(R.id.RoomsType_Spinner);
        priceRules_spinner = findViewById(R.id.PriceRule_Spinner);
        description_Etxt = findViewById(R.id.Description_Etxt);
        saveBtn = findViewById(R.id.Save_Btn);
        uploadImg = findViewById(R.id.UploadBtn);
        uploadphototext = findViewById(R.id.UPhotoTW);
        depositRequired_chkbox = findViewById(R.id.DepositReq_checkbox);
        recommendedRoom_chk_box = findViewById(R.id.Recommended_checkbox);
        roomTitle_Etxt = findViewById(R.id.RoomTitle_Etxt);

        // Initialize progress dialog for room creation
        progressDialog = new ProgressDialog(Activity_CreateRoom.this); // Initialize progressDialog here
        progressDialog.setTitle("Create Room");
        progressDialog.setMessage("Saving information ...");

        title.setText("Create");
        roomTypes_DBref = FirebaseDatabase.getInstance().getReference("Room Types");
        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
        rooms_DBref = FirebaseDatabase.getInstance().getReference("Rooms");
        rooms_Sref = FirebaseStorage.getInstance().getReference();

        roomTypes_list = new ArrayList<>();
        roomTypes_adapter = new CustomArrayAdapter(Activity_CreateRoom.this, R.layout.style_spinner, roomTypes_list);
        setupSpinner(roomsType_spinner);
        roomsType_spinner.setAdapter(roomTypes_adapter);
        Showdata(roomTypes_DBref, roomTypes_adapter, roomTypes_list, "Room Types");

        priceRules_list = new ArrayList<>();
        priceRules_adapter = new CustomArrayAdapter(Activity_CreateRoom.this, R.layout.style_spinner, priceRules_list);
        setupSpinner(priceRules_spinner);
        priceRules_spinner.setAdapter(priceRules_adapter);
        Showdata(priceRules_DBref, priceRules_adapter, priceRules_list, "Price Rules");

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_Rooms.class);
            startActivity(i);
            finish();
        });

        uploadImg.setOnClickListener(view ->{
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        saveBtn.setOnClickListener(view ->{
            final String roomName = roomName_Etxt.getText().toString();
            final String roomTitle = roomTitle_Etxt.getText().toString();
            final String roomType = roomsType_spinner.getSelectedItem().toString();
            final String priceRule = priceRules_spinner.getSelectedItem().toString();
            final String description = description_Etxt.getText().toString();
            final double price = 1500.00;
            // Di ko pa siya nacocoompute based sa price rule
            // Compute Booking Price
            // Add Validation - Check if Empty Before Saving
            // Empty Edittext

            if(imageUri == null){
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(roomName) || TextUtils.isEmpty(roomType) || roomsType_spinner.getSelectedItemPosition() == 0  || priceRules_spinner.getSelectedItemPosition() == 0 || TextUtils.isEmpty(roomTitle)){
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean depositReq;
            depositReq = depositRequired_chkbox.isChecked();

            if(recommendedRoom_chk_box.isChecked()){
                rooms_DBref = FirebaseDatabase.getInstance().getReference("Check");
            }

            Model_Room modelRoom = new Model_Room(roomName, roomTitle, depositReq, price, roomType, priceRule, description);
            uploadToFirebase(imageUri, modelRoom); // Upload the image and save the room information
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            uploadImg.setImageURI(imageUri);
            uploadphototext.setText("");
        }
    }

    // Function to upload the image and data to Firebase
    private void uploadToFirebase(Uri uri, final Model_Room modelRoom) {
        StorageReference fileRef = rooms_Sref.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        modelRoom.setImageUrl(uri.toString()); // Set the image URL in the model

                        // Save room information to Firebase Realtime Database
                        rooms_DBref.child(modelRoom.getRoomName()).setValue(modelRoom)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Clear the input fields
                                        roomName_Etxt.setText("");
                                        roomTitle_Etxt.setText("");
                                        resetImageUri();
                                        roomsType_spinner.setSelection(0);
                                        priceRules_spinner.setSelection(0);
                                        description_Etxt.setText("");
                                        depositRequired_chkbox.setChecked(false);
                                        recommendedRoom_chk_box.setChecked(false);
                                        progressDialog.dismiss();
                                        Toast.makeText(Activity_CreateRoom.this, "Room saved successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Activity_CreateRoom.this, "Failed to save room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        resetImageUri();
                        Toast.makeText(Activity_CreateRoom.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Activity_CreateRoom.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetImageUri() {
        imageUri = null;
        uploadImg.setImageDrawable(null);
        uploadphototext.setText("Upload your picture");
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime  = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void setupSpinner(Spinner spinner){
        // Set default selection to "Select Room Type [Room Type Spinner] | Select Price Rule [Price Rule Spinner]"
        spinner.setSelection(0);
        spinner.post(() -> {
            View selectedView = spinner.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                ((TextView) selectedView).setTextColor(Color.GRAY);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (view != null && view instanceof TextView) {
                    if (position == 0) {
                        ((TextView) view).setTextColor(Color.GRAY);
                    } else {
                        ((TextView) view).setTextColor(Color.BLACK);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void Showdata(DatabaseReference dbRef, CustomArrayAdapter adapter, ArrayList<String> list, String spinner_name){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(spinner_name.equalsIgnoreCase("Room Types")){
                    list.add("Select Room Type");
                }else{
                    list.add("Select Price Rule");
                }

                for(DataSnapshot item:snapshot.getChildren()){
                    list.add(item.getKey().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}