package com.example.flutter11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    EditText User, Pass, age, gender;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        User = findViewById(R.id.txtUser);
        Pass = findViewById(R.id.txtPass);
        age = findViewById(R.id.txtAge);
        gender = findViewById(R.id.txtGender);
        register = findViewById(R.id.btnregister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = User.getText().toString();
                String password = Pass.getText().toString();
                String ageText = age.getText().toString();
                String genderText = gender.getText().toString();

                if (username.isEmpty() || password.isEmpty() || ageText.isEmpty() || genderText.isEmpty()) {
                    Toast.makeText(Register.this, "Fill up All the Fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int Age = Integer.parseInt(ageText);
                        if (Age <= 0) {
                            Toast.makeText(Register.this, "Invalid Age", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!databaseHelper.checkUsername(username)) {
                                if (databaseHelper.insertUserData(username, password, Age, genderText)) {
                                    Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Register.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Register.this, "User already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(Register.this, "Invalid Age", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void back(View view) {

        finish();
    }
}
