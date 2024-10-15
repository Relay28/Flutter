package com.example.flutter11;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private RoundedImageView profileImageView;

    private DatabaseHelper databaseHelper;
    private String username;
    private TextView nameTextView,usernameTextView,ageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        if (intent != null && intent.hasExtra("user"))
            username = intent.getStringExtra("user");
        profileImageView = findViewById(R.id.r7wp4p2hgwx3);

        databaseHelper = DatabaseHelper.getInstance(this);


        nameTextView = findViewById(R.id.rv07605miotg);
        usernameTextView = findViewById(R.id.r4uitp6yj4op);
        ageTextView = findViewById(R.id.r0msxgeza99gp);
        profileImageView = findViewById(R.id.r7wp4p2hgwx3);

    loadData();

    }
    public void loadData(){
        String imagePath = databaseHelper.getUserImagePath(username);
        if (imagePath != null) {
            profileImageView.setImageURI(Uri.parse(imagePath));
        }
        String name = databaseHelper.getName(username);
        String age = databaseHelper.getAge(username);
        if(name!=null){
            nameTextView.setText(name);
        }
        if(age!=null){
            ageTextView.setText(age);
        }
        usernameTextView.setText(username);
    }

    public void back(View view) {
        Intent intent = new Intent(Profile.this,Home.class);
        intent.putExtra("user", username);
        startActivity(intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

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

    public void EditProf(View view) {
        Intent intent = new Intent(Profile.this,ProfileEdit.class);
        intent.putExtra("user", username);
        intent.putExtra("name", username);
        intent.putExtra("age", username);
        startActivity(intent);
        finish();
    }


    public void Logout(View view) {
        Intent intent = new Intent(Profile.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}