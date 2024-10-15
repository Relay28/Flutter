package com.example.flutter11;

import java.util.ArrayList;

public class ToDo {
    private int id;
    private String task, category, when, description;
    public static ArrayList<ToDo> noteArrayList = new ArrayList<>();

    public ToDo(int id, String when, String task, String category, String description) {
        this.id = id;
        this.when = when;
        this.task = task;
        this.category = category;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
