package com.example.bing.shopping.view.AddFriendView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.list_friend_list_listName)
    TextView friendEmail;

    @BindView(R.id.list_friend_list_listView)
    public ImageView listView;

    public AddFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(User user) {
        itemView.setTag(user);
        friendEmail.setText(user.getEmail());

    }
}
