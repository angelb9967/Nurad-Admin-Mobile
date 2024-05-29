package com.example.nuradadmin.Activities.List_and_Create;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.Adapters.CustomArrayAdapter;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Activity_CreateRoom extends AppCompatActivity {
    private FrameLayout frame_layout;
    private CheckBox depositRequired_chkbox, recommendedRoom_chk_box;
    private DatabaseReference roomTypes_DBref, priceRules_DBref, rooms_DBref;
    private StorageReference rooms_Sref;
    private ArrayList<String> roomTypes_list, priceRules_list;
    private CustomArrayAdapter roomTypes_adapter, priceRules_adapter;
    private EditText roomName_Etxt, description_Etxt, roomTitle_Etxt, price_Etxt;
    private Spinner roomsType_spinner, priceRules_spinner;
    private Button saveBtn;
    private ImageView back_icon, uploadImg;
    private TextView title, uploadphototext;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private String purpose = "", imageUrl = "", oldImageURL = "", url = "";
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

        initializeViews();
        setupFirebase();
        setupSpinners();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadRoomDetails(bundle);
        }

        setEventListeners();
    }

    private void initializeViews() {
        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        roomName_Etxt = findViewById(R.id.RoomName_Etxt);
        roomTitle_Etxt = findViewById(R.id.RoomTitle_Etxt);
        roomsType_spinner = findViewById(R.id.RoomsType_Spinner);
        priceRules_spinner = findViewById(R.id.PriceRule_Spinner);
        description_Etxt = findViewById(R.id.Description_Etxt);
        saveBtn = findViewById(R.id.Save_Btn);
        uploadImg = findViewById(R.id.UploadBtn);
        uploadphototext = findViewById(R.id.UPhotoTW);
        depositRequired_chkbox = findViewById(R.id.DepositReq_checkbox);
        recommendedRoom_chk_box = findViewById(R.id.Recommended_checkbox);
        frame_layout = findViewById(R.id.framelayout);
        price_Etxt = findViewById(R.id.Price_Etxt);
        disablePriceEtxt();

        progressDialog = new ProgressDialog(Activity_CreateRoom.this);
        progressDialog.setTitle("Create Room");
        progressDialog.setMessage("Saving information ...");
    }

    private void setupFirebase() {
        roomTypes_DBref = FirebaseDatabase.getInstance().getReference("Room Types");
        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
        rooms_DBref = FirebaseDatabase.getInstance().getReference("Rooms");
        rooms_Sref = FirebaseStorage.getInstance().getReference();

        roomTypes_list = new ArrayList<>();
        roomTypes_adapter = new CustomArrayAdapter(Activity_CreateRoom.this, R.layout.style_spinner, roomTypes_list);
        priceRules_list = new ArrayList<>();
        priceRules_adapter = new CustomArrayAdapter(Activity_CreateRoom.this, R.layout.style_spinner, priceRules_list);
    }

    private void setupSpinners() {
        setupSpinner(roomsType_spinner, roomTypes_adapter, roomTypes_list, "Room Types");
        setupSpinner(priceRules_spinner, priceRules_adapter, priceRules_list, "Price Rules");

        roomsType_spinner.setAdapter(roomTypes_adapter);
        priceRules_spinner.setAdapter(priceRules_adapter);

        loadSpinnerData();
    }

    private void loadRoomDetails(Bundle bundle) {
        purpose = bundle.getString("Purpose");
        imageUrl = bundle.getString("Image");
        oldImageURL = imageUrl;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(uploadImg);
        } else {
            uploadImg.setImageResource(R.drawable.logo_purple);
        }
        uploadphototext.setText("");
        frame_layout.setBackground(ContextCompat.getDrawable(this, R.drawable.pink_straight_bg));

        roomName_Etxt.setText(bundle.getString("Room Number"));
        roomTitle_Etxt.setText(bundle.getString("Room Title"));
        description_Etxt.setText(bundle.getString("Description"));

        String roomType = bundle.getString("Room Type");
        String priceRule = bundle.getString("Price Rule");

        Log.d("SpinnerIndices", "Room Type String: " + roomType);
        Log.d("SpinnerIndices", "Price Rule String: " + priceRule);

        roomsType_spinner.setTag(roomType);
        priceRules_spinner.setTag(priceRule);

        depositRequired_chkbox.setChecked(bundle.getBoolean("Deposit Required?"));
        recommendedRoom_chk_box.setChecked(bundle.getBoolean("Recommend Room?"));

        if (purpose.equalsIgnoreCase("View Details")) {
            title.setText("Edit");
            saveBtn.setText("Update");
        } else {
            title.setText("Create");
            saveBtn.setText("Save");
        }
    }

    private void changeSpinnerItemTextColor(Spinner spinner, int position) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            if (position == 0) {
                ((TextView) selectedView).setTextColor(Color.GRAY); // Set to gray if no item selected
            } else {
                ((TextView) selectedView).setTextColor(Color.BLACK); // Set to black if item selected
            }
        }
    }


    private void setEventListeners() {
        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_Rooms.class);
            startActivity(i);
            finish();
        });

        uploadImg.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        saveBtn.setOnClickListener(view -> saveRoomDetails());

        priceRules_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selectedPriceRule = priceRules_spinner.getSelectedItem().toString();
                    retrieveAndSetPrice(selectedPriceRule);
                } else {
                    price_Etxt.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void saveRoomDetails() {
        final String roomName = roomName_Etxt.getText().toString();
        final String roomTitle = roomTitle_Etxt.getText().toString();
        final String roomType = roomsType_spinner.getSelectedItem().toString();
        final String priceRule = priceRules_spinner.getSelectedItem().toString();
        final String description = description_Etxt.getText().toString();

        if (TextUtils.isEmpty(roomName) || TextUtils.isEmpty(roomType) || roomsType_spinner.getSelectedItemPosition() == 0 || priceRules_spinner.getSelectedItemPosition() == 0 || TextUtils.isEmpty(roomTitle)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean depositReq = depositRequired_chkbox.isChecked();
        boolean isRecommended = recommendedRoom_chk_box.isChecked();

        if (isRecommended) {
            rooms_DBref = FirebaseDatabase.getInstance().getReference("Check");
        }

        Model_Room modelRoom = new Model_Room(roomName, roomTitle, isRecommended, depositReq, roomType, priceRule, description);
        if(purpose.equalsIgnoreCase("View Details")){
            // Update Existing Record in Firebase
            if (imageUri == null && Objects.equals(imageUrl, oldImageURL)) {
                // If imageUri is not set, use the existing image URL
                modelRoom.setImageUrl(oldImageURL);
                updateRoomDetails(modelRoom);
            } else {
                // If imageUri is set, update with the new image
                updateRoomDetailsWithImage(imageUri, modelRoom);
            }
        } else {
            // Add and Save New Record to Firebase
            if (imageUri == null) {
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadToFirebase(imageUri, modelRoom);
        }
    }

    private void updateRoomDetailsWithImage(Uri uri, final Model_Room modelRoom){
        // Upload the new image
        String fileName = System.currentTimeMillis() + "." + getFileExtension(uri);
        StorageReference fileRef = rooms_Sref.child("Room Images/" + fileName);
        progressDialog.show();

        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri uriImage = uriTask.getResult();
            url = uriImage.toString();
            // Set the new image URL
            modelRoom.setImageUrl(url);
            updateRoomDetails(modelRoom);
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Activity_CreateRoom.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FirebaseUpload", "Failed to upload image", e);
        });
    }

    private void updateRoomDetails(Model_Room modelRoom) {
        DatabaseReference roomRef = rooms_DBref.child(modelRoom.getRoomName());
        roomRef.setValue(modelRoom).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Activity_CreateRoom.this, "Room Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Activity_CreateRoom.this, Activity_Rooms.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(Activity_CreateRoom.this, "Failed to Update Room", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            uploadImg.setImageURI(imageUri);
            uploadphototext.setText("");
            frame_layout.setBackground(ContextCompat.getDrawable(this, R.drawable.pink_straight_bg));
        }
    }

    private void uploadToFirebase(Uri uri, final Model_Room modelRoom) {
        StorageReference fileRef = rooms_Sref.child("Room Images/" + System.currentTimeMillis() + "." + getFileExtension(uri));
        // Save Image to Firebase Storage
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            modelRoom.setImageUrl(uri1.toString());
            // Save data to Realtime Database
            rooms_DBref.child(modelRoom.getRoomName()).setValue(modelRoom).addOnSuccessListener(aVoid -> {
                clearInputFields();
                progressDialog.dismiss();
                Toast.makeText(Activity_CreateRoom.this, "Room saved successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(Activity_CreateRoom.this, "Failed to save room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
            resetImageUri();
            Toast.makeText(Activity_CreateRoom.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
        })).addOnProgressListener(snapshot -> progressDialog.show()).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Activity_CreateRoom.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearInputFields() {
        roomName_Etxt.setText("");
        roomTitle_Etxt.setText("");
        resetImageUri();
        roomsType_spinner.setSelection(0);
        priceRules_spinner.setSelection(0);
        description_Etxt.setText("");
        depositRequired_chkbox.setChecked(false);
        recommendedRoom_chk_box.setChecked(false);
    }

    private void resetImageUri() {
        imageUri = null;
        uploadImg.setImageDrawable(null);
        uploadphototext.setText("Upload your picture");
        frame_layout.setBackground(ContextCompat.getDrawable(this, R.drawable.pink_dotted_bg));
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void setupSpinner(Spinner spinner, CustomArrayAdapter adapter, ArrayList<String> list, String spinner_name) {
        spinner.setSelection(0);
        spinner.post(() -> {
            View selectedView = spinner.getSelectedView();
            if (selectedView instanceof TextView) {
                ((TextView) selectedView).setTextColor(Color.GRAY);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (view instanceof TextView) {
                    if (position == 0) {
                        ((TextView) view).setTextColor(Color.GRAY);
                        if (spinner.getId() == R.id.PriceRule_Spinner) {
                            price_Etxt.setText("");
                        }
                    } else {
                        ((TextView) view).setTextColor(Color.BLACK);
                        retrieveAndSetPrice(value);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void loadSpinnerData() {
        // Load Room Types
        roomTypes_DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomTypes_list.add("Select Room Type");
                for (DataSnapshot item : snapshot.getChildren()) {
                    roomTypes_list.add(item.getKey());
                }
                roomTypes_adapter.notifyDataSetChanged();

                if (roomsType_spinner.getTag() != null) {
                    String roomType = roomsType_spinner.getTag().toString();
                    int roomTypeIndex = getIndex(roomsType_spinner, roomType);
                    roomsType_spinner.setSelection(roomTypeIndex);
                    // After setting the selection, change the text color
                    changeSpinnerItemTextColor(roomsType_spinner, roomTypeIndex);
                    Log.d("Spinner", "Room Type Spinner set to: " + roomType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Room Types load cancelled", error.toException());
            }
        });

        // Load Price Rules
        priceRules_DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                priceRules_list.add("Select Price Rule");
                for (DataSnapshot item : snapshot.getChildren()) {
                    priceRules_list.add(item.getKey());
                }
                priceRules_adapter.notifyDataSetChanged();

                if (priceRules_spinner.getTag() != null) {
                    String priceRule = priceRules_spinner.getTag().toString();
                    int priceRuleIndex = getIndex(priceRules_spinner, priceRule);
                    priceRules_spinner.setSelection(priceRuleIndex);
                    // After setting the selection, change the text color
                    changeSpinnerItemTextColor(priceRules_spinner, priceRuleIndex);
                    Log.d("Spinner", "Price Rule Spinner set to: " + priceRule);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Price Rules load cancelled", error.toException());
            }
        });
    }


    private void disablePriceEtxt() {
        price_Etxt.setCursorVisible(false);
        price_Etxt.setClickable(false);
        price_Etxt.setLongClickable(false);
    }

    private void retrieveAndSetPrice(String priceRule) {
        if (priceRule == null || priceRule.equals("Select Price Rule")) {
            price_Etxt.setText("");
            return;
        }

        priceRules_DBref.child(priceRule).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_PriceRule modelPriceRule = snapshot.getValue(Model_PriceRule.class);
                    if (modelPriceRule != null) {
                        double price = modelPriceRule.getPrice();
                        price_Etxt.setText("₱" + String.format(Locale.US, "%.2f", price));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Price retrieval cancelled", error.toException());
            }
        });
    }


    private int getIndex(Spinner spinner, String text) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(text)) {
                return i; // Return the index if the text matches
            }
        }
        return 0; // Indicate that the item was not found
    }
}
