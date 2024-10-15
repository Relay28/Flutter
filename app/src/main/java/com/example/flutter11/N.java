package com.example.flutter11;

import java.util.ArrayList;
import java.util.Date;

public class N {
    private int id;
    private String title;
    private String description;
    private Date deleted;
    private Date added;

    public static ArrayList<N> noteArrayList = new ArrayList<>();

    public N(int id, String title, String description, Date added) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.added = added;
        this.deleted = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }
}
