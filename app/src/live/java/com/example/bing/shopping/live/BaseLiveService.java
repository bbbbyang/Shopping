package com.example.bing.shopping.live;

import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Bus;

public class BaseLiveService {
    protected Bus bus;
    protected ShoppingApplication application;
    protected FirebaseAuth auth;

    public BaseLiveService(ShoppingApplication application) {
        this.application = application;
        bus = application.getBus();
        bus.register(this);
        auth = FirebaseAuth.getInstance();

    }
}
