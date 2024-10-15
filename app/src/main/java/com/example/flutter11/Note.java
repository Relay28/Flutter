package com.example.flutter11;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class Note extends AppCompatActivity {
    private EditText titleEdit;
    private EditText descEdit;
    private boolean isEditMode = false;
    private int noteId;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initWidgets();

        // Retrieve userID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            userID = intent.getStringExtra("user");
        }

        // Check if we are editing an existing note
        if (getIntent().hasExtra("note_id")) {
            isEditMode = true;
            noteId = getIntent().getIntExtra("note_id", -1);
            titleEdit.setText(getIntent().getStringExtra("note_title"));
            descEdit.setText(getIntent().getStringExtra("note_description"));
        }
    }

    private void initWidgets() {
        titleEdit = findViewById(R.id.titleEdit);
        descEdit = findViewById(R.id.descEdit);
    }

    public void saveNote(View view) {
        DatabaseHelper sqliteManager = DatabaseHelper.getInstance(this);
        Date currentTime = Calendar.getInstance().getTime();
        String title = titleEdit.getText().toString();
        String desc = descEdit.getText().toString();

        if (isEditMode) {
            // Update existing note
            for (N note : N.noteArrayList) {
                if (note.getId() == noteId) {
                    note.setTitle(title);
                    note.setDescription(desc);
                    note.setAdded(currentTime);
                    sqliteManager.updateNote(note, userID);
                    break;
                }
            }
        } else {
            // Add new note
            N newNote = new N(0, title, desc, currentTime); // Set id to 0, as it will be auto-generated
            sqliteManager.addNoteToDatabase(newNote, userID);
            N.noteArrayList.add(newNote);
            Intent intent = new Intent(Note.this,Journal.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

        finish();
    }

    public void back(View view) {
        Intent intent = new Intent(Note.this,Journal.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }

}
