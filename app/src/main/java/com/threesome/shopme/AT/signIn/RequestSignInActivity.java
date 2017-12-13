package com.threesome.shopme.AT.signIn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.R;

public class RequestSignInActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSignInStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sign_in);
        addControls ();
        addEvents ();
    }

    private void addEvents() {
        imgSignInStore.setOnClickListener(this);
    }

    private void addControls() {
        imgSignInStore= findViewById(R.id.imgSignInStore);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSignInStore){
            startActivity(new Intent(RequestSignInActivity.this, SignInStoreActivity.class));
        }
    }
}
