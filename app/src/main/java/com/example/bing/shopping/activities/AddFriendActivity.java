package com.example.bing.shopping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.User;
import com.example.bing.shopping.entities.UserFriends;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.GetUserFriendsService;
import com.example.bing.shopping.view.AddFriendView.AddFriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendActivity extends BaseActivity {

    private static final String SHOPPING_LIST_EXTRA_INFO = "SHOPPING_LIST_EXTRA_INFO";

    @BindView(R.id.activity_friend_listRecyclerView)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference friendReference;
    private UserFriends currentUserFriends;
    private String shoppingListId;

    public static Intent newIntent(Context context, String shoppingListId) {
        Intent intent = new Intent(context, AddFriendActivity.class);
        intent.putExtra(SHOPPING_LIST_EXTRA_INFO, shoppingListId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);

        shoppingListId = getIntent().getStringExtra(SHOPPING_LIST_EXTRA_INFO);
    }

    @Override
    protected void onResume() {
        super.onResume();

        friendReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Utils.FIRE_BASE_USER_FRIEND_REFERENCE + Utils.encodeEmail(userEmail));
        bus.post(new GetUserFriendsService.GetUserFriendsRequest(friendReference));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(Utils.FIRE_BASE_USER_REFERENCE);
        adapter = new FirebaseRecyclerAdapter<User, AddFriendViewHolder>(User.class,
                R.layout.friends_list, AddFriendViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(AddFriendViewHolder viewHolder, User model, int position) {
                viewHolder.populate(model);

                if(currentUserFriends.getUserFriends() != null ) {
                    if(isFriend(model)) {
                        viewHolder.listView.setImageResource(R.mipmap.ic_done);
                    } else {
                        viewHolder.listView.setImageResource(R.mipmap.ic_add);
                    }
                }

                viewHolder.listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userEmail.equals(model.getEmail())) {
                            Toast.makeText(getApplicationContext(), "You can not add yourself", Toast.LENGTH_LONG).show();
                        } else {
                            DatabaseReference userFriendReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    Utils.FIRE_BASE_USER_FRIEND_REFERENCE + Utils.encodeEmail(userEmail) + "/userFriends/" + Utils.encodeEmail(model.getEmail()));
                            DatabaseReference sharedReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    Utils.FIRE_BASE_USER_SHARED_WITH_REFERENCE + shoppingListId + "/sharedWith/" + Utils.encodeEmail(model.getEmail()));

                            if(isFriend(model)) {
                                userFriendReference.removeValue();
                                sharedReference.removeValue();
                                viewHolder.listView.setImageResource(R.mipmap.ic_add);
                            } else {
                                userFriendReference.setValue(model);
                                viewHolder.listView.setImageResource(R.mipmap.ic_done);
                            }
                        }


                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void getCurrentUserFriends(GetUserFriendsService.GetUserFriendsResponse response) {
        if(response.userFriends != null) {
            currentUserFriends = response.userFriends;
        } else {
            currentUserFriends = new UserFriends();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isFriend(User user){
        HashMap<String, User> userFriends = currentUserFriends.getUserFriends();
        return userFriends != null && userFriends.size() != 0 && userFriends.containsKey(Utils.encodeEmail(user.getEmail()));
    }
}
