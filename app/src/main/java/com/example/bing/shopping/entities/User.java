package com.example.bing.shopping.entities;

import java.util.HashMap;

public class User {
    private String name;
    private String email;
    private HashMap<String, Object> timeJoined;
    private boolean hasLoggedInWithPassword;

    public User() {
    }

    public User(String name, String email, HashMap<String, Object> timeJoined, boolean hasLoggedInWithPassword) {
        this.name = name;
        this.email = email;
        this.timeJoined = timeJoined;
        this.hasLoggedInWithPassword = hasLoggedInWithPassword;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimeJoined() {
        return timeJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }
}
