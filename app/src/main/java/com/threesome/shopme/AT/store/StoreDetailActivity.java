package com.threesome.shopme.AT.store;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.threesome.shopme.AT.singleton.FirebaseDB;
import com.threesome.shopme.AT.store.homeStoreDetail.HomeStoreDetailFragment;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.LA.CategoryFragment;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSlidemenu, imgBanerStoreSlider, imgSignOut;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private String idStore;
    private TextView txtEditProfile;
    private int indexEdit = 0;
    private ProfileStoreFragment fragmentProfileStore;
    private HomeStoreDetailFragment homeStoreDetailFragment;
    private LinearLayout btnCreateProduct, layoutMyProfileStore, layoutHomeDetailStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        addControls ();
        addEvents ();
        setUpBanerStore ();
    }

    private void setUpBanerStore() {
        if (idStore != null){
            Store store = FirebaseDB.getInstance().getStoreDetail(idStore);
            if (store != null && store.getLinkPhotoStore() != null){
                GlideApp.with(this)
 //                     .placeholder()
 //                     .override(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH, (int)(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH/ com.threesome.shopme.LA.Constant.GOLDEN_RATIO))
                        .load(store.getLinkPhotoStore())
                        .into(imgBanerStoreSlider);
            }
        }
    }

    private void addEvents() {
        imgSlidemenu.setOnClickListener(this);
        btnCreateProduct.setOnClickListener(this);
        layoutMyProfileStore.setOnClickListener(this);
        layoutHomeDetailStore.setOnClickListener(this);
        imgSignOut.setOnClickListener(this);

    }

    private void addControls() {
        imgSlidemenu = (ImageView) findViewById(R.id.imgSlideMenuStore);
        imgBanerStoreSlider = findViewById(R.id.imgBanerStoreSlide);
        imgSignOut = findViewById(R.id.imgSignOut);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_Store);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);
        layoutMyProfileStore = findViewById(R.id.layoutMyProfileStore);
        layoutHomeDetailStore= findViewById(R.id.layoutHomeDetailStore);
        txtEditProfile = findViewById(R.id.txtEditProfile);
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
        }if (id == R.id.txtEditProfile){
            editProfile ();
        }if (id == R.id.btnCreateProduct){
            createProduct ();
        }if (id == R.id.layoutMyProfileStore){
            myProfileFragment ();
        }if (id == R.id.imgSignOut){
            signOut ();
        }if (id == R.id.layoutHomeDetailStore){
            homeDetailStore ();
        }

        //showTextEditProfile
        if (indexEdit == 0){
            txtEditProfile.setVisibility(View.GONE);
            txtEditProfile.setOnClickListener(null);
        }else if (indexEdit == 1){
            txtEditProfile.setVisibility(View.VISIBLE);
            txtEditProfile.setText("Edit");
            txtEditProfile.setOnClickListener(this);
        }else if (indexEdit == 2){
            txtEditProfile.setVisibility(View.VISIBLE);
            txtEditProfile.setText("Save");
            txtEditProfile.setOnClickListener(this);
        }
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you want to logout?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
              /*  if (mGoogleApiClient.isConnected())
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);*/
                Toast.makeText(StoreDetailActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void editProfile() {
        if (indexEdit == 1){
            indexEdit = 2;
            txtEditProfile.setText("Save");
            fragmentProfileStore.showIcon(true);
        }else {
            indexEdit = 1;
            txtEditProfile.setText("Edit");
            fragmentProfileStore.showIcon(false);

        }
    }

    private void myProfileFragment() {
        drawerLayout.closeDrawers();
        indexEdit = 1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentProfileStore == null) {
            fragmentProfileStore = new ProfileStoreFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentProfileStore.setArguments(bundle);
        fragmentTransaction.replace(R.id.category_container, fragmentProfileStore);
        fragmentTransaction.commit();
    }

    private void createProduct() {
        drawerLayout.closeDrawers();
        indexEdit = 0;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CategoryFragment fragmentCategory = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentCategory.setArguments(bundle);
        fragmentTransaction.add(R.id.category_container, fragmentCategory);
        fragmentTransaction.commit();
    }

    private void homeDetailStore() {
        drawerLayout.closeDrawers();
        indexEdit = 0;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (homeStoreDetailFragment == null) {
            homeStoreDetailFragment = new HomeStoreDetailFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        homeStoreDetailFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.category_container, homeStoreDetailFragment);
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
            Toast.makeText(this, "Sign Outed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        }else {
            idStore = currentUser.getUid();
        }
    }
}
