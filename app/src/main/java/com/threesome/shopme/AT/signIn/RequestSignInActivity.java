package com.threesome.shopme.AT.signIn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.LA.LoginFacebook;
import com.threesome.shopme.LA.LoginGoogle;
import com.threesome.shopme.R;

public class RequestSignInActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {


    private ImageView imgSignInStore;
    private TextView txtCreateStore;
    private static final String TAG = RequestSignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9003;
    private ImageView mBtnSignInGg;
    private ImageView mBtnSignInFb;
    private ImageView mBtnSignOut;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private LoginManager loginManager;
    private LoginFacebook loginFacebook;
    private LoginGoogle loginGoogle;
    private CallbackManager callbackManager;
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
    }

    private void addControls() {
        imgSignInStore= (ImageView) findViewById(R.id.imgSignInStore);
        mBtnSignInGg = (ImageView) findViewById(R.id.btn_signInGg);
        mBtnSignInFb = (ImageView) findViewById(R.id.btn_signInFb);

        //login Facebook
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginFacebook = new LoginFacebook(loginManager, callbackManager, this);

        //login Google
        loginGoogle = new LoginGoogle(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //initialize auth
        mAuth = FirebaseAuth.getInstance();
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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginFacebook(){
        loginFacebook.loginFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                loginGoogle.firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
