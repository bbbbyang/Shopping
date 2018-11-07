package com.example.bing.shopping.live;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.bing.shopping.entities.SharedWith;
import com.example.bing.shopping.entities.UserFriends;
import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.services.GetUserFriendsService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

public class LiveUserFriendsService extends BaseLiveService {

    public LiveUserFriendsService(ShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void getUserFriends(GetUserFriendsService.GetUserFriendsRequest request) {
        GetUserFriendsService.GetUserFriendsResponse response = new GetUserFriendsService.GetUserFriendsResponse();
        request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                response.userFriends = dataSnapshot.getValue(UserFriends.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe
    public void getUserSharedFriends(GetUserFriendsService.GetUserSharedFriendsListRequest request) {
        GetUserFriendsService.GetUserSharedFriendsListResponse response = new GetUserFriendsService.GetUserSharedFriendsListResponse();
        request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                response.sharedWith = dataSnapshot.getValue(SharedWith.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
