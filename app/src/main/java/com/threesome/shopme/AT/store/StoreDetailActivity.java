package com.threesome.shopme.AT.store;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.threesome.shopme.AT.singleton.FirebaseDB;
import com.threesome.shopme.AT.store.homeStoreDetail.HomeStoreDetailFragment;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.LA.CategoryFragment;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.LA.SearchFragment;
import com.threesome.shopme.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSlidemenu, imgBanerStoreSlider, imgSignOut, imgChangeBaner;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private Bitmap bitmap = null;
    private String idStore, linkCoverStore;
    private TextView txtEditProfile, txtStoreName, txtEmailStore;
    private int indexEdit = 0;
    private ProfileStoreFragment fragmentProfileStore;
    private HistoryOrderStoreFragment fragmentHistoryStore;
    private ProgressDialog progressDialog;
    private HomeStoreDetailFragment homeStoreDetailFragment;
    private StorageReference mStorage;
    private LinearLayout btnCreateProduct, layoutMyProfileStore, layoutHomeDetailStore;
    private EditText edtSearch;
    private int currentFragment = 1;
    private static final int HOME = 1, CREATE_PRODUCT = 2, HISTORY = 3, NOTIFICATION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        addControls();
        addEvents();
        homeDetailStore();
        setUpBanerStore();
    }

    private void setUpBanerStore() {
        if (idStore != null){
            mData.child(Constant.STORE).child(idStore).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        Store store = dataSnapshot.getValue(Store.class);
                        if (store != null && store.getLinkPhotoStore() != null) {
                            GlideApp.with(StoreDetailActivity.this)
                                    //                     .override(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH, (int)(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH/ com.threesome.shopme.LA.Constant.GOLDEN_RATIO))
                                    .load(store.getLinkPhotoStore().toString())
                                    .into(imgBanerStoreSlider);
                        }if (store.getNameStore() != null){
                            txtStoreName.setText(store.getNameStore());
                        }if (store.getEmailStore() != null){
                            txtEmailStore.setText(store.getEmailStore());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addEvents() {
        imgSlidemenu.setOnClickListener(this);
        btnCreateProduct.setOnClickListener(this);
        layoutMyProfileStore.setOnClickListener(this);
        layoutHomeDetailStore.setOnClickListener(this);
        imgSignOut.setOnClickListener(this);
        imgChangeBaner.setOnClickListener(this);

    }

    private void addControls() {
        imgChangeBaner = findViewById(R.id.imgChangeBanerMenu);
        txtStoreName = findViewById(R.id.txtStoreNameMenu);
        txtEmailStore = findViewById(R.id.txtEmailStoreMenu);
        imgSlidemenu = (ImageView) findViewById(R.id.imgSlideMenuStore);
        imgBanerStoreSlider = findViewById(R.id.imgBanerStoreSlide);
        imgSignOut = findViewById(R.id.imgSignOut);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_Store);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);
        layoutMyProfileStore = findViewById(R.id.linearHistoryStore);
        layoutHomeDetailStore = findViewById(R.id.layoutHomeDetailStore);
        txtEditProfile = findViewById(R.id.txtEditProfile);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty())
                    addSearchFragment(editable.toString());
                else{
                    switch(currentFragment){
                        case 1:
                            homeDetailStore();
                            break;
                        case 2:
                            createProduct();
                            break;
                        case 3:
                            myProfileFragment();
                            break;
                    }
                }
            }
        });
    }

    private void addSearchFragment(String search){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SearchFragment fragmentSearch = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentSearch.setArguments(bundle);
        fragmentTransaction.replace(R.id.category_container, fragmentSearch);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSlideMenuStore) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        if (id == R.id.txtEditProfile) {
            editProfile();
        }
        if (id == R.id.btnCreateProduct) {
            createProduct();
        }
        if (id == R.id.linearHistoryStore) {
            storeHistoryFragment();
        }
        if (id == R.id.imgSignOut) {
            signOut();
        }
        if (id == R.id.layoutHomeDetailStore) {
            homeDetailStore();
        }if (id == R.id.imgChangeBanerMenu){
            changerBanerStore ();
        }

        //showTextEditProfile
        if (indexEdit == 0) {
            txtEditProfile.setVisibility(View.GONE);
            txtEditProfile.setOnClickListener(null);
        } else if (indexEdit == 1) {
            txtEditProfile.setVisibility(View.VISIBLE);
            txtEditProfile.setText("Edit");
            txtEditProfile.setOnClickListener(this);
        } else if (indexEdit == 2) {
            txtEditProfile.setVisibility(View.VISIBLE);
            txtEditProfile.setText("Save");
            txtEditProfile.setOnClickListener(this);
        }
    }

    private void storeHistoryFragment() {
        drawerLayout.closeDrawers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentHistoryStore == null) {
            fragmentHistoryStore = new HistoryOrderStoreFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentHistoryStore.setArguments(bundle);
        fragmentTransaction.replace(R.id.category_container, fragmentHistoryStore);
        fragmentTransaction.commit();
    }

    private void changerBanerStore() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, Constant.REQUEST_CODE_LOAD_IMAGE);
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
        if (indexEdit == 1) {
            indexEdit = 2;
            txtEditProfile.setText("Save");
            fragmentProfileStore.showIcon(true);
        } else {
            indexEdit = 1;
            txtEditProfile.setText("Edit");
            fragmentProfileStore.showIcon(false);

        }
    }

    private void myProfileFragment() {
        drawerLayout.closeDrawers();
        indexEdit = 1;
        currentFragment = HISTORY;
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
        currentFragment = CREATE_PRODUCT;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CategoryFragment fragmentCategory = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentCategory.setArguments(bundle);
        fragmentTransaction.replace(R.id.category_container, fragmentCategory);
        fragmentTransaction.commit();
    }

    private void homeDetailStore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        } else {
            idStore = currentUser.getUid();
            drawerLayout.closeDrawers();
            indexEdit = 0;
            currentFragment = HOME;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            homeStoreDetailFragment = new HomeStoreDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID_STORE, idStore);
            homeStoreDetailFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.category_container, homeStoreDetailFragment);
            fragmentTransaction.commit();
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
        if (currentUser == null) {
            Toast.makeText(this, "Sign Outed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreDetailActivity.this, CustomMapsActivity.class));
        } else {
            idStore = currentUser.getUid();
            homeDetailStore();
        }
    }@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getAction() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                updateBanerStore();

            } else {
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    updateBanerStore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void updateBanerStore (){
        if (bitmap != null) {
            showProgressDialog("Loading...");
            StorageReference mountainsRef = mStorage.child(Constant.STORE).child(idStore);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgress();
                    Toast.makeText(StoreDetailActivity.this, "Update baner Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    linkCoverStore = String.valueOf(downloadUrl);
                    if (!linkCoverStore.equals("")) {
                        final DatabaseReference mChild = mData.child(Constant.STORE).child(idStore).child(Constant.LINKCBANERSTORE);
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    mChild.setValue(linkCoverStore);
                                    imgBanerStoreSlider.setImageBitmap(bitmap);
                                    hideProgress();
                                    Toast.makeText(StoreDetailActivity.this, "Update baner successfuly", Toast.LENGTH_SHORT).show();
                                }else {
                                    hideProgress();
                                    Toast.makeText(StoreDetailActivity.this, "Update baner Unsuccessfuly, Please check again!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgress();
                                Toast.makeText(StoreDetailActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void showProgressDialog (String s){
        progressDialog.setMessage(s);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgress (){
        if (progressDialog != null){
            progressDialog.hide();
        }
    }

}
