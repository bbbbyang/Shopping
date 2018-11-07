package com.example.bing.shopping.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.infrastructure.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Bus;

public class BaseActivity extends AppCompatActivity {
    protected ShoppingApplication application;
    protected Bus bus;
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authStateListener;
    protected String userEmail;
    protected String userName;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (ShoppingApplication) getApplication();
        bus = application.getBus();
        bus.register(this);

        sharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Utils.USER_NAME,"");
        userEmail = Utils.decodeEmail(sharedPreferences.getString(Utils.USER_EMAIL, ""));

        auth = FirebaseAuth.getInstance();

        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))) {
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user == null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Utils.USER_EMAIL, null).apply();
                        editor.putString(Utils.USER_NAME, null).apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        if(userEmail.equals("")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Utils.USER_EMAIL, null).apply();
                            editor.putString(Utils.USER_NAME, null).apply();
                            auth.signOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            };
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))) {
            auth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
