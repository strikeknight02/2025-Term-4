package com.example.wowcher.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.VouchersListActivity;
import com.example.wowcher.classes.Location;
import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.LocationController;
import com.example.wowcher.controller.LocationControllerFactory;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.LocationSource;
import com.example.wowcher.db.UserSource;
import com.example.wowcher.db.VoucherSource;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


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

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    UserController userModel;
    VoucherController voucherModel;
    LocationController locationModel;

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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Voucher
        DBSource voucherSourceInstance = new VoucherSource(db);
        voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        //Location
        DBSource locationSourceInstance = new LocationSource(db);
        locationModel = new ViewModelProvider(this, new LocationControllerFactory(locationSourceInstance)).get(LocationController.class);
        locationModel.getModelInstance(locationModel);

        userModel.getUserInfoFromSource("userId", user.getUid());

        final Observer<ArrayList<Location>> locationObserver = new Observer<ArrayList<Location>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Location> locationList) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (locationList !=null)){
                    for (Location location: locationList) {
                        GeoPoint geoObject = location.getGeolocation();
                        LatLng latlngObject = new LatLng(geoObject.getLatitude(), geoObject.getLongitude());
                        // temp image, still figuring out how to handle the marker images
                        addMarkerToMap(latlngObject, R.drawable.marker_mcd);
                    }
                }

            }
        };

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {

                if(voucherList != null){
                    //TODO ENSURE IT UPDATES AFTER REDEMPTION
                    ArrayList<String> voucherIdList = new ArrayList<>();
                    for(Voucher v : voucherList){
                        if (!voucherIdList.contains(v.getLocationId())){
                            voucherIdList.add(v.getLocationId());
                        }
                    }

                    Log.d("GETTING LOCATIONS", voucherIdList.toString());
                    locationModel.getLocationsBasedOnVoucher(voucherIdList);
                    locationModel.locationBasedVouchers().observe(getViewLifecycleOwner(), locationObserver);

                } else {
                    Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (user !=null)){
                    ArrayList<String> redeemedVouchers = user.getRedeemedVouchers();

                    //TODO supposed to get all now?
                    voucherModel.getVouchersforAll(redeemedVouchers);
                    voucherModel.getAllVouchers().observe(getViewLifecycleOwner(), voucherObserver);
                }

            }
        };


        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);

        return currentView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Location attributes = (Location) marker.getTag(); // Get the attributes from the marker

                if (attributes != null) {
                    Log.d("DEBUG", "Marker clicked for locationId: " + attributes.getLocationId());

                    Intent in = new Intent(requireContext(), VouchersListActivity.class);
                    Bundle b1 = new Bundle();
                    b1.putString("locationId", attributes.getLocationId());
                    in.putExtras(b1);
                    startActivity(in);
                } else {
                    Log.w("DEBUG", "Marker has no tag!");
                }

                return false;
            }
        });

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
            fetchLocationsAndDisplayMarkers();
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
    public void addMarkerToMap(LatLng location, int imageId, Location attributes) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(customMarker(imageId)))
                .flat(true);

        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(attributes); // Attach the location data to this specific marker
        }
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
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                fetchUserLocation(currentContext, fusedLocationProviderClient);
            } else {
                // Permission Denied or grantResults is empty
                Toast.makeText(currentContext, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
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
            if (addressList != null) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchLocationsAndDisplayMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Location> locationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Location location = document.toObject(Location.class);
                        locationList.add(location);
                    }

                    // Now you can use your original loop
                    for (Location location : locationList) {

                        GeoPoint geoObject = location.getGeolocation();
                        LatLng latlngObject = new LatLng(geoObject.getLatitude(), geoObject.getLongitude());
                        String imageName = "marker_" + location.getImageName();
                        int imageResId = currentContext.getResources().getIdentifier(imageName, "drawable", currentContext.getPackageName());
                        if (imageResId != 0) {
                            addMarkerToMap(latlngObject, imageResId,location);
                        } else {
                            addMarkerToMap(latlngObject, R.drawable.lebron_james,location);
                        }
                        // Temporary marker image

                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error fetching locations", e));
    }
    }
