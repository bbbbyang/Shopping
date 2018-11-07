package com.example.bing.shopping.view.ShoppingItemView;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_shopping_item_itemName)
    TextView itemName;

    @BindView(R.id.list_shopping_item_boughtBy)
    TextView boughtBy;

    @BindView(R.id.list_shopping_item_itemView)
    public ImageView imageView;

    @BindView(R.id.list_shopping_item_layout)
    public View layout;

    public ShoppingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Item item, Context context, String currentUserEmail) {
        itemView.setTag(item);

        if(!item.isBought()) {
            boughtBy.setVisibility(View.GONE);
            itemName.setPaintFlags(itemName.getPaintFlags()& (~Paint.STRIKE_THRU_TEXT_FLAG));
            imageView.setImageResource(R.mipmap.ic_trash);
            imageView.setEnabled(true);
        } else {
            boughtBy.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.mipmap.ic_done);
            imageView.setEnabled(false);
            itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            boughtBy.setText(context.getString(R.string.bought_by,
                    currentUserEmail.equals(item.getBoughtBy()) ? "You" : currentUserEmail));

        }

        itemName.setText(item.getItemName());
    }
}
