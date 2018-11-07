package com.example.bing.shopping.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bing.shopping.R;
import com.example.bing.shopping.dialog.AddListDialogFragment;
import com.example.bing.shopping.dialog.DeleteListDialogFragment;
import com.example.bing.shopping.entities.ShoppingList;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.view.ShoppingListView.ShoppingListViewHolder;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_FAB)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.activity_main_listRecyclerView)
    RecyclerView recyclerView;

    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String toolBarName;

        if(userName.contains(" ")) {
            toolBarName  = userName.substring(0, userName.indexOf(" ")) + "'s list";
        } else {
            toolBarName = userName + "'s list";
        }

        getSupportActionBar().setTitle(toolBarName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(userEmail));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String order = sharedPreferences.getString(Utils.LIST_ORDER_PREFERENCE, Utils.ORDER_BY_KEY);
        Query query;

        if(order.equals(Utils.ORDER_BY_KEY)) {
            query = reference.orderByKey();
        } else {
            query = reference.orderByChild(order);
        }

        adapter = new FirebaseRecyclerAdapter<ShoppingList, ShoppingListViewHolder>(ShoppingList.class,
                R.layout.shopping_list, ShoppingListViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ShoppingListViewHolder viewHolder, ShoppingList model, int position) {
                viewHolder.populate(model);
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> shoppingListInfo = new ArrayList<>();
                        shoppingListInfo.add(model.getId());
                        shoppingListInfo.add(model.getListName());
                        shoppingListInfo.add(model.getOwnerEmail());
                        startActivity(ListDetailsActivity.newInstance(getApplicationContext(), shoppingListInfo));
                    }
                });

                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(userEmail.equals(model.getOwnerEmail())) {
                            DialogFragment dialogFragment = DeleteListDialogFragment.newInstance(model.getId(), true);
                            dialogFragment.show(getFragmentManager(), DeleteListDialogFragment.class.getSimpleName());

                        } else {
                            Toast.makeText(getApplicationContext(), "Only the owner can delete the list", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                sharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.USER_EMAIL, null).apply();
                editor.putString(Utils.USER_NAME, null).apply();
                auth.signOut();
                finish();
                LoginManager.getInstance().logOut();
                return true;

            case R.id.action_sort:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_main_FAB)
    public void setFloatingActionButton() {
        DialogFragment dialogFragment = AddListDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(), AddListDialogFragment.class.getSimpleName());
    }
}
