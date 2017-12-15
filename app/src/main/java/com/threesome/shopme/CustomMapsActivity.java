package com.threesome.shopme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skyfishjy.library.RippleBackground;
import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.AT.signIn.RequestSignInActivity;
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
    RecyclerView itemStoreGmRv;
    ItemStoreGoogleMap itemStoreGoogleMap;
    //    private ImageView imgSidemenu;
    private DrawerLayout drawer;
    private TextView txtCreateStore;
    private ImageView imgLogin;
    //    String userId;
    private GoogleMap mMap;

    private Polyline line = null;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mPickupLocation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_maps);


        addControls();
        addEvents();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        userId = FirebaseAuth.getInstance().getUid();

        initItemRCV();

        mService = Common.getGoogleAPI();

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        ImageView imageView = (ImageView) findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
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

            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

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

    private void addEvents() {
        txtCreateStore.setOnClickListener(this);
        imgLogin.setOnClickListener(this);
    }

    private void addControls() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        txtCreateStore = findViewById(R.id.txtCreateStore);
        imgLogin = (ImageView) findViewById(R.id.imgLogin);
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
            case R.id.txtCreateStore:
                createNewStore();
                break;
            case R.id.imgLogin:
                startActivity(new Intent(CustomMapsActivity.this, RequestSignInActivity.class));
            default:
                break;
        }
    }

    private void createNewStore() {
        startActivity(new Intent(CustomMapsActivity.this, RegisterStoreActivity.class));
    }
}
