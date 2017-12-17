package com.threesome.shopme.AT.createstore;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shuhart.stepview.StepView;
import com.threesome.shopme.AT.GPSTracker.GPSTracker;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.AT.store.StoreDetailActivity;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RegisterStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtContinue, txtBack;
    private StepView stepView;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FragmentTransaction fragmentTransaction;
    private LinearLayout layoutContinue;
    public ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private GPSTracker gps;
    private int INDEX = 0;
    private double latitude = 0, longitude = 0;
    private Bitmap bitmap = null;
    private StorageReference mStorage;
    private String emailStore = "", password = "", confirmPassword = "", nameStore = "", phoneNumber = "", address = "", linkCoverStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);
        addControls ();
        addEvetns ();
    }

    private void addEvetns() {
        txtContinue.setOnClickListener(this);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.go(stepView.getCurrentStep() - 1, true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
           // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gps.stopUsingGPS();
    }

    private void addControls() {
        mData = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        gps = new GPSTracker(this);
        firstFragment = new FirstFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_content, firstFragment, "FIRST_FRAGMENT");
        fragmentTransaction.commit();
        txtContinue = findViewById(R.id.txtContinue);
        layoutContinue = findViewById(R.id.layoutContinue);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        txtBack = findViewById(R.id.txtBack);
        stepView = findViewById(R.id.step_view);
        stepView.setSteps(new ArrayList<String>() {{
            add("");
            add("");
            add("");
        }});
        //show Dialog request location
        AlertDialog.Builder mAler = new AlertDialog.Builder(this);
        mAler.setMessage("Vui lòng đứng tại cửa hàng đăng kí để chúng tôi hiển thị chính xác vị trí của bạn trên bản đồ!");
        mAler.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mAler.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        mAler.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtContinue:
                continueCreateStore ();
                break;
            case R.id.txtBack:
                backCreateStore ();
                break;
        }
    }

    private void backCreateStore() { Fragment fragment = null;
        if (INDEX == 2){
            Bundle bundle = new Bundle();
            bundle.putString(Constant.STORE_EMAIL, emailStore);
            bundle.putString(Constant.PASSWORD, password);
            bundle.putString(Constant.STORE_NAME, nameStore);
            bundle.putString(Constant.STORE_ADDRESS, address);
            bundle.putString(Constant.STORE_PHONENUMBER, phoneNumber);
            secondFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, secondFragment).commit();
            txtContinue.setText("Continue");
            txtBack.setVisibility(View.VISIBLE);
            txtBack.setOnClickListener(this);
            INDEX = 1;
        }else if (INDEX == 1){
            Bundle bundle = new Bundle();
            bundle.putString(Constant.STORE_EMAIL, emailStore);
            bundle.putString(Constant.PASSWORD, password);
            bundle.putString(Constant.CONFIRM_PASSWORD, confirmPassword);
            firstFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, firstFragment).commit();
            txtContinue.setText("Continue");
            txtBack.setVisibility(View.INVISIBLE);
            txtBack.setOnClickListener(null);
            INDEX = 0;
        }
        stepView.go(stepView.getCurrentStep() - 1, true);
    }

    private void continueCreateStore() {
        if (INDEX == 0){
            if (firstFragment.isNext()) {
                showProgress("Loading...");
                emailStore = firstFragment.getEmail();
                password = firstFragment.getPassword();
                confirmPassword = firstFragment.getConfirmPassword();
                createNewStore(firstFragment.getEmail());
            }
        }else if (INDEX == 1){
            if (secondFragment.isNext()) {
                emailStore = secondFragment.getEmailStore();
                password = secondFragment.getPassword();
                nameStore = secondFragment.getNameStore();
                address = secondFragment.getAddressStore();
                phoneNumber = secondFragment.getPhoneStore();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.STORE_EMAIL, emailStore);
                bundle.putString(Constant.PASSWORD, password);
                bundle.putString(Constant.STORE_NAME, nameStore);
                bundle.putString(Constant.STORE_ADDRESS, address);
                bundle.putString(Constant.STORE_PHONENUMBER, phoneNumber);
                thirdFragment = new ThirdFragment();
                thirdFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, thirdFragment).commit();
                txtContinue.setText("Complete");
                txtBack.setVisibility(View.VISIBLE);
                txtBack.setOnClickListener(this);
                INDEX = 2;
                stepView.go(stepView.getCurrentStep() + 1, true);
            }
        }else if (INDEX == 2){
            showProgress("Loading...");
            if (thirdFragment != null) {
                bitmap = thirdFragment.getBitmap();
                mAuth.createUserWithEmailAndPassword(emailStore, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addStoreToFirebase(task.getResult().getUser().getUid().toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgress();
                        Toast.makeText(RegisterStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                hideProgress();
            }
        }

    }

    private void addStoreToFirebase(final String idStore) {
        if (bitmap != null) {
            showProgress("Loading...");
            StorageReference mountainsRef = mStorage.child(Constant.STORE).child(idStore);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgress();
                    Toast.makeText(RegisterStoreActivity.this, "Upload cover Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    linkCoverStore = String.valueOf(downloadUrl);
                    if (!linkCoverStore.equals("")) {
                        final DatabaseReference mChild = mData.child(Constant.STORE).child(idStore);
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null){
                                    if (latitude != 0 && longitude != 0){
                                        HashMap<String, Double> location = new HashMap<>();
                                        location.put(Constant.LONGTITUDE, longitude);
                                        location.put(Constant.LATITUDE, latitude);
                                        Store store = new Store(idStore, nameStore, linkCoverStore, address, emailStore, phoneNumber, location);
                                        mChild.setValue(store);
                                        hideProgress();
                                        Toast.makeText(RegisterStoreActivity.this, "Registation a Store Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterStoreActivity.this, StoreDetailActivity.class));
                                    }else {
                                        hideProgress();
                                        Toast.makeText(RegisterStoreActivity.this, "We dont have your location, Please check GPS again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }


    private void createNewStore(final String email) {
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.getResult().getProviders().size() > 0){
                    hideProgress();
                    Toast.makeText(RegisterStoreActivity.this, "Email đã đăng kí, vui lòng chọn Email khác!", Toast.LENGTH_SHORT).show();
                }
                else {
                    hideProgress();
                    secondFragment = new SecondFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.STORE_EMAIL, emailStore);
                    bundle.putString(Constant.PASSWORD, password);
                    bundle.putString(Constant.CONFIRM_PASSWORD, confirmPassword);
                    bundle.putString(Constant.STORE_NAME, nameStore);
                    bundle.putString(Constant.STORE_ADDRESS, address);
                    bundle.putString(Constant.STORE_PHONENUMBER, phoneNumber);
                    secondFragment.setArguments(bundle);
                    txtBack.setVisibility(View.VISIBLE);
                    txtBack.setOnClickListener(RegisterStoreActivity.this);
                    INDEX = 1;
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, secondFragment).commit();
                    stepView.go(stepView.getCurrentStep() + 1, true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                Toast.makeText(RegisterStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideProgress() {
        progressDialog.hide();
    }
}
