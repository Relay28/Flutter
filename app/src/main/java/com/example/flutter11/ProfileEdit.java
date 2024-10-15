package com.example.flutter11;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ProfileEdit extends AppCompatActivity {
    private String username;
    private static final int PICK_IMAGE_REQUEST = 1;
    private RoundedImageView profileImageView; // Changed to RoundedImageView
    private TextView editProfileImageTextView;
    private DatabaseHelper databaseHelper;
    private EditText nameEditText, ageEditText;
    private Button saveButton;
    private TextView userTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Get the username from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            username = intent.getStringExtra("user");
        }

        // Initialize views and database helper
        profileImageView = findViewById(R.id.r7wp4p2hgwx3);
        editProfileImageTextView = findViewById(R.id.rv0frbhw52n);
        nameEditText = findViewById(R.id.fieldName);
        editProfileImageTextView.setOnClickListener(view -> openImagePicker());
        ageEditText = findViewById(R.id.fieldAge);
        userTextView = findViewById(R.id.fieldUsername);
        saveButton = findViewById(R.id.rqgc05ns9xra);
        databaseHelper = DatabaseHelper.getInstance(this);

        // Load user data and display in EditText fields
        loadUserData();

        // Set OnClickListener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap); // Set bitmap directly to profileImageView

                // Save the image to internal storage
                String imagePath = ImageUtils.saveImageToInternalStorage(this, bitmap);

                // Update the image path in the database
                if (imagePath != null) {
                    databaseHelper.updateUserImagePath(username, imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to load user data from the database and display in EditText fields
    private void loadUserData() {
        String imagePath = databaseHelper.getUserImagePath(username);
        if (imagePath != null) {
            profileImageView.setImageURI(Uri.parse(imagePath));
        }
        String name = databaseHelper.getName(username);
        String age = databaseHelper.getAge(username);
        if (name != null) {
            nameEditText.setText(name);
        }
        if (age != null) {
            ageEditText.setText(age);
        }
        userTextView.setText(username);
    }

    // Method to save edited user data to the database
    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        if (!name.isEmpty() && !ageStr.isEmpty()) {
            boolean isUpdated = databaseHelper.updateUser(username, ageStr, name);
            if (isUpdated) {
                Intent intent = new Intent(ProfileEdit.this, Profile.class);
                intent.putExtra("user", username);
                startActivity(intent);
                finish();
            } else {
                // Failed to update user data
                // Handle accordingly
            }
        }
    }

    public void back(View view) {
        Intent intent = new Intent(ProfileEdit.this, Profile.class);
        intent.putExtra("user", username);
        startActivity(intent);
        finish();
    }
}
