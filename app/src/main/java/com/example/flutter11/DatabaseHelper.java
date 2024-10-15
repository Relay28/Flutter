package com.example.flutter11;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    public static final String dbFL = "Flutter1.3.db";
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public DatabaseHelper(@Nullable Context context) {
        super(context, dbFL, null, 1);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }



    @Override
    public void onCreate(SQLiteDatabase myDatabase) {
        try {
            myDatabase.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT, age INTEGER, gender TEXT, sad INTEGER, happy INTEGER, angry INTEGER, neutral INTEGER, image_path TEXT)");
            myDatabase.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, deleted DATE, username TEXT, FOREIGN KEY(username) REFERENCES users(username))");
            myDatabase.execSQL("CREATE TABLE todo (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT,category TEXT,due TEXT, username TEXT, FOREIGN KEY(username) REFERENCES users(username))");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating database tables: " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase myDatabase, int oldVersion, int newVersion) {
        myDatabase.execSQL("DROP TABLE IF EXISTS notes");
        myDatabase.execSQL("DROP TABLE IF EXISTS users");
        myDatabase.execSQL("DROP TABLE IF EXISTS todo");

    }
    public ArrayList<ToDo> getToDoItemsByUsername(String username) {
        ArrayList<ToDo> toDoItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("todo", null, "username = ?", new String[]{username}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String when = cursor.getString(cursor.getColumnIndex("due"));
                @SuppressLint("Range") String task = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                toDoItems.add(new ToDo(id, when, task, category, description));
            }
            cursor.close();
        }
        return toDoItems;
    }

    public void deleteToDoItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("todo", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllDueDates(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"due"};
        String selection = "username = ?";
        String[] selectionArgs = {userID};
        return db.query("todo", columns, selection, selectionArgs, null, null, null);
    }
    public boolean updateToDoData(int id, String title, String description, String category, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("category", category);
        contentValues.put("due", dueDate); // This line seems redundant, remove if necessary


        int result = db.update("todo", contentValues, "id = ?", new String[]{String.valueOf(id)});
        return result > 0; // returns true if updated successfully, false otherwise
    }



    public ToDo getToDoItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("todo", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String when = cursor.getString(cursor.getColumnIndex("due"));
            @SuppressLint("Range") String task = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            cursor.close();
            return new ToDo(id, when, task, category, description);
        }
        return null;
    }


    // Methods for the users table
    public boolean insertUserData(String username, String password, int age, String gender) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("age", age);
        contentValues.put("gender", gender);
        contentValues.put("happy", 0);
        contentValues.put("sad", 0);
        contentValues.put("angry", 0);
        contentValues.put("neutral", 0);
        long result = myDB.insert("users", null, contentValues);
        return result != -1;
    }
    public boolean insertToDoData(String title, String description, String category, String dueDate, String username) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("category", category);
        contentValues.put("due", dueDate);
        contentValues.put("username", username);
        long result = myDB.insert("todo", null, contentValues);
        return result != -1;
    }


    public Cursor getCate(String username,String due) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Define the SQL query to retrieve categories associated with due dates
        String query = "SELECT category FROM todo WHERE username = ? and due = ?";

        try {
            // Execute the query
            cursor = db.rawQuery(query, new String[]{username, due});
        } catch (SQLiteException e) {
            Log.e("DatabaseHelper", "Error executing getCate query: " + e.getMessage());
        }

        return cursor;
    }

    public boolean incrementMoodCount(String username, String mood) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT " + mood + " FROM users WHERE username = ?", new String[]{username});

        int currentCount = 0;
        if (cursor.moveToFirst()) {
            currentCount = cursor.getInt(0);
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(mood, currentCount + 1);

        int result = myDB.update("users", contentValues, "username = ?", new String[]{username});
        return result > 0;
    }


    public Boolean checkUsername(String username) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean updateUser(String username, String age, String gender) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("age", age);
        contentValues.put("gender", gender);
        int result = myDB.update("users", contentValues, "username = ?", new String[]{username});
        return result > 0;
    }



    public Boolean checkUserPassword(String username, String password) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }
    public int getMoodCount(String mood, String userID) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT "+mood+" FROM users WHERE username=?", new String[]{userID});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }



    public void addNoteToDatabase(N note, String userID) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", note.getTitle());
        contentValues.put("description", note.getDescription());
        contentValues.put("deleted", getStringFromDate(note.getAdded()));
        contentValues.put("username", userID);
        database.insert("notes", null, contentValues);
    }


    public void populateNoteListArray(String userID) {
        N.noteArrayList.clear(); // Clear the list before populating it with new data
        SQLiteDatabase database = this.getReadableDatabase();
        try (Cursor result = database.rawQuery("SELECT * FROM notes WHERE username = ?", new String[]{userID})) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int id = result.getInt(result.getColumnIndexOrThrow("id"));
                    String title = result.getString(result.getColumnIndexOrThrow("title"));
                    String description = result.getString(result.getColumnIndexOrThrow("description"));
                    String dateStr = result.getString(result.getColumnIndexOrThrow("deleted"));
                    Date date = getDateFromString(dateStr);

                    N note = new N(id, title, description, date);
                    N.noteArrayList.add(note);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getStringFromDate(Date date) {
        return date == null ? null : dateFormat.format(date);
    }

    public void updateNote(N note, String userID) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", note.getTitle());
        contentValues.put("description", note.getDescription());
        contentValues.put("deleted", getStringFromDate(note.getAdded()));
        contentValues.put("username", userID);
        database.update("notes", contentValues, "id=?", new String[]{String.valueOf(note.getId())});
    }


    public void deleteNote(int noteId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("notes", "id=?", new String[]{String.valueOf(noteId)});
    }

    private Date getDateFromString(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM users");
        database.execSQL("DELETE FROM notes");
    }
    public boolean updateUserImagePath(String username, String imagePath) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("image_path", imagePath);
        int result = myDB.update("users", contentValues, "username = ?", new String[]{username});
        return result > 0;
    }

    public String getUserImagePath(String username) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT image_path FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex("image_path"));
            cursor.close();
            return imagePath;
        }
        cursor.close();
        return null;
    }
    public String getName(String username) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT gender FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("gender"));
            cursor.close();
            return name;
        }
        cursor.close();
        return null;
    }

    public String getAge(String username) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT age FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String age = cursor.getString(cursor.getColumnIndex("age"));
            cursor.close();
            return age;
        }
        cursor.close();
        return null;
    }



}
