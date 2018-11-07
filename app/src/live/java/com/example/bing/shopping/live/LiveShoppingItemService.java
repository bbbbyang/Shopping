package com.example.bing.shopping.live;

import com.example.bing.shopping.entities.Item;
import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.ShoppingItemService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LiveShoppingItemService extends BaseLiveService {

    public LiveShoppingItemService(ShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void addShoppingItem(ShoppingItemService.AddShoppingItemRequest request) {
        ShoppingItemService.AddShoppingItemResponse response = new ShoppingItemService.AddShoppingItemResponse();

        if(request.shoppingItemName.isEmpty()) {
            response.setPropertyErrors("itemName", "Item must have a name.");
        }
        if(response.didSucceed()) {
            DatabaseReference itemReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId).push();
            DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.userEmail) + "/" + request.shoppingListId);

            Item item = new Item(itemReference.getKey(), request.shoppingItemName, request.userEmail, "", false);

            itemReference.child("id").setValue(item.getId());
            itemReference.child("itemName").setValue(item.getItemName());
            itemReference.child("ownerEmail").setValue(item.getOwnerEmail());
            itemReference.child("boughtBy").setValue(item.getBoughtBy());
            itemReference.child("bought").setValue(item.isBought());

            HashMap<String, Object> timeStampedLastChanged = new HashMap<>();
            timeStampedLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("dateLastChanged", timeStampedLastChanged);
            listReference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void changeShoppingItemName(ShoppingItemService.ChangeShoppingItemNameRequest request) {
        ShoppingItemService.ChangeShoppingItemNameResponse response = new ShoppingItemService.ChangeShoppingItemNameResponse();

        if(request.itemName.isEmpty()) {
            response.setPropertyErrors("itemName", "Item must have a name");
        }
        if(response.didSucceed()) {
            DatabaseReference itemReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/" + request.shoppingItemId);
            DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.userEmail) + "/" + request.shoppingListId);

            Map newItemData = new HashMap();
            newItemData.put("itemName", request.itemName);
            itemReference.updateChildren(newItemData);

            HashMap<String, Object> timeStampedLastChanged = new HashMap<>();
            timeStampedLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("dateLastChanged", timeStampedLastChanged);
            listReference.updateChildren(newListData);

        }
        bus.post(response);
    }

    @Subscribe
    public void changeShoppingItemStatus(ShoppingItemService.ChangeShoppingItemStatusRequest request) {

        DatabaseReference itemReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/" + request.item.getId());

        Map newItemData = new HashMap();
        if(!request.item.isBought()) {
            newItemData.put("bought", true);
            newItemData.put("boughtBy", request.currentUserEmail);
        } else if(request.currentUserEmail.equals(request.item.getBoughtBy())) {
            newItemData.put("bought", false);
            newItemData.put("boughtBy", "");
            itemReference.updateChildren(newItemData);
        }
        itemReference.updateChildren(newItemData);
    }

    @Subscribe
    public void deleteShoppingItem(ShoppingItemService.DeleteShoppingItemRequest request) {
        DatabaseReference itemReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/" + request.shoppingItemId);
        DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + Utils.encodeEmail(request.userEmail) + "/" + request.shoppingListId);

        itemReference.removeValue();
        HashMap<String, Object> timeStampedLastChanged = new HashMap<>();
        timeStampedLastChanged.put("date", ServerValue.TIMESTAMP);
        Map newListData = new HashMap();
        newListData.put("dateLastChanged", timeStampedLastChanged);
        listReference.updateChildren(newListData);
    }

}
