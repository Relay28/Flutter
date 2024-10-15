package com.example.flutter11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomDayDecorator {

    private final Context mContext;
    private final DatabaseHelper mDbHelper;
    private final String mUsername;

    public CustomDayDecorator(Context context, DatabaseHelper dbHelper, String username) {
        mContext = context;
        mDbHelper = dbHelper;
        mUsername = username;
    }

    public void decorateCalendar(CalendarView calendarView) {
        List<EventDay> eventDays = getDueDates();
        calendarView.setEvents(eventDays);
    }

    private List<EventDay> getDueDates() {
        List<EventDay> eventDays = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Cursor cursor = mDbHelper.getAllDueDates(mUsername);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String dueDate = cursor.getString(cursor.getColumnIndex("due"));
                    Cursor cursor2 = mDbHelper.getCate(mUsername, dueDate);
                    if (cursor2 != null) {
                        try {
                            if (cursor2.moveToFirst()) {
                                String category = cursor2.getString(cursor2.getColumnIndex("category"));
                                if (dueDate != null && category != null) {
                                    try {
                                        Date dueDateParsed = dateFormat.parse(dueDate);
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(dueDateParsed);
                                        int drawableResId;
                                        switch (category) {
                                            case "Work":
                                                drawableResId = R.drawable.work;
                                                break;
                                            case "Personal":
                                                drawableResId = R.drawable.personal;
                                                break;
                                            case "Chores":
                                                drawableResId = R.drawable.chores;
                                                break;
                                            case "School":
                                                drawableResId = R.drawable.school;
                                                break;
                                            default:
                                                drawableResId = R.drawable.hehe;
                                                break;
                                        }
                                        eventDays.add(new EventDay(calendar, drawableResId));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } finally {
                            cursor2.close();
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return eventDays;
    }

}
