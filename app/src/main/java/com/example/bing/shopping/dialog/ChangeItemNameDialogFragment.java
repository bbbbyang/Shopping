package com.example.bing.shopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.bing.shopping.R;
import com.example.bing.shopping.services.ShoppingItemService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeItemNameDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String SHOPPING_ITEM_EXTRA_INFO = "SHOPPING_ITEM_EXTRA_INFO";

    private String itemName;
    private String shoppingListId;
    private String shoppingItemId;

    @BindView(R.id.dialog_change_item_name_editText)
    EditText newItemName;

    public static ChangeItemNameDialogFragment newInstance(ArrayList<String> shoppingItem) {
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SHOPPING_ITEM_EXTRA_INFO, shoppingItem);
        ChangeItemNameDialogFragment changeItemNameDialogFragment = new ChangeItemNameDialogFragment();
        changeItemNameDialogFragment.setArguments(arguments);
        return changeItemNameDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListId = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(0);
        shoppingItemId = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(1);
        itemName = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO ).get(2);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_item_name, null);
        ButterKnife.bind(this, rootView);

        newItemName.setText(itemName);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Change Name", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Change Item Name")
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View view) {
        bus.post(new ShoppingItemService.ChangeShoppingItemNameRequest(shoppingListId, shoppingItemId, newItemName.getText().toString(), userEmail));
    }

    @Subscribe
    public void changeListName(ShoppingItemService.ChangeShoppingItemNameResponse response) {
        if(!response.didSucceed()) {
            newItemName.setError(response.getPropertyError("itemName"));
        } else {
            dismiss();
        }
    }
}
