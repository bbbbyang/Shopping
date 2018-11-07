package com.example.bing.shopping.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.infrastructure.Utils;
import com.squareup.otto.Bus;

public class BaseDialog extends DialogFragment {
    protected ShoppingApplication application;
    protected Bus bus;
    protected String userEmail;
    protected String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (ShoppingApplication) getActivity().getApplication();
        bus = application.getBus();
        bus.register(this);
        userEmail = Utils.decodeEmail(getActivity().getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE).getString(Utils.USER_EMAIL, ""));
        userName = getActivity().getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE).getString(Utils.USER_NAME, "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }
}
