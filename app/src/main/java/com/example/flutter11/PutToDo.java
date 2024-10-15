package com.example.flutter11;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PutToDo extends AppCompatActivity {

    private String username;
    private int taskId = -1;
    private TextView selectedDateTV;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner categorySpinner;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_to_do);

        selectedDateTV = findViewById(R.id.dat);
        titleEditText = findViewById(R.id.rpymzp2apnzn);
        descriptionEditText = findViewById(R.id.ry47vhg3sia);
        categorySpinner = findViewById(R.id.spinner);

        dbHelper = DatabaseHelper.getInstance(this);

        String[] choices = {"Work", "School", "Personal", "Chores"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        createNotificationChannel();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("user")) {
                username = intent.getStringExtra("user");
            }
            if (intent.hasExtra("task_id")) {
                taskId = intent.getIntExtra("task_id", -1);
                populateFields(taskId);
            }
        }
    }

    private void populateFields(int taskId) {
        ToDo toDo = dbHelper.getToDoItemById(taskId);
        if (toDo != null) {
            titleEditText.setText(toDo.getTask());
            descriptionEditText.setText(toDo.getDescription());
            selectedDateTV.setText(toDo.getWhen());
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
            int position = adapter.getPosition(toDo.getCategory());
            categorySpinner.setSelection(position);
        }
    }

    public void selectdate(View view) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PutToDo.this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Format the selected date
                        String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year);
                        selectedDateTV.setText(formattedDate);
                    }
                },
                year, month, day);


        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }



    public void addNewTask(View view) {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String dueDate = selectedDateTV.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || category.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted;
        if (taskId == -1) {
            isInserted = dbHelper.insertToDoData(title, description, category, dueDate, username);
        } else {
            isInserted = dbHelper.updateToDoData(taskId, title, description, category, dueDate);
        }

        if (isInserted) {
            Toast.makeText(this, "To-do task saved successfully", Toast.LENGTH_SHORT).show();


            if (!dueDate.isEmpty()) {
                // Parse the due date
                String[] dateParts = dueDate.split("-");
                int year = Integer.parseInt(dateParts[2]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int day = Integer.parseInt(dateParts[0]);


                Calendar reminderCalendar = Calendar.getInstance();
                reminderCalendar.set(Calendar.YEAR, year);
                reminderCalendar.set(Calendar.MONTH, month);
                reminderCalendar.set(Calendar.DAY_OF_MONTH, day);


                reminderCalendar.set(Calendar.HOUR_OF_DAY, 8);
                reminderCalendar.set(Calendar.MINUTE, 0);
                reminderCalendar.set(Calendar.SECOND, 0);

                setReminder(reminderCalendar.getTimeInMillis());
            }

            Intent intent = new Intent(PutToDo.this, ToDoList.class);
            intent.putExtra("user", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to save to-do task", Toast.LENGTH_SHORT).show();
        }
    }
    private void setReminder(long reminderTimeInMillis) {
        // Create an intent to trigger the reminder
        Intent intent = new Intent(this, ReminderBroadcast.class);

        // Use FLAG_IMMUTABLE for PendingIntent since it doesn't require mutability
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Set the reminder using AlarmManager
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FlutterChannel";
            String description = "Channel for Flutter Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("flutter_channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void back(View view) {
        Intent intent = new Intent(PutToDo.this,Home.class);
        intent.putExtra("user", username);
        startActivity(intent);
        finish();
    }




}
