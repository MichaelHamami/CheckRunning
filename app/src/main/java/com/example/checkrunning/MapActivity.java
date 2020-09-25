package com.example.checkrunning;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    //google map object
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;

    TextView txt_time;
    TextView txt_distance;
    TextView txt_speed;

    private long time = 1;
    private double speed;
    private double distance;
    private Location lastLocation = null;

    Runnable secondlyRun;
    private Handler handler = new Handler();
    private boolean timer_start = false;
    private int timer_value = 1;
    private final int DELAY = 1000;


    private final int ZOOM_VALUE = 20;


    //    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest);
    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    //polyline object
    private List<Polyline> polylines = null;
    private Polyline polyline;
    private ArrayList<LatLng> points = new ArrayList();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("phpstl", "onCreate");
//        MyToaster.getInstance().showToast("On create called");
        setContentView(R.layout.activity_map);
        txt_time = findViewById(R.id.map_TXT_time);
        txt_distance = findViewById(R.id.map_TXT_distance);
        txt_speed = findViewById(R.id.map_TXT_speed);
        timer_start = true;
        secondlyRun = new Runnable() {
            public void run() {
                time++;
                txt_time.setText("" + time);

//                timer_value++;

                {
                    handler.postDelayed(this, DELAY);
                }
            }
        };
        secondlyRun.run();
        Toast.makeText(getBaseContext(), "On Create", Toast.LENGTH_LONG).show();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        //request location permission.
        requestPermision();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("phpstl", "onLocationResult");
                if (locationResult == null) {
                    return;
                }
//                polylines = new ArrayList<>();
                PolylineOptions polyOptions = new PolylineOptions();
//                ArrayList<LatLng> localpoints = new ArrayList<>();
                if (polyline != null) polyline.remove();
                LatLng lastLatLng = null;
                Location currentLocation = locationResult.getLastLocation();
                for (Location location : locationResult.getLocations()) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    points.add(point);
                    Log.d("phpstl", "point is:" + point.toString());
                    lastLatLng = point;
                }
                if (lastLocation != null) {
                    // if one of them not equel then location changed.... so we get distance
                    if (lastLocation.getLatitude() != currentLocation.getLatitude() && lastLocation.getLongitude() != currentLocation.getLongitude()) {
                        distance += lastLocation.distanceTo(currentLocation);
                    }
                }
                lastLocation = locationResult.getLastLocation();
                txt_distance.setText("" + distance);
                txt_speed.setText("" + (distance / time));
                polyOptions.addAll(points);
                polyOptions.width(15);
                polyOptions.color(Color.BLACK);
                polyOptions.geodesic(true);
                polyline = mMap.addPolyline(polyOptions);
                Log.d("phpstl", "polyline is:" + polyline.toString());
//                polylines.add(polyline);
                Log.d("phpstl", "LastLatLng is:" + lastLatLng.latitude + " lng is: " + lastLatLng.longitude);

                if (lastLatLng != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_VALUE));
            }
        };
//        if (locationPermission) {
//            Log.d("phpstl", "before calling Requect Location Update");
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        }

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("phpstl", "onRequestPermissionsResult");
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //to get user location
    @SuppressLint("MissingPermission")
    private void getMyLocation() {

        if (locationPermission) {
            Log.d("phpstl", "before calling Requect Location Update");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();
    }
}