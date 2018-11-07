package com.example.bing.shopping.infrastructure;

public class Utils {
    private static final String FIRE_BASE_BASE_URL = "https://shopping-50831.firebaseio.com/";
    public static final String FIRE_BASE_USER_REFERENCE = FIRE_BASE_BASE_URL + "users/";
    public static final String FIRE_BASE_USER_FRIEND_REFERENCE = FIRE_BASE_BASE_URL + "userFriends/";
    public static final String FIRE_BASE_USER_SHARED_WITH_REFERENCE = FIRE_BASE_BASE_URL + "sharedWith/";
    public static final String FIRE_BASE_SHOPPING_LIST_REFERENCE = FIRE_BASE_BASE_URL + "usersShoppingList/";
    public static final String FIRE_BASE_SHOPPING_ITEM_REFERENCE = FIRE_BASE_BASE_URL + "shoppingListItems/";

    public static final String MY_PREFERENCE = "MY_PREFERENCE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_NAME = "USER_NAME";

    public static final String LIST_ORDER_PREFERENCE = "LIST_ORDER_PREFERENCE";
    public static final String ORDER_BY_KEY = "orderByPushKey";

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

}
