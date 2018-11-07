package com.example.bing.shopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.bing.shopping.R;
import com.example.bing.shopping.entities.SharedWith;
import com.example.bing.shopping.entities.User;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.GetUserFriendsService;
import com.example.bing.shopping.services.ShoppingListService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class DeleteListDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String EXTRA_SHOPPING_LIST_ID = "EXTRA_SHOPPING_LIST_ID";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private String shoppingListId;
    private boolean isLongClicked;
    private SharedWith sharedWith;

    public static DeleteListDialogFragment newInstance(String shopplingListId, boolean isLongClicked) {
        Bundle argument = new Bundle();
        argument.putString(EXTRA_SHOPPING_LIST_ID, shopplingListId);
        argument.putBoolean(EXTRA_BOOLEAN, isLongClicked);

        DeleteListDialogFragment dialogFragment = new DeleteListDialogFragment();
        dialogFragment.setArguments(argument);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListId = getArguments().getString(EXTRA_SHOPPING_LIST_ID);
        isLongClicked = getArguments().getBoolean(EXTRA_BOOLEAN);
        bus.post(new GetUserFriendsService.GetUserSharedFriendsListRequest(
                FirebaseDatabase.getInstance().getReferenceFromUrl(Utils.FIRE_BASE_USER_SHARED_WITH_REFERENCE + shoppingListId)));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_list, null))
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Delete Shopping List")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        deleteAllShoppingLists(shoppingListId, userEmail);
        if(!isLongClicked) {
            getActivity().finish();
        }
    }

    @Subscribe
    public void getUsersSharedWith(GetUserFriendsService.GetUserSharedFriendsListResponse response){

        if (response.sharedWith!=null){
            sharedWith = response.sharedWith;
        } else{
            sharedWith = new SharedWith();
        }
    }

    public void deleteAllShoppingLists(String shoppingListId, String ownerEmail) {
        if(sharedWith.getSharedWith() != null && !sharedWith.getSharedWith().isEmpty()) {
            for(User user : sharedWith.getSharedWith().values()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                        Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(user.getEmail()) + "/" + shoppingListId);
                Map newListData = new HashMap();
                newListData.put("listName","CListIsAboutToGetDeleted");
                reference.updateChildren(newListData);
                reference.removeValue();
            }
        }
        bus.post(new ShoppingListService.DeleteShoppingListRequest(userEmail, shoppingListId));
    }
}
