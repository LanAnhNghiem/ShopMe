package com.threesome.shopme.LA;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.MapsActivity;
import com.threesome.shopme.R;
import com.threesome.shopme.models.User;

import java.util.Arrays;

/**
 * Created by KunPark on 7/18/2017.
 */

public class LoginFacebook {
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private ProgressDialog progressDialog;
    private String email = "", userName = "", avatar = "", idUser;


    public LoginFacebook(LoginManager loginManager, CallbackManager callbackManager, Activity mActivity) {
        this.loginManager = loginManager;
        this.callbackManager = callbackManager;
        this.mActivity = mActivity;
        progressDialog = new ProgressDialog(mActivity);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

    }

    public void loginFacebook (){
        String [] permission = {"public_profile", "email", "user_friends"};
        loginManager.logInWithReadPermissions(mActivity, Arrays.asList(permission));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                //Toast.makeText(mActivity, R.string.login_success, Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mActivity, CustomMapsActivity.class);
               // mActivity.startActivity(intent);
        }

            @Override
            public void onCancel() {
                Toast.makeText(mActivity, R.string.login_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mActivity, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        showProgress(mActivity.getResources().getString(R.string.loading));
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgress();
                    email = task.getResult().getUser().getEmail();
                    userName = task.getResult().getUser().getDisplayName();
                    avatar = task.getResult().getUser().getPhotoUrl().toString();
                    idUser = task.getResult().getUser().getUid();
                    User user = new User(idUser, email, userName, avatar);
                    createUserOnFireBase(user);
                }
                else {
                    hideProgress();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                Log.d("UNSUCCESSFUL", "SignInError");
                Toast.makeText(mActivity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createUserOnFireBase (final User user){
        final DatabaseReference userNode = mData.child(Constant.USER).child(user.getId());
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    userNode.setValue(user);
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    mActivity.startActivity(new Intent(mActivity, CustomMapsActivity.class));
                }
                else {
                    hideProgress();
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    mActivity.startActivity(new Intent(mActivity, CustomMapsActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showProgress (String s){
        progressDialog.setCancelable(false);
        progressDialog.setMessage(mActivity.getResources().getString(R.string.loading));
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }

}
