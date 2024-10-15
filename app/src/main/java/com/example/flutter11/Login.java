package com.example.flutter11;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Button login;
    EditText user,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper = DatabaseHelper.getInstance(this);

        user = findViewById(R.id.logUser);
        pass = findViewById(R.id.logPass);
        login =findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User =user.getText().toString();
                String Pass = pass.getText().toString();
                if (User.isEmpty() || Pass.isEmpty()) {
                    Toast.makeText(Login.this, "Fill up All the Fields", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean check = databaseHelper.checkUserPassword(User,Pass);
                    if(check){
                        Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Home.class);
                        intent.putExtra("user", User);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(Login.this, "Incorrect Password or Username", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
