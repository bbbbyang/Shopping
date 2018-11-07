package com.example.bing.shopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bing.shopping.R;
import com.example.bing.shopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeListNameDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String SHOPPING_LIST_EXTRA_INFO = "SHOPPING_LIST_EXTRA_INFO";

    private String shoppingListId;
    @BindView(R.id.dialog_change_list_name_editText)
    EditText newListName;

    public static ChangeListNameDialogFragment newInstance(ArrayList<String> shoppingListInfo) {
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SHOPPING_LIST_EXTRA_INFO, shoppingListInfo);
        ChangeListNameDialogFragment dialogFragment = new ChangeListNameDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListId = getArguments().getStringArrayList(SHOPPING_LIST_EXTRA_INFO).get(0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_list_name, null);
        ButterKnife.bind(this, rootView);

        newListName.setText(getArguments().getStringArrayList(SHOPPING_LIST_EXTRA_INFO).get(1));
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Change Name", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Change Shopping List Name")
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;
    }

    @Override
    public void onClick(View view) {
        bus.post(new ShoppingListService.ChangeListNameRequest(newListName.getText().toString(),
                shoppingListId, userEmail));
    }

    @Subscribe
    public void changeListName(ShoppingListService.ChangeListNameResponse response) {
        if(!response.didSucceed()) {
            newListName.setError(response.getPropertyError("listName"));
        } else {
            dismiss();
        }
    }
}
