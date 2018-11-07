package com.example.bing.shopping.live;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.bing.shopping.activities.LoginActivity;
import com.example.bing.shopping.activities.MainActivity;
import com.example.bing.shopping.entities.User;
import com.example.bing.shopping.infrastructure.ShoppingApplication;
import com.example.bing.shopping.infrastructure.Utils;
import com.example.bing.shopping.services.AccountServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class LiveAccountServices extends BaseLiveService {

    public LiveAccountServices(ShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void registerUser(AccountServices.RegisterUserRequest request) {
        AccountServices.RegisterUserResponse response = new AccountServices.RegisterUserResponse();

        if(request.userEmail.isEmpty()) {
            response.setPropertyErrors("email", "Please put in your email.");
        }

        if(request.userName.isEmpty()) {
            response.setPropertyErrors("name", "Please put in your name.");
        }

        if(response.didSucceed()) {
            request.progressDialog.show();

            SecureRandom random = new SecureRandom();
            final String randomPassword = new BigInteger(32, random).toString();

            auth.createUserWithEmailAndPassword(request.userEmail, randomPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                request.progressDialog.dismiss();
                                Toast.makeText(application.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            else {
                                auth.sendPasswordResetEmail(request.userEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(!task.isSuccessful()) {
                                                    request.progressDialog.dismiss();
                                                    Toast.makeText(application.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                } else {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                                            Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));

                                                    HashMap<String, Object> timeJoined = new HashMap<>();
                                                    timeJoined.put("timeJoined", ServerValue.TIMESTAMP);

                                                    reference.child("email").setValue(request.userEmail);
                                                    reference.child("name").setValue(request.userName);
                                                    reference.child("hasLoggedInWithPassword").setValue(false);
                                                    reference.child("timeJoined").setValue(timeJoined);

                                                    Toast.makeText(application.getApplicationContext(), "Please Check Your Email", Toast.LENGTH_LONG).show();
                                                    request.progressDialog.dismiss();

                                                    Intent intent = new Intent(application.getApplicationContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    application.startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

        bus.post(response);
    }

    @Subscribe
    public void loginUser(AccountServices.LogUserInRequest request) {
        AccountServices.LogUserInResponse response = new AccountServices.LogUserInResponse();

        if(request.userEmail.isEmpty()) {
            response.setPropertyErrors("email", "Please put in your email.");
        }

        if(request.userPassword.isEmpty()) {
            response.setPropertyErrors("password","Please put in your password");
        }

        if(response.didSucceed()) {
            request.progressDialog.show();
            auth.signInWithEmailAndPassword(request.userEmail, request.userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                request.progressDialog.dismiss();
                                Toast.makeText(application.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            else {
                                DatabaseReference userLocation = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                        Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));

                                userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        if(user != null) {
                                            userLocation.child("hasLoggedInWithPassword").setValue(true);
                                            SharedPreferences sharedPreferences = request.sharedPreferences;
                                            sharedPreferences.edit().putString(Utils.USER_EMAIL, Utils.encodeEmail(user.getEmail())).apply();
                                            sharedPreferences.edit().putString(Utils.USER_NAME, user.getName()).apply();

                                            request.progressDialog.dismiss();
                                            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            application.startActivity(intent);
                                        }
                                        else {
                                            request.progressDialog.dismiss();
                                            Toast.makeText(application.getApplicationContext(), "Failed to connect to server", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        request.progressDialog.dismiss();
                                        Toast.makeText(application.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
        }
        bus.post(response);
    }

    @Subscribe
    public void facebookLogin(AccountServices.LogUserInFacebookRequest request) {
        request.progressDialog.show();

        AuthCredential authCredential = FacebookAuthProvider.getCredential(request.accessToken.getToken());

        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            request.progressDialog.dismiss();
                            Toast.makeText(application.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null) {
                                        HashMap<String, Object> timeJoined = new HashMap<>();
                                        timeJoined.put("timeJoined", ServerValue.TIMESTAMP);

                                        reference.child("email").setValue(request.userEmail);
                                        reference.child("name").setValue(request.userName);
                                        reference.child("hasLoggedInWithPassword").setValue(true);
                                        reference.child("timeJoined").setValue(timeJoined);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(application.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    request.progressDialog.dismiss();
                                }
                            });
                            SharedPreferences sharedPreferences = request.sharedPreferences;
                            sharedPreferences.edit().putString(Utils.USER_EMAIL, Utils.encodeEmail(request.userEmail)).apply();
                            sharedPreferences.edit().putString(Utils.USER_NAME, request.userName).apply();

                            request.progressDialog.dismiss();
                            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            application.startActivity(intent);
                        }
                    }
                });
    }
}
