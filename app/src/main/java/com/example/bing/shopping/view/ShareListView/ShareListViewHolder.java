package com.example.bing.shopping.view.ShareListView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_share_listName)
    TextView sharedUserName;

    @BindView(R.id.list_share_listView)
    public ImageView listView;

    public ShareListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(User user) {
        sharedUserName.setText(user.getName());
    }
}
