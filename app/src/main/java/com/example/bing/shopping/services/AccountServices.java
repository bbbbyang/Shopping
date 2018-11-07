package com.example.bing.shopping.services;

import android.app.ProgressDialog;
import android.content.SharedPreferences;

import com.example.bing.shopping.infrastructure.ServiceResponse;
import com.facebook.AccessToken;
import com.google.firebase.database.ServerValue;

public class AccountServices {
    private AccountServices() {
    }

    public static class RegisterUserRequest {
        public String userName;
        public String userEmail;
        public ProgressDialog progressDialog;

        public RegisterUserRequest(String userName, String userEmail, ProgressDialog progressDialog) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.progressDialog = progressDialog;
        }
    }

    public static class RegisterUserResponse extends ServiceResponse {

    }

    public static class LogUserInRequest {
        public String userPassword;
        public String userEmail;
        public ProgressDialog progressDialog;
        public SharedPreferences sharedPreferences;

        public LogUserInRequest(String userEmail, String userPassword, ProgressDialog progressDialog, SharedPreferences sharedPreferences) {
            this.userPassword = userPassword;
            this.userEmail = userEmail;
            this.progressDialog = progressDialog;
            this.sharedPreferences = sharedPreferences;
        }
    }

    public static class LogUserInResponse extends ServiceResponse {

    }

    public static class LogUserInFacebookRequest {
        public AccessToken accessToken;
        public ProgressDialog progressDialog;
        public String userName;
        public String userEmail;
        public SharedPreferences sharedPreferences;

        public LogUserInFacebookRequest(AccessToken accessToken, ProgressDialog progressDialog, String userName, String userEmail, SharedPreferences sharedPreferences) {
            this.accessToken = accessToken;
            this.progressDialog = progressDialog;
            this.userName = userName;
            this.userEmail = userEmail;
            this.sharedPreferences = sharedPreferences;
        }
    }
}
