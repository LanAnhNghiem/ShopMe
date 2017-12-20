package com.threesome.shopme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodolfonavalon.shaperipplelibrary.ShapeRipple;
import com.skyfishjy.library.RippleBackground;
import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.AT.signIn.RequestSignInActivity;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.AT.store.StoreDetailActivity;
import com.threesome.shopme.AT.user.UserProfileActivity;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.Common.Common;
import com.threesome.shopme.Retrofit.IGoogleAPI;
import com.threesome.shopme.adapters.ItemStoreGoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMapsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private double longtitude = 0.0, latitude = 0.0;
    RecyclerView itemStoreGmRv;
    ItemStoreGoogleMap itemStoreGoogleMap;
    //    private ImageView imgSidemenu;
    private DrawerLayout drawer;
    private ImageView imgLogin, imgSlideMenu;
    private TextView txtAccount;
    //    String userId;
    private GoogleMap mMap;
    private Polyline line = null;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mPickupLocation;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private boolean isStore = false;
    private String idUser;
    private int heightFindme, widthFindme, heightRipple, widhtRipple;

    private SupportMapFragment mapFragment;

    private int radius = 10;
    private boolean driverFound = false;
    private Set<String> driverFoundIds = new HashSet<>();
    private IGoogleAPI mService;

    private boolean isFirstLoad = false;

    public static List<LatLng> decode(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    //Createtor AnhTam : 19/12/2017
    private RippleBackground rippleBackground;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_maps);
        addControls();
        addEvents();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initItemRCV();
        mService = Common.getGoogleAPI();
    }

    private void addEvents() {
        imgLogin.setOnClickListener(this);
        imgSlideMenu.setOnClickListener(this);
        txtAccount.setOnClickListener(this);
    }

    private void addControls() {
        txtAccount = findViewById(R.id.txtAccount);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgLogin = (ImageView) findViewById(R.id.imgLogin);
        imgSlideMenu = findViewById(R.id.imgSlideMenuMap);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.centerImage);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgLogin:
                signIn();
                break;
            case R.id.imgSlideMenuMap:
                opentSlideMenu();
                break;
            case R.id.txtAccount:
                moveToAccountActivity();
                break;
            default:
                break;
        }
    }

    private void signIn() {
        if (idUser == null) {
            startActivity(new Intent(CustomMapsActivity.this, RequestSignInActivity.class));
        } else {
            signOut();
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
                Toast.makeText(CustomMapsActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                txtAccount.setTextColor(getResources().getColor(R.color.color_text));
                imgLogin.setImageResource(R.drawable.btnsignin);
                idUser = null;
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

    private void setUpRipple() {
        rippleBackground.startRippleAnimation();
        heightFindme = imageView.getLayoutParams().height;
        widthFindme = imageView.getLayoutParams().width;
        CountDownTimer Timer = new CountDownTimer(3000, 80) {
            public void onTick(long millisUntilFinished) {
                heightFindme -= 6;
                widthFindme -= 6;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(heightFindme, widthFindme);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                imageView.setLayoutParams(params);
            }

            public void onFinish() {
                rippleBackground.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private void moveToAccountActivity() {
        if (isStore) {
            startActivity(new Intent(CustomMapsActivity.this, StoreDetailActivity.class));
        } else {
            startActivity(new Intent(CustomMapsActivity.this, UserProfileActivity.class));
        }
    }

    private void opentSlideMenu() {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            txtAccount.setTextColor(getResources().getColor(R.color.color_text));
            imgLogin.setImageResource(R.drawable.btnsignin);
        } else {
            idUser = user.getUid();
            txtAccount.setTextColor(getResources().getColor(R.color.btnRegister));
            imgLogin.setImageResource(R.drawable.btn_signout);
            if (idUser != null) {
                checkStore();
            }
        }
        // mGoogleApiClient.connect();
    }

    private void checkStore() {
        mData.child(Constant.STORE).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    isStore = true;
                } else {
                    mData.child(Constant.USER).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                isStore = false;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initItemRCV() {
        itemStoreGmRv = (RecyclerView) findViewById(R.id.itemStoreGmRv);
        itemStoreGoogleMap = new ItemStoreGoogleMap(this, driverFoundIds);
        itemStoreGmRv.setAdapter(itemStoreGoogleMap);
        itemStoreGmRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(itemStoreGmRv);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getDirection(LatLng store) {
        LatLng currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + store.latitude + "," + store.longitude + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);

            Log.d("NhanD", requestApi);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                String status = jsonObject.getString("status");
                                if (status.equals("NOT_FOUND")) {
                                    Toast.makeText(CustomMapsActivity.this, "NOT FOUND", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    List<LatLng> list = decode(polyline);

                                    for (int z = 0; z < list.size() - 1; z++) {
                                        LatLng src = list.get(z);
                                        LatLng dest = list.get(z + 1);
                                        line = mMap.addPolyline(new PolylineOptions()
                                                .add(new LatLng(src.latitude, src.longitude),
                                                        new LatLng(dest.latitude, dest.longitude))
                                                .width(5).color(Color.BLUE).geodesic(true));
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomMapsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClosetStore() {
        DatabaseReference storeLocation = FirebaseDatabase.getInstance().getReference("DriverAvailable");

        GeoFire geoFire = new GeoFire(storeLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mPickupLocation.latitude, mPickupLocation.longitude), radius);

        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                int size = driverFoundIds.size();
                driverFoundIds.add(key);
                itemStoreGoogleMap.notifyDataSetChanged();
                if (driverFoundIds.size() - size == 1)
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude)));

                if (driverFoundIds.size() == 2) {
                    driverFound = true;
                    getDirection(new LatLng(location.latitude, location.longitude));
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (!isFirstLoad) {
            isFirstLoad = true;
            mLastLocation = location;
            longtitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            LatLng latLng = new LatLng(latitude, longtitude);
            // Creating an instance of MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting latitude and longitude for the marker
            markerOptions.position(latLng);
            //  markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_account));
            // Adding marker on the Google Map
            // mMap.addMarker(markerOptions);
            setUpRipple();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            // mMap is GoogleMap object, latLng is the location on map from which ripple should start
            MapRipple mapRipple = new MapRipple(mMap, latLng, CustomMapsActivity.this);
            mapRipple.withFillColor(R.color.color_green);
            //mapRipple.withNumberOfRipples(3);// 10dp
            mapRipple.withDistance(400);   // 2000 metres radius
            mapRipple.withRippleDuration(2000);    //12000ms
            mapRipple.withTransparency(0.5f);
            mapRipple.startRippleMapAnimation();
            // Use same procedure to stop Animation and start it again as mentioned anove in Default Ripple Animation Sample
            mPickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getClosetStore();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
}
