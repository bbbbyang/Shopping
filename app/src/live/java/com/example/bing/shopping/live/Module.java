package com.example.bing.shopping.live;

import com.example.bing.shopping.infrastructure.ShoppingApplication;

public class Module {

    public static void Register(ShoppingApplication application) {
        new LiveAccountServices(application);
        new LiveShoppingListService(application);
        new LiveShoppingItemService(application);
        new LiveUserFriendsService(application);
    }
}
