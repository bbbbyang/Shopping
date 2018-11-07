package com.example.bing.shopping.entities;

import java.util.HashMap;

public class UserFriends {

    private HashMap<String, User> userFriends;

    public UserFriends() {
    }

    public UserFriends(HashMap<String, User> userFriends) {
        this.userFriends = userFriends;
    }

    public HashMap<String, User> getUserFriends() {
        return userFriends;
    }
}
