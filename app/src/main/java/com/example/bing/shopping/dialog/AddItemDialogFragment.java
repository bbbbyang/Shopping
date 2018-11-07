package com.example.bing.shopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.bing.shopping.R;
import com.example.bing.shopping.services.ShoppingItemService;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddItemDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String SHOPPING_LIST_ID = "SHOPPING_LIST_ID";

    @BindView(R.id.dialog_add_item_editText)
    EditText newItemName;

    private String shoppingId;

    public static AddItemDialogFragment newInstance(String shoppingListId) {
        Bundle arguments = new Bundle();
        arguments.putString(SHOPPING_LIST_ID, shoppingListId);
        AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
        addItemDialogFragment.setArguments(arguments);
        return addItemDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingId = getArguments().getString(SHOPPING_LIST_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        ButterKnife.bind(this, rootView);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;
    }

    @Override
    public void onClick(View view) {
        bus.post(new ShoppingItemService.AddShoppingItemRequest(newItemName.getText().toString(), shoppingId, userEmail));
    }

    @Subscribe
    public void addShoppingItem(ShoppingItemService.AddShoppingItemResponse response) {
        if(!response.didSucceed()) {
            newItemName.setError(response.getPropertyError("itemName"));
        } else {
            dismiss();
        }
    }
}
