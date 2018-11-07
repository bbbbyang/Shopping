package com.example.bing.shopping.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bing.shopping.R;
import com.example.bing.shopping.services.AccountServices;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.activity_register_linear_layout)
    LinearLayout linearLayout;

    @BindView(R.id.activity_register_loginButton)
    Button loginButton;

    @BindView(R.id.activity_register_userEmail)
    EditText userEmail;

    @BindView(R.id.activity_register_userName)
    EditText userName;

    @BindView(R.id.activity_register_registerButton)
    Button registerButton;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        linearLayout.setBackgroundResource(R.drawable.background_screen_two);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading....");
        progressDialog.setMessage("Attempting to Register Account");
        progressDialog.setCancelable(false);
    }

    @OnClick(R.id.activity_register_loginButton)
    public void setLoginButton() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.activity_register_registerButton)
    public void setRegisterButton() {
        bus.post(new AccountServices.RegisterUserRequest(
                userName.getText().toString(), userEmail.getText().toString(), progressDialog));
    }

    @Subscribe
    public void registerUser(AccountServices.RegisterUserResponse response) {
        if(!response.didSucceed()) {
            userEmail.setError(response.getPropertyError("email"));
            userName.setError(response.getPropertyError("name"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
