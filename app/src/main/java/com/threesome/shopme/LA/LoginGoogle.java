package com.threesome.shopme.LA;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.MapsActivity;
import com.threesome.shopme.R;
import com.threesome.shopme.models.User;


/**
 * Created by Lan Anh on 14/12/2017.
 */

public class LoginGoogle {

    private FirebaseAuth mAuth;
    private Activity mActivity;
    private DatabaseReference mData;
    private ProgressDialog progressDialog;
    private String email = "", userName = "", avatar = "", idUser;
    //private int REQUEST_LOGIN_DISCUSSION = 843;

    public LoginGoogle(Activity mActivity) {
        this.mActivity = mActivity;
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(mActivity);
        mData = FirebaseDatabase.getInstance().getReference();
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgress(mActivity.getResources().getString(R.string.loading));
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgress();
                    Intent intent = new Intent(mActivity, MapsActivity.class);
                    mActivity.startActivity(intent);
                    //Add new User to Database realtime
                    email = task.getResult().getUser().getEmail();
                    userName = task.getResult().getUser().getDisplayName();
                    idUser = task.getResult().getUser().getUid();
                    if (task.getResult().getUser().getPhotoUrl() != null) {
                        avatar = task.getResult().getUser().getPhotoUrl().toString();
                    }
                    User user = new User(idUser, email, userName, avatar);
                    createUserOnFireBase(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
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

                }
                else {
                    hideProgress();
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
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
