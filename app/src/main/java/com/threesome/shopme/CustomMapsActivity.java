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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.Common.Common;
import com.threesome.shopme.Retrofit.IGoogleAPI;
import com.threesome.shopme.adapters.ItemStoreGoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    //RecyclerView
    RecyclerView itemStoreGmRv;
    ItemStoreGoogleMap itemStoreGoogleMap;
    String userId;
    private GoogleMap mMap;
    private Polyline line;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mPickupLocation;
    private DatabaseReference drivers;
    private GeoFire geoFire;
    private Marker mCurrent;
    private SupportMapFragment mapFragment;
    private Button logout, request;
    private int radius = 10;
    private boolean driverFound = false;
    private ArrayList<String> driverFoundIds = new ArrayList<>();
    private IGoogleAPI mService;

    //poly

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

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        logout = (Button) findViewById(R.id.logout);
//        request = (Button) findViewById(R.id.request);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CustomMapsActivity.this, StartActivity.class));
                finish();
                return;
            }
        });


        userId = FirebaseAuth.getInstance().getUid();
//        request.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                request.setText("GETING YOUR DRIVER");
//
//                getClosetStore();
//            }
//        });

        initItemRCV();

        mService = Common.getGoogleAPI();
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
                driverFoundIds.add(key);
                itemStoreGoogleMap.notifyDataSetChanged();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude)));

                if (!driverFound) {
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

        mMap.setMyLocationEnabled(true);
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
        mLastLocation = location;

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        if (mCurrent != null)
//            mCurrent.remove();
//        mCurrent = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Your location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

        GeoFire geoFire = new GeoFire(ref);

        if (userId != null || !userId.equals(""))
            geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        mPickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        getClosetStore();



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
}
