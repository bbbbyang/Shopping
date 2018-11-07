package com.example.bing.shopping.services;

import com.example.bing.shopping.entities.SharedWith;
import com.example.bing.shopping.entities.UserFriends;
import com.google.firebase.database.DatabaseReference;

public class GetUserFriendsService {

    public GetUserFriendsService() {
    }

    public static class GetUserFriendsRequest {
        public DatabaseReference reference;

        public GetUserFriendsRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }

    public static class GetUserFriendsResponse {
        public UserFriends userFriends;
    }

    public static class GetUserSharedFriendsListRequest {
        public DatabaseReference reference;

        public GetUserSharedFriendsListRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }

    public static class GetUserSharedFriendsListResponse {
        public SharedWith sharedWith;
    }
}
