package com.threesome.shopme.AT.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.LA.CategoryFragment;
import com.threesome.shopme.LA.StoreDetailFragment;
import com.threesome.shopme.R;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSlidemenu;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private String idStore;
    private LinearLayout btnCreateProduct;
    private LinearLayout btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        addControls ();
        addEvents ();

    }

    private void addEvents() {
        imgSlidemenu.setOnClickListener(this);
        btnCreateProduct.setOnClickListener(this);
    }

    private void addControls() {
        imgSlidemenu = (ImageView) findViewById(R.id.imgSlideMenuStore);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_Store);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);
        btnHome = findViewById(R.id.btnHome);
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
        if (id == R.id.btnCreateProduct){
            createProduct ();
        }
        if(id == R.id.btnHome){
            goToHome();
        }
    }

    private void createProduct() {
        drawerLayout.closeDrawers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CategoryFragment fragmentCategory = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentCategory.setArguments(bundle);
        fragmentTransaction.add(R.id.category_container, fragmentCategory);
        fragmentTransaction.commit();
    }
    private void goToHome(){
        drawerLayout.closeDrawers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StoreDetailFragment storeDetailFragment = new StoreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        storeDetailFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.category_container, storeDetailFragment);
        fragmentTransaction.commit();
    }
    private void addHomeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StoreDetailFragment storeDetailFragment = new StoreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        storeDetailFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.category_container, storeDetailFragment);
        fragmentTransaction.commit();
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
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        }else {
            idStore = currentUser.getUid();
            addHomeFragment();
        }
    }
}
