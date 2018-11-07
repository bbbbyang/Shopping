package com.example.bing.shopping.infrastructure;

import android.app.Application;

import com.example.bing.shopping.live.Module;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Bus;

public class ShoppingApplication extends Application {

    private Bus bus;

    public ShoppingApplication() {
        this.bus = new Bus();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance();
        Module.Register(this);
    }

    public Bus getBus() {
        return bus;
    }
}
