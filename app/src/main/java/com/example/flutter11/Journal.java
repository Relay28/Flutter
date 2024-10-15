package com.example.flutter11;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Journal extends AppCompatActivity {
    private ListView noteListView;
    private String userID;
    private NoteCell noteCellAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Retrieve userID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            userID = intent.getStringExtra("user");
        } else {
            // Handle the case where userID is not passed, possibly redirect to login
            Intent loginIntent = new Intent(this, Login.class);
            startActivity(loginIntent);
            finish(); // Finish the current activity
            return;
        }
        loadFromDBToMembory();
        initWidgets();
        setNoteCell();
        setListItemClickListener();
    }

    private void loadFromDBToMembory() {
        DatabaseHelper sqliteManager = DatabaseHelper.getInstance(this);
        sqliteManager.populateNoteListArray(userID);
    }

    private void initWidgets() {
        noteListView = findViewById(R.id.noteList);
    }

    private void setNoteCell() {
        noteCellAdapter = new NoteCell(getApplicationContext(), N.noteArrayList);
        noteListView.setAdapter(noteCellAdapter);
    }

    private void setListItemClickListener() {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                N selectedNote = (N) parent.getItemAtPosition(position);
                showNoteDetails(selectedNote);
            }
        });
    }

    private void showNoteDetails(final N note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(note.getTitle());
        builder.setMessage(note.getDescription());
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editNote(note);
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(note);
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

    private void editNote(final N note) {
        Intent intent = new Intent(Journal.this, Note.class);
        intent.putExtra("note_id", note.getId());
        intent.putExtra("note_title", note.getTitle());
        intent.putExtra("note_description", note.getDescription());

        intent.putExtra("user", userID);
        startActivity(intent); // Start Note activity for editing
    }

    private void deleteNote(final N note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the note from the database
                DatabaseHelper sqliteManager = DatabaseHelper.getInstance(Journal.this);
                sqliteManager.deleteNote(note.getId());

                // Remove the note from the list and update the adapter
                N.noteArrayList.remove(note);
                noteCellAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void newNote(View view) {
        Intent intent = new Intent(this, Note.class);

        // Pass userID to Note activity
        intent.putExtra("user", userID);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the adapter to update the ListView
        noteCellAdapter.notifyDataSetChanged();
    }

    public void back(View view) {
        Intent intent = new Intent(Journal.this,Home.class);
        intent.putExtra("user", userID);
        startActivity(intent);
        finish();
    }
}
