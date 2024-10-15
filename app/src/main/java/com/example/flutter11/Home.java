    package com.example.flutter11;

    import androidx.appcompat.app.AppCompatActivity;

    import android.app.AlarmManager;
    import android.app.AlertDialog;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.Toast;

    import java.util.Calendar;

    public class Home extends AppCompatActivity {
        private ListView noteListView;
        private NoteCell noteCellAdapter;
        private String userID;
        ImageView prof1,prof2;
        RoundedImageView tab;
        private DatabaseHelper databaseHelper;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("user")) {
                userID = intent.getStringExtra("user");
            }

            databaseHelper = DatabaseHelper.getInstance(this);
            initWidgets();
            setListItemClickListener();
            setNoteCell();
            tab =findViewById(R.id.profTab);

            //prof1=findViewById(R.id.profTab);
            prof2=findViewById(R.id.profile);
            String imagePath = databaseHelper.getUserImagePath(userID);
            if (imagePath != null) {
                tab.setImageURI(Uri.parse(imagePath));
                //tab.onDraw(this);
                prof2.setImageURI(Uri.parse(imagePath));
            }
            createNotificationChannel();

        }

        @Override
        protected void onResume() {
            super.onResume();
            // Clear the noteArrayList and reload data from the database
            N.noteArrayList.clear();
            loadFromDBToMemory();
        }

        private void loadFromDBToMemory() {
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
            noteListView.setOnItemClickListener((parent, view, position, id) -> {
                N selectedNote = (N) parent.getItemAtPosition(position);
                showNoteDetails(selectedNote);
            });
        }

        private void showNoteDetails(final N note) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(note.getTitle());
            builder.setMessage(note.getDescription());
            builder.setPositiveButton("Edit", (dialog, which) -> editNote(note));
            builder.setNegativeButton("Delete", (dialog, which) -> deleteNoteDialog(note));
            builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        private void editNote(final N note) {
            Intent intent = new Intent(Home.this, Note.class);
            intent.putExtra("note_id", note.getId());
            intent.putExtra("note_title", note.getTitle());
            intent.putExtra("note_description", note.getDescription());
            intent.putExtra("user", userID);
            startActivity(intent); // Start Note activity for editing
        }

        private void deleteNoteDialog(final N note) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this note?");
            builder.setPositiveButton("Yes", (dialog, which) -> deleteNoteAction(note));
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        private void deleteNoteAction(final N note) {
            // Remove the note from the database
            DatabaseHelper sqliteManager = DatabaseHelper.getInstance(this);
            sqliteManager.deleteNote(note.getId());

            // Remove the note from the list and update the adapter
            N.noteArrayList.remove(note);
            noteCellAdapter.notifyDataSetChanged();
        }

        public void openJournal(View view) {
            Intent intent = new Intent(Home.this,Journal.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

        public void openProfile(View view) {
            Intent intent = new Intent(Home.this,Profile.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

        public void addNote(View view) {
            Intent intent = new Intent(Home.this,Note.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

        public void openCalendar(View view) {
            Intent intent = new Intent(Home.this, Calendars.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

        public void goStats(View view) {
            Intent intent = new Intent(Home.this, Statistics.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }


        public void addMoodHappy(View view) {

            addM("happy");
        }

        public void addMoodAngry(View view) {

            addM("angry");
        }

        public void addMoodSad(View view) {

            addM("sad");
        }

        public void addMoodNeutral(View view) {
        ;
            addM("neutral");
        }
        public void addM(String mood){
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
            boolean success = databaseHelper.incrementMoodCount(userID, mood);
            if (success) {

                Toast.makeText(this, "Mood updated successfully", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Failed to update mood", Toast.LENGTH_SHORT).show();
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
        public void goToDo(View view) {
            Intent intent = new Intent(Home.this, ToDoList.class);
            intent.putExtra("user", userID);
            startActivity(intent);
            finish();
        }

    }
