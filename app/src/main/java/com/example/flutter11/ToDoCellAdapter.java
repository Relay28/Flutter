package com.example.flutter11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ToDoCellAdapter extends ArrayAdapter<ToDo> {
    public ToDoCellAdapter(Context context, List<ToDo> notes) {
        super(context, 0, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ToDo note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.txtTask);
        TextView desc = convertView.findViewById(R.id.txtCategory);
        TextView date = convertView.findViewById(R.id.txtDate);

        title.setText(note.getTask());
        desc.setText(note.getCategory());
        date.setText(note.getWhen());

        return convertView;
    }
}
