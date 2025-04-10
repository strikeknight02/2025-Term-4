package com.example.wowcher.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.wowcher.R;
import com.example.wowcher.VouchersListActivity;
import com.example.wowcher.classes.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.GeoPoint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;


public class Map extends Fragment implements OnMapReadyCallback {
    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    public static LatLng userLocation;

    private ArrayAdapter<String> autoCompleteAdapter;

    private PlacesClient placesClient;

    private AutocompleteSessionToken autocompleteSessionToken;

    private LatLng selectedLocation;

    private boolean gpsEnabled = false;

    private View currentView;

    private Context currentContext;

    private ArrayList<Location> locationList = new ArrayList<Location>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_map, container, false);
        currentContext = currentView.getContext();
        super.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(currentContext);

        // Retrieve API key for initialize Places to get data of places
        String apiKey = getApiKeyFromManifest("com.google.android.geo.API_KEY");

        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(currentContext, apiKey);
        }

        // Necessary variables for getting places from Google Maps
        autoCompleteAdapter = new ArrayAdapter<String>(currentContext, android.R.layout.simple_dropdown_item_1line);
        placesClient = Places.createClient(currentContext);
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();

        // Setup search bar
        searchBarSetup();

        return currentView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationEnabled();

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(currentContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(currentContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // If don't have, request for permissions
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            // Otherwise, retrieve current user location
            fetchUserLocation(currentContext, fusedLocationProviderClient);
            makeLocationTestData();
        }

    }

    // Function to setup the search bar
    public void searchBarSetup(){
        // Search Bar View
        AutoCompleteTextView autoCompleteTextView = currentView.findViewById(R.id.searchbar);

        // Handle when type in search bar
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                System.out.println("what");
                if (!query.isEmpty()) {
                    System.out.println(query);
                    // Build a request to fetch autocomplete predictions based on the user's input
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(autocompleteSessionToken)
                            .setQuery(query)
                            .build();

                    // Fetch autocomplete predictions from the Places API
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        autoCompleteAdapter.clear(); // Clear the previous suggestions
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            // Add each prediction to the adapter for displaying in the dropdown
                            autoCompleteAdapter.add(prediction.getFullText(null).toString());
                        }
                        autoCompleteAdapter.notifyDataSetChanged(); // Notify the adapter to update the dropdown
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        autoCompleteTextView.setAdapter(autoCompleteAdapter);

        // Set an item click listener to handle when the user selects a suggestion
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = autoCompleteAdapter.getItem(position);
            if (selectedPlace != null) {
                // Call the callback function to handle the selected place
                selectLocation(selectedPlace, currentContext);
            }
        });
    }

    // Check if user location service is enabled
    public void checkLocationEnabled() {
        LocationManager lm = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e("LOCATION SERVICES ERROR", "ERROR: " + ex);
        }

        if (!gpsEnabled) {
            // Notify user
            Toast.makeText(currentContext, "Please enable location services.", Toast.LENGTH_LONG)
                    .show();
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
                        CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(userLocation, 15f);
                        moveCameraToLocation(cameraPosition);

                        // Enable maps functions
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);

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

    // Add marker to user location
    public void addMarkerToMap(LatLng location, int imageId) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(customMarker(imageId)))
                .flat(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Intent in = new Intent(requireContext(), VouchersListActivity.class);
                Bundle b1 = new Bundle();
                b1.putString("locationId", "Uwc62HtzeDrk97Gx3fxh");
                in.putExtras(b1);
                startActivity(in);
                return false;
            }
        });

        mMap.addMarker(markerOptions);
    }

    // Create custom marker from image from drawable
    private Bitmap customMarker(int resId) {
        Bitmap bitmap = null;
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), resId, null);
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(200, 250, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        return bitmap;
    }

    // Move camera to user location
    public void moveCameraToLocation(CameraPosition cameraPosition) {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // Handles after requesting for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // Retrieve user location
                    fetchUserLocation(currentContext, fusedLocationProviderClient);
                } else {
                    // Permission Denied
                    Toast.makeText(currentContext, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Handle selecting location from recommended search
    public void selectLocation(String selectedPlace, Context context) {
        Geocoder geocoder = new Geocoder(context);
        try {
            // Obtain list of addresses in Google Maps from selected place
            List<Address> addressList = geocoder.getFromLocationName(selectedPlace, 1);

            // Handle if selected location is found
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);

                selectedLocation = new LatLng(address.getLatitude(), address.getLongitude());

                CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(selectedLocation, 20f);
                moveCameraToLocation(cameraPosition);

            }
            // Handle if selected location is not found
            else {
                Toast.makeText(currentContext, "No location found for the selected place.", Toast.LENGTH_LONG)
                        .show();
            }
        } catch (IOException e) {
            Log.e("Get addresses fail", "Error: " + e);
        }


    }

    public String getApiKeyFromManifest(String key) {
        String apiKey;
        try {
            ApplicationInfo applicationInfo = currentContext.getPackageManager().getApplicationInfo(currentContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("API key retrieval failed", "Error: " + e);
            return null;
        }
        return apiKey;
    }

    // todo (Maryse) - this is where the call is to get location based on vouchers
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeLocationTestData(){
        locationList.add(new Location("Uwc62HtzeDrk97Gx3fxh", 0, new GeoPoint(1.334708,103.963177), "")); // tofu
        locationList.add(new Location("cjhivi5QhPQKEzsuO1zs", 0,new GeoPoint(1.343304, 103.962652), "")); // bread
        locationList.add(new Location("WTPg6z7sim0z7ZmNcaWY", 0, new GeoPoint(1.341547, 103.961101), "")); // noods

        // todo (Maryse) - on successful, handle response
        for (Location location: locationList) {
            GeoPoint geoObject = location.getGeolocation();
            LatLng latlngObject = new LatLng(geoObject.getLatitude(), geoObject.getLongitude());
            // temp image, still figuring out how to handle the marker images
            addMarkerToMap(latlngObject, R.drawable.marker);
        }
    }
}
