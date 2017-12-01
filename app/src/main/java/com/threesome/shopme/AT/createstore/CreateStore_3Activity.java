package com.threesome.shopme.AT.createstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.AT.utility.Utility;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.LA.SplashActivity;
import com.threesome.shopme.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateStore_3Activity extends Utility implements View.OnClickListener {

    public static final int PICK_IMAGE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static GoogleApiClient mGoogleApiClient;
    private final String TAG = SplashActivity.class.getSimpleName();
    private TextView txtContinue;
    private String email = "", idStore = "", nameStore = "", address = "", phoneNumber = "", linkPhoto = "";
    private DatabaseReference mData;
    private StorageReference mStorageRef;
    private ImageView imgAvata, imgChooseImage;
    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };
    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                    Toast.makeText(context, R.string.GPS_enabled, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CreateStore_3Activity.this, CustomMapsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                } else {
                    //If GPS turned OFF show Location Dialog
                    Toast.makeText(context, R.string.GPS_disabled, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(sendUpdatesToUI, 5);
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creater_store_3);
        addControls();
        addEvetns();

        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(CreateStore_3Activity.this, CustomMapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);


        }
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(CreateStore_3Activity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /*Show Location Access Dialog. Search SettingClient for more detail*/
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000); //5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //Toast.makeText(SplashActivity.this, getResources().getString(R.string.GPS_enabled), Toast.LENGTH_SHORT).show();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        CreateStore_3Activity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateStore_3Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(CreateStore_3Activity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(CreateStore_3Activity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(CreateStore_3Activity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE) {
            //TODO: action
            //linkPhoto = data.getData().toString();
            try {
                imgAvata.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage();
        }

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //Toast.makeText(this, getResources().getString(R.string.GPS_enabled), Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        //Toast.makeText(this, getResources().getString(R.string.GPS_disabled), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else {
                        showSettingDialog();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
                    Toast.makeText(CreateStore_3Activity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void addEvetns() {
        txtContinue.setOnClickListener(this);
        imgChooseImage.setOnClickListener(this);
    }

    private void addControls() {
        Intent intent = getIntent();
        email = intent.getStringExtra(Constant.STORE_EMAIL);
        idStore = intent.getStringExtra(Constant.ID_STORE);
        nameStore = intent.getStringExtra(Constant.STORE_NAME);
        address = intent.getStringExtra(Constant.STORE_ADDRESS);
        phoneNumber = intent.getStringExtra(Constant.STORE_PHONENUMBER);
        txtContinue = findViewById(R.id.txtComplete);
        imgAvata = findViewById(R.id.imgAvataStore);
        imgChooseImage = findViewById(R.id.imgChooseImage);
        mData = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.txtComplete:
                showProgress("Loading...");
                continueCreateStore(linkPhoto);
                break;
            case R.id.imgChooseImage:
                chooseImage();
                break;
            default:
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void uploadImage() {
        StorageReference mountainImagesRef = mStorageRef.child(Constant.STORE).child(idStore).child("Avatar");
        // Get the data from an ImageView as bytes
        imgAvata.setDrawingCacheEnabled(true);
        imgAvata.buildDrawingCache();
        Bitmap bitmap = imgAvata.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkPhoto = downloadUrl.toString();
            }
        });
    }

    private void continueCreateStore(final String linkPhoto) {
        final DatabaseReference mRef = mData.child(Constant.STORE).child(idStore);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Store store = new Store();
                    store.setIdStore(idStore);
                    store.setPhoneNumber(phoneNumber);
                    store.setNameStore(nameStore);
                    store.setEmailStore(email);
                    store.setAddressStore(address);
                    store.setLinkPhotoStore(linkPhoto);
                    mRef.setValue(store);
                    hideProgress();
                    Toast.makeText(CreateStore_3Activity.this, "Register new store successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
                Toast.makeText(CreateStore_3Activity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
