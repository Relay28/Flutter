package com.example.flutter11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarView;

import java.util.Calendar;

public class Calendars extends AppCompatActivity {

    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 101;
    private String userID;
    private DatabaseHelper dbHelper;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Get the user ID from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            userID = intent.getStringExtra("user");
        }

        // Initialize calendar view
        calendarView = findViewById(R.id.calendarView2);

        // Check if calendar permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    CALENDAR_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed
            initializeCalendar();
        }
    }

    private void initializeCalendar() {
        // Instantiate CustomDayDecorator
        CustomDayDecorator customDayDecorator = new CustomDayDecorator(this, dbHelper, userID);

        // Decorate the calendar view
        customDayDecorator.decorateCalendar(calendarView);

        // Set listener to handle day clicks
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar calendarDay = eventDay.getCalendar();
            // Handle clicks on calendar days
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the calendar
                initializeCalendar();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void back(View view) {
        Intent intent = new Intent(Calendars.this, Home.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }
}
