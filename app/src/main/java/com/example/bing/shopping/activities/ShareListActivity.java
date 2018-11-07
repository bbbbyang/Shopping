package com.example.bing.shopping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.SharedWith;
import com.example.bing.shopping.entities.ShoppingList;
import com.example.bing.shopping.entities.User;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.GetUserFriendsService;
import com.example.bing.shopping.services.ShoppingListService;
import com.example.bing.shopping.view.ShareListView.ShareListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareListActivity extends BaseActivity {

    private static final String SHOPPING_LIST_EXTRA_INFO = "SHOPPING_LIST_EXTRA_INFO";

    @BindView(R.id.activity_share_listRecyclerView)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter adapter;
    private String shoppingListId;
    private DatabaseReference sharedReference;
    private SharedWith sharedWith;
    private ShoppingList shoppingList;

    public static Intent newIntent(Context context, String shoppingListId) {
        Intent intent = new Intent(context, ShareListActivity.class);
        intent.putExtra(SHOPPING_LIST_EXTRA_INFO, shoppingListId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        ButterKnife.bind(this);

        shoppingListId = getIntent().getStringExtra(SHOPPING_LIST_EXTRA_INFO);
        sharedReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Utils.FIRE_BASE_USER_SHARED_WITH_REFERENCE + shoppingListId);
        bus.post(new GetUserFriendsService.GetUserSharedFriendsListRequest(sharedReference));
        bus.post(new ShoppingListService.GetCurrentShoppingListRequest(shoppingListId, userEmail));
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_USER_FRIEND_REFERENCE + Utils.encodeEmail(userEmail) + "/userFriends");
        adapter = new FirebaseRecyclerAdapter<User,ShareListViewHolder>(User.class,
                R.layout.share_list, ShareListViewHolder.class, friendReference) {
            @Override
            protected void populateViewHolder(ShareListViewHolder viewHolder, User model, int position) {
                viewHolder.populate(model);

                if(sharedWith.getSharedWith() != null) {
                    if(isSharedFriend(model)) {
                        viewHolder.listView.setImageResource(R.mipmap.ic_done);
                    } else {
                        viewHolder.listView.setImageResource(R.mipmap.ic_add);
                    }
                }

                viewHolder.listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference sharedReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                Utils.FIRE_BASE_USER_SHARED_WITH_REFERENCE + shoppingListId + "/sharedWith/" + Utils.encodeEmail(model.getEmail()));
                        DatabaseReference friendShoppingListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(model.getEmail()) + "/" + shoppingListId);


                        if(isSharedFriend(model)) {
                            sharedReference.removeValue();
                            friendShoppingListReference.removeValue();
                            viewHolder.listView.setImageResource(R.mipmap.ic_add);
                            updateAllShoppingListReference(true);
                        } else {
                            sharedReference.setValue(model);
                            friendShoppingListReference.setValue(shoppingList);
                            viewHolder.listView.setImageResource(R.mipmap.ic_done);
                            updateAllShoppingListReference(false);
                        }
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friends:
                startActivity(AddFriendActivity.newIntent(this, shoppingListId));
        }
        return true;
    }

    @Subscribe
    public void getUserSharedFriendsList(GetUserFriendsService.GetUserSharedFriendsListResponse response) {
        if(response.sharedWith != null) {
            sharedWith = response.sharedWith;
        } else {
            sharedWith = new SharedWith();
        }
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListResponse response) {
        shoppingList = response.shoppingList;
    }

    private boolean isSharedFriend(User user) {
        return sharedWith.getSharedWith() != null && sharedWith.getSharedWith().size() != 0 && sharedWith.getSharedWith().containsKey(Utils.encodeEmail(user.getEmail()));
    }

    private void updateAllShoppingListReference(boolean deletingList) {
        if(sharedWith.getSharedWith() != null && sharedWith.getSharedWith().isEmpty()) {
            for(User user : sharedWith.getSharedWith().values()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                        Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(user.getEmail()) + "/" + shoppingListId);

                if(!deletingList) {
                    bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(reference));
                }
            }
        }

        DatabaseReference ownerReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + "/" + Utils.encodeEmail(userEmail) + "/" + shoppingListId);

        bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(ownerReference));
    }
}
