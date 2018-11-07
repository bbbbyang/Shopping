package com.example.bing.shopping.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bing.shopping.R;
import com.example.bing.shopping.dialog.AddItemDialogFragment;
import com.example.bing.shopping.dialog.ChangeItemNameDialogFragment;
import com.example.bing.shopping.dialog.ChangeListNameDialogFragment;
import com.example.bing.shopping.dialog.DeleteItemDialogFragment;
import com.example.bing.shopping.dialog.DeleteListDialogFragment;
import com.example.bing.shopping.entities.Item;
import com.example.bing.shopping.entities.ShoppingList;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.ShoppingItemService;
import com.example.bing.shopping.services.ShoppingListService;
import com.example.bing.shopping.view.ShoppingItemView.ShoppingItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListDetailsActivity extends BaseActivity {

    public static final String SHOPPING_LIST_DETAILS = "SHOPPING_LIST_DETAILS";

    @BindView(R.id.activity_list_details_FAB)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.activity_list_listRecyclerView)
    RecyclerView recyclerView;

    FirebaseRecyclerAdapter adapter;

    private String shoppingId;
    private String shoppingName;
    private String shoppingOwner;
    private ShoppingList currentShoppingList;

    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo) {
        Intent intent = new Intent(context, ListDetailsActivity.class);
        intent.putStringArrayListExtra(SHOPPING_LIST_DETAILS, shoppingListInfo);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        ButterKnife.bind(this);

        shoppingId = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(0);
        shoppingName = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(1);
        shoppingOwner = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(2);

        bus.post(new ShoppingListService.GetCurrentShoppingListRequest(shoppingId, userEmail));

        getSupportActionBar().setTitle(shoppingName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + shoppingId);

        adapter = new FirebaseRecyclerAdapter<Item, ShoppingItemViewHolder>(Item.class,
                R.layout.shopping_item, ShoppingItemViewHolder.class, reference) {

            @Override
            protected void populateViewHolder(ShoppingItemViewHolder viewHolder, Item model, int position) {
                viewHolder.populate(model, getApplicationContext(), userEmail);

                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(shoppingOwner.equals(userEmail) | model.getOwnerEmail().equals(userEmail)) {
                            ArrayList<String> shoppingItemInfo = new ArrayList<>();
                            shoppingItemInfo.add(shoppingId);
                            shoppingItemInfo.add(model.getId());
                            shoppingItemInfo.add(model.getItemName());
                            DialogFragment dialogFragment = ChangeItemNameDialogFragment.newInstance(shoppingItemInfo);
                            dialogFragment.show(getFragmentManager(), ChangeItemNameDialogFragment.class.getSimpleName());
                            return true;
                        } else {
                            Toast.makeText(getApplicationContext(), "Only the owner of the list or item can rename it.", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }
                });

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!model.isBought() | model.getBoughtBy().equals(userEmail)) {
                            bus.post(new ShoppingItemService.ChangeShoppingItemStatusRequest(model, userEmail, shoppingId));
                        } else {
                            Toast.makeText(getApplicationContext(), "Only the person who bought can unbuy this item", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> extraInfo = new ArrayList<>();
                        extraInfo.add(shoppingId);
                        extraInfo.add(model.getId());
                        extraInfo.add(userEmail);
                        DialogFragment dialogFragment = DeleteItemDialogFragment.newInstance(extraInfo);
                        dialogFragment.show(getFragmentManager(), DeleteItemDialogFragment.class.getSimpleName());
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(shoppingOwner.equals(userEmail)) {
            getMenuInflater().inflate(R.menu.menu_list_details, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_list_name:
                ArrayList<String> shoppingListInfo = new ArrayList<>();
                shoppingListInfo.add(shoppingId);
                shoppingListInfo.add(shoppingName);
                shoppingListInfo.add(shoppingOwner);
                DialogFragment changeListNameDialogFragment = ChangeListNameDialogFragment.newInstance(shoppingListInfo);
                changeListNameDialogFragment.show(getFragmentManager(), ChangeListNameDialogFragment.class.getSimpleName());
                return true;
            case R.id.action_delete_list:
                DialogFragment deleteListDialogFragment = DeleteListDialogFragment.newInstance(shoppingId, false);
                deleteListDialogFragment.show(getFragmentManager(), DeleteListDialogFragment.class.getSimpleName());
                return true;
            case R.id.action_share_list:
                startActivity(ShareListActivity.newIntent(this, shoppingId));
                return true;
        }
        return true;
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListResponse response) {
        currentShoppingList = response.shoppingList;
        getSupportActionBar().setTitle(currentShoppingList.getListName());
    }

    @OnClick(R.id.activity_list_details_FAB)
    public void setFloatingActionButton() {
        DialogFragment dialogFragment = AddItemDialogFragment.newInstance(shoppingId);
        dialogFragment.show(getFragmentManager(), AddItemDialogFragment.class.getSimpleName());
    }
}
