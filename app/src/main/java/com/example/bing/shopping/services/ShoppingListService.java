package com.example.bing.shopping.services;

import com.example.bing.shopping.entities.ShoppingList;
import com.example.bing.shopping.infrastructure.ServiceResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ShoppingListService {

    public ShoppingListService() {
    }

    public static class AddShoppingListRequest {
        public String shoppingListName;
        public String ownerName;
        public String ownerEmail;

        public AddShoppingListRequest(String shoppingListName, String ownerName, String ownerEmail) {
            this.shoppingListName = shoppingListName;
            this.ownerName = ownerName;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class AddShoppingListResponse extends ServiceResponse {

    }

    public static class DeleteShoppingListRequest {
        public String ownerEmail;
        public String shoppingListId;

        public DeleteShoppingListRequest(String ownerEmail, String shoppingListId) {
            this.ownerEmail = ownerEmail;
            this.shoppingListId = shoppingListId;
        }
    }

    public static class ChangeListNameRequest {
        public String newShoppingListName;
        public String shoppingListId;
        public String shoppingListOwnerEmail;

        public ChangeListNameRequest(String newShoppingListName, String shoppingListId, String shoppingListOwnerEmail) {
            this.newShoppingListName = newShoppingListName;
            this.shoppingListId = shoppingListId;
            this.shoppingListOwnerEmail = shoppingListOwnerEmail;
        }
    }

    public static class ChangeListNameResponse extends ServiceResponse {

    }

    public static class GetCurrentShoppingListRequest {
        public String shoppingListId;
        public String ownerEmail;

        public GetCurrentShoppingListRequest(String shoppingListId, String ownerEmail) {
            this.shoppingListId = shoppingListId;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class GetCurrentShoppingListResponse {
        public ShoppingList shoppingList;
    }

    public static class UpdateShoppingListTimeStampRequest {
        public DatabaseReference reference;

        public UpdateShoppingListTimeStampRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }
}
