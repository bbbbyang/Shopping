package com.example.bing.shopping.live;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.bing.shopping.entities.ShoppingList;
import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.ShoppingListService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LiveShoppingListService extends BaseLiveService {

    public LiveShoppingListService(ShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void addShoppingList(ShoppingListService.AddShoppingListRequest request) {
        ShoppingListService.AddShoppingListResponse response = new ShoppingListService.AddShoppingListResponse();

        if(request.shoppingListName.isEmpty()) {
            response.setPropertyErrors("listName", "Shopping List must have a name");
        }
        if(response.didSucceed()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.ownerEmail)).push();
            HashMap<String, Object> timeStampedCreated = new HashMap<>();
            timeStampedCreated.put("timeStamp", ServerValue.TIMESTAMP);
            ShoppingList shoppingList = new ShoppingList(reference.getKey(), request.shoppingListName,
                    Utils.decodeEmail(request.ownerEmail), request.ownerName, timeStampedCreated);

            reference.setValue(shoppingList);
        }

        bus.post(response);
    }

    @Subscribe
    public void deleteShoppingList(ShoppingListService.DeleteShoppingListRequest request) {
        DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.ownerEmail) + "/" + request.shoppingListId);
        DatabaseReference itemReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId);
        DatabaseReference sharedWithReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_USER_SHARED_WITH_REFERENCE + request.shoppingListId);
        sharedWithReference.removeValue();
        listReference.removeValue();
        itemReference.removeValue();
    }

    @Subscribe
    public void changeListName(ShoppingListService.ChangeListNameRequest request) {
        ShoppingListService.ChangeListNameResponse response = new ShoppingListService.ChangeListNameResponse();
        if(request.newShoppingListName.isEmpty()) {
            response.setPropertyErrors("listName", "Shopping list must have a name");
        }

        if(response.didSucceed()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.shoppingListOwnerEmail)
                    + "/" + request.shoppingListId);

            HashMap<String, Object> timeStampedLastChanged = new HashMap<>();
            timeStampedLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("listName", request.newShoppingListName);
            newListData.put("dateLastChanged", timeStampedLastChanged);
            reference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListRequest request) {
        final ShoppingListService.GetCurrentShoppingListResponse response = new ShoppingListService.GetCurrentShoppingListResponse();

        FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.ownerEmail) + "/" + request.shoppingListId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                response.shoppingList = dataSnapshot.getValue(ShoppingList.class);
                if(response.shoppingList != null) {
                    bus.post(response);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void UpdateShoppingListTimeStamp(ShoppingListService.UpdateShoppingListTimeStampRequest request){
        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date",ServerValue.TIMESTAMP);
        Map newListData = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        request.reference.updateChildren(newListData);
    }
}
