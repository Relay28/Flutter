package com.example.flutter11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

public class Statistics extends AppCompatActivity {
    AnyChartView anyChartView;

       private  String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Intent intent=getIntent();
        if (intent != null && intent.hasExtra("user")) {
            userID = intent.getStringExtra("user");
        }
        anyChartView = findViewById(R.id.anyChartView);
        setupChartView();
    }


    private void setupChartView() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        // Retrieve mood counts from the database
        int happyCount = dbHelper.getMoodCount("happy",userID);
        int neutralCount = dbHelper.getMoodCount("neutral",userID);
        int sadCount = dbHelper.getMoodCount("sad",userID);
        int angryCount = dbHelper.getMoodCount("angry",userID);

        // Add mood counts to dataEntries list
        dataEntries.add(new ValueDataEntry("Happy", happyCount));
        dataEntries.add(new ValueDataEntry("Neutral", neutralCount));
        dataEntries.add(new ValueDataEntry("Sad", sadCount));
        dataEntries.add(new ValueDataEntry("Angry", angryCount));

        pie.data(dataEntries);
        pie.title("Daily Moods");
        anyChartView.setChart(pie);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(Statistics.this,Profile.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }

    public void addNote(View view) {
        Intent intent = new Intent(Statistics.this,Note.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        Intent intent = new Intent(Statistics.this,Home.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }

    public void ob(View view) {
        Intent intent = new Intent(Statistics.this,Home.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }
}