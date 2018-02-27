package com.threesome.shopme.AT.signIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.LA.Constant;
import com.threesome.shopme.LA.LoginFacebook;
import com.threesome.shopme.MapsActivity;
import com.threesome.shopme.R;
import com.threesome.shopme.models.User;

public class RequestSignInActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {


    private ImageView imgSignInStore;
    private TextView txtCreateStore;
    private static final String TAG = RequestSignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9003;
    private ImageView mBtnSignInGg;
    private ImageView mBtnSignInFb;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private LoginManager loginManager;
    private LoginFacebook loginFacebook;
    private DatabaseReference mData;
    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private String email = "", userName = "", avatar = "", idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_request_sign_in);
        addControls ();
        addEvents ();
    }

    private void addEvents() {
        imgSignInStore.setOnClickListener(this);
        mBtnSignInFb.setOnClickListener(this);
        mBtnSignInGg.setOnClickListener(this);
        txtCreateStore.setOnClickListener(this);
    }

    private void addControls() {
        txtCreateStore = findViewById(R.id.txtCreateNewStore);
        imgSignInStore= (ImageView) findViewById(R.id.imgSignInStore);
        mBtnSignInGg = (ImageView) findViewById(R.id.btn_signInGg);
        mBtnSignInFb = (ImageView) findViewById(R.id.btn_signInFb);
        progressDialog = new ProgressDialog(this);
        mData = FirebaseDatabase.getInstance().getReference();
        //login Facebook
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginFacebook = new LoginFacebook(loginManager, callbackManager, this);

        //login Google
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //initialize auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSignInStore){
            startActivity(new Intent(RequestSignInActivity.this, SignInStoreActivity.class));
        }
        if (id == R.id.txtCreateNewStore){
            startActivity(new Intent(RequestSignInActivity.this, RegisterStoreActivity.class));
        }
        if(id == R.id.btn_signInGg){
            loginGoogle();
        }
        if(id == R.id.btn_signInFb){
            loginFacebook();
        }
    }
    private void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginFacebook(){
        loginFacebook.loginFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgress();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            hideProgress();
                            Intent intent = new Intent(RequestSignInActivity.this, MapsActivity.class);
                            startActivity(intent);
                            //Add new User to Database realtime
                            email = task.getResult().getUser().getEmail();
                            userName = task.getResult().getUser().getDisplayName();
                            idUser = task.getResult().getUser().getUid();
                            if (task.getResult().getUser().getPhotoUrl() != null) {
                                avatar = task.getResult().getUser().getPhotoUrl().toString();
                            }
                            User user = new User(idUser, email, userName, avatar);
                            createUserOnFireBase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RequestSignInActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // [END auth_with_google]
    private void createUserOnFireBase (final User user){
        final DatabaseReference userNode = mData.child(Constant.USER).child(user.getId());
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    userNode.setValue(user);
                    Toast.makeText(RequestSignInActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                }
                else {
                    hideProgress();
                    Toast.makeText(RequestSignInActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void showProgress (){
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }
}
