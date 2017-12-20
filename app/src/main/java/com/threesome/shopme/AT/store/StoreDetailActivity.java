package com.threesome.shopme.AT.store;

import android.content.Intent;
import android.media.Image;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.R;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSlidemenu;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private String idStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        addControls ();
        addEvents ();
    }

    private void addEvents() {
        imgSlidemenu.setOnClickListener(this);
    }

    private void addControls() {
        imgSlidemenu = (ImageView) findViewById(R.id.imgSlideMenuStore);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_Store);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSlideMenuStore){
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Toast.makeText(this, "Sign Outed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        }else {
            idStore = currentUser.getUid();
        }
    }
}
