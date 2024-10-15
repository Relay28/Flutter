package com.example.flutter11;// User.java

public class User {
    private String username;
    private String password;
    private int age;
    private String name;
    private String imagePath; // Add imagePath field

    public User(String username, String password, int age, String name, String imagePath) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.name = name;
        this.imagePath = imagePath;
    }

    // Getter methods for all fields
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    // Getter method for imagePath
    public String getImagePath() {
        return imagePath;
    }
}
