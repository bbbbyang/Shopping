package com.example.bing.shopping.services;

import com.example.bing.shopping.entities.Item;
import com.example.bing.shopping.infrastructure.ServiceResponse;

public class ShoppingItemService {

    public ShoppingItemService() {
    }


    public static class AddShoppingItemRequest {
        public String shoppingItemName;
        public String shoppingListId;
        public String userEmail;

        public AddShoppingItemRequest(String shoppingItemName, String shoppingListId, String userEmail) {
            this.shoppingItemName = shoppingItemName;
            this.shoppingListId = shoppingListId;
            this.userEmail = userEmail;
        }
    }

    public static class AddShoppingItemResponse extends ServiceResponse {

    }

    public static class ChangeShoppingItemNameRequest {
        public String shoppingListId;
        public String shoppingItemId;
        public String itemName;
        public String userEmail;

        public ChangeShoppingItemNameRequest(String shoppingListId, String shoppingItemId, String itemName, String userEmail) {
            this.shoppingListId = shoppingListId;
            this.shoppingItemId = shoppingItemId;
            this.itemName = itemName;
            this.userEmail = userEmail;
        }
    }

    public static class ChangeShoppingItemNameResponse extends ServiceResponse {

    }

    public static class ChangeShoppingItemStatusRequest {
        public Item item;
        public String currentUserEmail;
        public String shoppingListId;

        public ChangeShoppingItemStatusRequest(Item item, String currentUserEmail, String shoppingListId) {
            this.item = item;
            this.currentUserEmail = currentUserEmail;
            this.shoppingListId = shoppingListId;
        }
    }

    public static class DeleteShoppingItemRequest {
        public String shoppingListId;
        public String shoppingItemId;
        public String userEmail;

        public DeleteShoppingItemRequest(String shoppingListId, String shoppingItemId, String userEmail) {
            this.shoppingListId = shoppingListId;
            this.shoppingItemId = shoppingItemId;
            this.userEmail = userEmail;
        }
    }
}
