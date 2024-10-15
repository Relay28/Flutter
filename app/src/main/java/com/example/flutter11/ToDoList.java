package com.example.flutter11;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ToDoList extends AppCompatActivity {
    private String username;
    private ListView listView;
    private ToDoCellAdapter adapter;
    private ArrayList<ToDo> toDoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            username = intent.getStringExtra("user");
        }

        listView = findViewById(R.id.list_view);

        // Initialize toDoItems
        toDoItems = new ArrayList<>();

        // Initialize the adapter
        adapter = new ToDoCellAdapter(this, toDoItems);

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        // Set the item click listener
        setListItemClickListener();
        createNotificationChannel();
        // Populate the to-do list from the database
        populateToDoList();
    }

    private void setListItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo selectedToDo = (ToDo) parent.getItemAtPosition(position);
                showToDoDetails(selectedToDo);
            }
        });
    }

    private void showToDoDetails(final ToDo toDo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(toDo.getTask());
        builder.setMessage(toDo.getDescription());
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editToDoItem(toDo);
            }
        });
        builder.setNegativeButton("Done Task", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteToDoItem(toDo);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void editToDoItem(ToDo toDo) {
        Intent intent = new Intent(ToDoList.this, PutToDo.class);
        intent.putExtra("task_id", toDo.getId());
        intent.putExtra("user", username);
        startActivity(intent);
    }

    private void deleteToDoItem(ToDo toDo) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.deleteToDoItem(toDo.getId());
        populateToDoList();
    }

    private void populateToDoList() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        // Get to-do items from the database and update the list
        toDoItems.clear();
        toDoItems.addAll(dbHelper.getToDoItemsByUsername(username));
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }

    public void addToDo(View view) {
        Intent intent = new Intent(ToDoList.this, PutToDo.class);
        intent.putExtra("user", username);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        Intent intent = new Intent(ToDoList.this,Home.class);
        intent.putExtra("user", username);
        startActivity(intent);
        finish();
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
}
