package com.threesome.shopme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.skyfishjy.library.RippleBackground;
import com.threesome.shopme.AT.signIn.RequestSignInActivity;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.AT.store.StoreDetailActivity;
import com.threesome.shopme.AT.store.userStoreDetail.UserStoreDetailActivity;
import com.threesome.shopme.AT.user.UserProfileActivity;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.AT.utility.DirectionsJSONParser;
import com.threesome.shopme.AT.utility.GeoLocat;
import com.threesome.shopme.Common.Common;
import com.threesome.shopme.Retrofit.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private double longtitude = 0.0, latitude = 0.0;
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
    private String idUser, idStore;
    private int heightFindme, widthFindme;

    private SupportMapFragment mapFragment;

    private int radius = 100;
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
    private FrameLayout frameStore;
    private ArrayList<GeoLocat> arrGeoLocation;
    private int index = 0;
    private TextView txtStoreName;
    private TextView txtAddressStore;
    private MapRipple mapRipple;
    private TextView txtShare;
    private LinearLayout layoutStore1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_maps);
        addControls();
        addEvents();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mService = Common.getGoogleAPI();
    }

    private void addEvents() {
        imgLogin.setOnClickListener(this);
        imgSlideMenu.setOnClickListener(this);
        txtAccount.setOnClickListener(this);
        rippleBackground.startRippleAnimation();
        layoutStore1.setOnClickListener(this);
        txtShare.setOnClickListener(this);
    }

    private void addControls() {
        txtAccount = findViewById(R.id.txtAccount);
        layoutStore1 = findViewById(R.id.layoutStore);
        frameStore= findViewById(R.id.frameStore);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgLogin = (ImageView) findViewById(R.id.imgLogin);
        imgSlideMenu = findViewById(R.id.imgSlideMenuMap);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.centerImage);
        arrGeoLocation = new ArrayList<>();

        txtShare = findViewById(R.id.txtShare);
        txtStoreName = findViewById(R.id.txtNameStoreList);
        txtAddressStore = findViewById(R.id.txtAddressStoreList);
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
            case R.id.layoutStore :
                detailStoreForUser ();
                break;
            case R.id.txtShare:
                shareApp();
                break;
            default:
                break;
        }
    }

    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Share app ShopMe");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Send app"));
    }

    private void detailStoreForUser() {
        if (idStore != null) {
            Intent intent = new Intent(CustomMapsActivity.this, UserStoreDetailActivity.class);
            intent.putExtra(Constant.ID_STORE, idStore);
            intent.putExtra(Constant.IS_STORE, false);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Lỗi định vị cửa hàng!", Toast.LENGTH_SHORT).show();
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

    private void setUpRipple(final LatLng latLng) {
        heightFindme = imageView.getLayoutParams().height;
        widthFindme = imageView.getLayoutParams().width;
        CountDownTimer Timer = new CountDownTimer(3000, 70) {
            public void onTick(long millisUntilFinished) {
                heightFindme -= 7;
                widthFindme -= 7;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(heightFindme, widthFindme);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                imageView.setLayoutParams(params);
            }

            public void onFinish() {
                rippleBackground.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private void setUpRippleMarker(final LatLng latLng) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rippleBackground.setVisibility(View.INVISIBLE);
                // mMap is GoogleMap object, latLng is the location on map from which ripple should start
                MapRipple mapRipple = new MapRipple(mMap, latLng, CustomMapsActivity.this);
                mapRipple.withFillColor(Color.GREEN);
                mapRipple.withStrokeColor(Color.GREEN);
                //mapRipple.withNumberOfRipples(3);// 10dp
                mapRipple.withDistance(450);   // 2000 metres radius
                mapRipple.withRippleDuration(2000);    //12000ms
                mapRipple.withTransparency(0.8f);
                mapRipple.startRippleMapAnimation();
            }
        }, 1000);
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
                                                .width(7).color(Color.rgb(0, 153, 51)).geodesic(true));
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
        DatabaseReference storeLocation = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = storeLocation.child("LocationStore");
        GeoFire geoFire = new GeoFire(mRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mPickupLocation.latitude, mPickupLocation.longitude), radius);
        //   geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("KEY", key + "");
                arrGeoLocation.add(new GeoLocat(key, location));
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
                Toast.makeText(CustomMapsActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setOnMarkerClickListener(this);
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
            drawMarkerCurrentLocation();
            // Use same procedure to stop Animation and start it again as mentioned anove in Default Ripple Animation Sample
            mPickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getClosetStore();
            drawMarkerStore();
        }
    }

    private void drawMarkerStore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (arrGeoLocation.size() > 0) {
                    double minDistance = calculationByDistance(arrGeoLocation.get(0).getLocation().latitude, arrGeoLocation.get(0).getLocation().longitude);
                    for (int i = 0; i < arrGeoLocation.size(); i++) {
                        double lon = arrGeoLocation.get(i).getLocation().longitude;
                        double lat = arrGeoLocation.get(i).getLocation().latitude;
                        double distance = calculationByDistance(lat, lon);
                        if (distance < minDistance) {
                            minDistance = distance;
                            index = i;
                        }
                    }
                    //draw marker
                    for (int i = 0; i < arrGeoLocation.size(); i++) {
                        double lon = arrGeoLocation.get(i).getLocation().longitude;
                        double lat = arrGeoLocation.get(i).getLocation().latitude;
                        LatLng latLng = new LatLng(lat, lon);
                        if (i == index) {
                            Marker mMarker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_active)));
                            mMarker.setTag(arrGeoLocation.get(i).getKey());
                            drawDirection(latLng);
                            idStore = arrGeoLocation.get(i).getKey().toString();
                            showStore(arrGeoLocation.get(i).getKey().toString());
                        } else {
                            Marker mMarker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_nonactive)));
                            mMarker.setTag(arrGeoLocation.get(i).getKey());
                        }
                    }
                    Log.d("DISTANCE", minDistance + "");
                }
            }
        }, 2000);
    }

    private void drawMarkerStoreAgain(String key) {
        if (arrGeoLocation.size() > 0) {
            //Clear map and draw marker agian
            mMap.clear();
            for (int i = 0; i < arrGeoLocation.size(); i++) {
                double lon = arrGeoLocation.get(i).getLocation().longitude;
                double lat = arrGeoLocation.get(i).getLocation().latitude;
                LatLng latLng = new LatLng(lat, lon);
                if (key.contains(arrGeoLocation.get(i).getKey())) {
                    idStore = arrGeoLocation.get(i).getKey().toString();
                    Marker mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_active)));
                    mMarker.setTag(arrGeoLocation.get(i).getKey());
                    drawDirection(latLng);
                } else {
                    Marker mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_nonactive)));
                    mMarker.setTag(arrGeoLocation.get(i).getKey());
                }
            }
        }
        drawMarkerCurrentLocationAgain();
        showStore(key);
    }

    private void showStore(String idStore) {
        if (isStore){
            //frameStore.setVisibility(View.INVISIBLE);
            layoutStore1.setOnClickListener(null);
            //layoutStore1.setVisibility(View.GONE);
        }else {
            layoutStore1.setOnClickListener(this);
           // layoutStore1.setVisibility(View.VISIBLE);
           // frameStore.setVisibility(View.VISIBLE);
        }
        mData.child(Constant.STORE).child(idStore).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Store store = dataSnapshot.getValue(Store.class);
                    if (store != null && store.getLinkPhotoStore() != null) {
                        txtStoreName.setText(store.getNameStore());
                        txtAddressStore.setText(store.getAddressStore());
                        layoutStore1.setBackgroundResource(R.drawable.bg_item_store);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Caculator Distance
    public double calculationByDistance(double lat, double lon) {
        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(lat - latitude);
        double dLon = Math.toRadians(lon - longtitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(lat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private void drawMarkerCurrentLocation() {
        LatLng latLng = new LatLng(latitude, longtitude);
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        setUpRipple(latLng);
    }

    private void drawMarkerCurrentLocationAgain() {
        LatLng latLng = new LatLng(latitude, longtitude);
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
        // mMap is GoogleMap object, latLng is the location on map from which ripple should start
        // mapRipple.startRippleMapAnimation();
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
    public boolean onMarkerClick(Marker marker) {
        try {
            drawMarkerStoreAgain(marker.getTag().toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
            Log.d("ER", ex.getMessage().toString());
        }
        return true;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void drawDirection(LatLng latLngStore) {
        // Getting URL to the Google Directions API
        LatLng origin = new LatLng(latitude, longtitude);
        String url = getDirectionsUrl(origin, latLngStore);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }
}
