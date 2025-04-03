package com.example.wowcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class Map extends AppCompatActivity implements OnMapReadyCallback {
    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    private CameraPosition cameraPosition;
    private LatLng userLocation;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_maps_test);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve API key for initialize Places to get data of places
        String apiKey = getApiKeyFromManifest(this);
        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(this, apiKey);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If don't have, request for permissions
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            // Otherwise, retrieve current user location
            fetchUserLocation(this, fusedLocationProviderClient);
        }

    }

    // Function to fetch the user's location
    public void fetchUserLocation(Context context, FusedLocationProviderClient fusedLocationClient) {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Fetch the last known location
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        // Update the user's location in the state
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Initialize camera position of user location
                        cameraPosition = CameraPosition.fromLatLngZoom(userLocation, 20f);

                        // Move camera to user location
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        // Add marker to user location
                        mMap.addMarker(
                                new MarkerOptions()
                                        .position(userLocation)

                        );

                        // Enable necessary map functions
                        enableMapsSettings();
                    } else {
                        System.out.println("didnt get location");
                    }
                });
            } catch (SecurityException e) {
                Log.e("Location Error", "Permission for location access was revoked: " + e.getLocalizedMessage());
            }
        } else {
            Log.e("Location Error", "Location permission is not granted.");
        }
    }

    // Handles selected place from the search results - TODO
    public void selectLocation(String selectedPlace, Context context){
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addressList = geocoder.getFromLocationName(selectedPlace, 1);
            if(!addressList.isEmpty()){
                Address address = addressList.get(0);
                selectedLocation = new LatLng(address.getLatitude(), address.getLongitude());
            }else{
                Toast.makeText(this, "No location found for the selected place.", Toast.LENGTH_LONG)
                        .show();
            }
        }catch(IOException e){
            Log.e("Get addresses fail", "Error: "+e);
        }


    }

    // Suppressed permission check since this will only be used after permission is checked
    @SuppressLint("MissingPermission")
    public void enableMapsSettings() {
        // Enable maps functions
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    // Handles after requesting for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // Retrieve user location
                    fetchUserLocation(this, fusedLocationProviderClient);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Retrieve API key
    public static String getApiKeyFromManifest(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("API key retrieval failed", "Error: " + e);
            return null;
        }
    }
}
