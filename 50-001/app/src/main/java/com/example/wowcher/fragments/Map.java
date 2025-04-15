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
//            makeLocationTestData();
//            seedLocationsToFirestore();
//            addVouchersToLocations();
//            generateMultipleVouchersPerLocation();
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

    // todo (Maryse) - this is where the call is to get location based on vouchers
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void makeLocationTestData(){
//        locationList.add(new Location("Uwc62HtzeDrk97Gx3fxh", 0, new GeoPoint(1.334708,103.963177), "", "")); // tofu
//        locationList.add(new Location("cjhivi5QhPQKEzsuO1zs", 0,new GeoPoint(1.343304, 103.962652), "","")); // bread
//        locationList.add(new Location("WTPg6z7sim0z7ZmNcaWY", 0, new GeoPoint(1.341547, 103.961101), "","")); // noods
//
//        // todo (Maryse) - on successful, handle response
//        for (Location location: locationList) {
//            GeoPoint geoObject = location.getGeolocation();
//            LatLng latlngObject = new LatLng(geoObject.getLatitude(), geoObject.getLongitude());
//            // temp image, still figuring out how to handle the marker images
//            addMarkerToMap(latlngObject, R.drawable.marker);
//        }
//    }

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


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void seedLocationsToFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        List<Location> locations = Arrays.asList(
//                new Location("", 1, new GeoPoint(1.2630, 103.6350), "2025-04-14T12:00:00", "Dairy Queen"),
//                new Location("", 2, new GeoPoint(1.4285, 103.8352), "2025-04-14T12:01:00", "Taco Bell"),
//                new Location("", 1, new GeoPoint(1.3900, 103.9880), "2025-04-14T12:02:00", "Cinnabon"),
//                new Location("", 2, new GeoPoint(1.2465, 103.8300), "2025-04-14T12:03:00", "Sbarro"),
//                new Location("", 1, new GeoPoint(1.3470, 103.6981), "2025-04-14T12:04:00", "A&W"),
//                new Location("", 2, new GeoPoint(1.3211, 103.9016), "2025-04-14T12:05:00", "Dunkin'."),
//                new Location("", 1, new GeoPoint(1.3732, 103.7677), "2025-04-14T12:06:00", "Nando's"),
//                new Location("", 2, new GeoPoint(1.2821, 103.8580), "2025-04-14T12:07:00", "Burger King"),
//                new Location("", 1, new GeoPoint(1.3000, 103.7870), "2025-04-14T12:08:00", "Pizza Hut"),
//                new Location("", 2, new GeoPoint(1.3521, 104.0035), "2025-04-14T12:09:00", "Starbucks")
//        );
//
//        for (Location location : locations) {
//            java.util.Map<String, Object> locationMap = new HashMap<>();
//            locationMap.put("locationType", location.getLocationType());
//            locationMap.put("geolocation", location.getGeolocation());
//            locationMap.put("createdAt", location.getCreatedAt());
//            locationMap.put("name", location.getName());
//
//            DocumentReference docRef = db.collection("locations").document(); // generate a new doc ref
//            String generatedId = docRef.getId();
//
//            locationMap.put("locationId", generatedId); // set locationId field
//
//            docRef.set(locationMap)
//                    .addOnSuccessListener(aVoid ->
//                            Log.d("Firestore", "Location added with ID: " + generatedId))
//                    .addOnFailureListener(e ->
//                            Log.e("Firestore", "Error adding location", e));
//        }
//    }

//    private void addVouchersToLocations() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("vouchers")
//                .get()
//                .addOnSuccessListener(voucherSnapshots -> {
//                    for (DocumentSnapshot voucherDoc : voucherSnapshots) {
//                        String locationId = voucherDoc.getString("locationId");
//
//                        if (locationId != null) {
//                            db.collection("locations")
//                                    .document(locationId)
//                                    .collection("vouchers")
//                                    .document(voucherDoc.getId())  // same voucherId
//                                    .set(voucherDoc.getData())
//                                    .addOnSuccessListener(aVoid ->
//                                            Log.d("Firestore", "Voucher added to location: " + locationId))
//                                    .addOnFailureListener(e ->
//                                            Log.e("Firestore", "Failed to add voucher to location: " + locationId, e));
//                        } else {
//                            Log.w("Firestore", "Voucher has no locationId: " + voucherDoc.getId());
//                        }
//                    }
//                })
//                .addOnFailureListener(e ->
//                        Log.e("Firestore", "Failed to fetch vouchers", e));
//    }

//    private void generateMultipleVouchersPerLocation() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        java.util.Map<String, String> locations = new HashMap<>();
//        locations.put("Dairy Queen", "NPUZqaHCoePhxPPeO8SQ");
//        locations.put("Taco Bell", "jIHUP0fOsp8VImNMZ97L");
//        locations.put("Cinnabon", "VyAivJk602ruVhxdAroc");
//        locations.put("Sbarro", "ReYPMldrROBkhI8NmZaU");
//        locations.put("A&W", "jduYTW0ADkdTzRlRF2wj");
//        locations.put("Dunkin'", "MBojTExozTWk78ttOAls");
//        locations.put("Nando's", "O1WXMi9HLOJk3I7fxEdM");
//        locations.put("Burger King", "5c3ZzTSQpGxPNxElVuYM");
//        locations.put("Pizza Hut", "dm3fLx25wtJfIQ9sfhrU");
//        locations.put("Starbucks", "FTou7vyO5Hbf4MSgTJNk");
//
//        WriteBatch batch = db.batch();
//        CollectionReference vouchersRef = db.collection("vouchers");
//
//        java.util.Map<String, List<String[]>> voucherTemplates = new HashMap<>();
//
//        // Sample voucher templates for variety per brand
//        voucherTemplates.put("Dairy Queen", Arrays.asList(
//                new String[]{"Choco Dipped Cone", "Free upgrade to choco dipped cone on any soft serve"},
//                new String[]{"Buy 1 Blizzard, Get 1", "Available on Fridays only"},
//                new String[]{"Dairy Queen Happy Hour", "20% off from 2pm-4pm daily"},
//                new String[]{"Free Sundae Scoop", "Claim a free sundae with any combo meal"},
//                new String[]{"Waffle Cone Bonus", "Waffle cones at half points this week!"}
//        ));
//        voucherTemplates.put("Taco Bell", Arrays.asList(
//                new String[]{"Taco Tuesday Treat", "Redeem any taco for 20 points"},
//                new String[]{"Burrito Bundle Deal", "Free drink with burrito order"},
//                new String[]{"Spicy Boost", "Get double points on spicy menu items"},
//                new String[]{"Nacho Surprise", "Free nachos with every 2 redemptions"},
//                new String[]{"Fiesta Friday", "Special reward items every Friday"}
//        ));
//        voucherTemplates.put("Cinnabon", Arrays.asList(
//                new String[]{"MiniBon Bonus", "Redeem a MiniBon for 30 points"},
//                new String[]{"Roll into Savings", "10% off classic rolls"},
//                new String[]{"Sweet Deal Alert", "Double your points on large Cinnabons"},
//                new String[]{"Extra Icing!", "Get extra icing free for any redemption"},
//                new String[]{"Cinna Happy Hour", "2-for-1 MiniBons from 3–5pm"}
//        ));
//        voucherTemplates.put("Sbarro", Arrays.asList(
//                new String[]{"Pizza Slice Special", "Free slice of cheese pizza"},
//                new String[]{"Free Drink Combo", "Add a drink to your meal for free"},
//                new String[]{"Midweek Pizza Boost", "Redeem pizza slice for only 25 points"},
//                new String[]{"Garlic Bread Bonus", "Free garlic bread with any pasta"},
//                new String[]{"Weekend Feast", "Extra points for Sbarro redemptions on weekends"}
//        ));
//        voucherTemplates.put("A&W", Arrays.asList(
//                new String[]{"Root Beer Float Fest", "Redeem a float for 20 points"},
//                new String[]{"Burger Bonus", "Buy a burger, get fries free"},
//                new String[]{"Curly Fries Special", "Discounted curly fries all week"},
//                new String[]{"Waffle Combo Deal", "Waffle + Float combo for fewer points"},
//                new String[]{"2x Points on Chicken Sandwich", "Earn double for each redemption"}
//        ));
//        voucherTemplates.put("Dunkin'", Arrays.asList(
//                new String[]{"Coffee Boost", "Get a free small coffee every 3 redemptions"},
//                new String[]{"Donut Delight", "Free classic donut with any drink"},
//                new String[]{"Mocha Madness", "Redeem mocha for 35 points"},
//                new String[]{"Morning Starter Pack", "Double points for orders before 11am"},
//                new String[]{"Sweet Pair Deal", "Buy 2 donuts, get 1 free"}
//        ));
//        voucherTemplates.put("Nando's", Arrays.asList(
//                new String[]{"Peri-Peri Perk", "Redeem free spicy wings"},
//                new String[]{"Chicken Feast", "Free ¼ chicken with any sides"},
//                new String[]{"Grill Master Bonus", "Double points on grilled platters"},
//                new String[]{"Midweek Flame Deal", "20% off Wednesday orders"},
//                new String[]{"Hot Sauce Hunt", "Collect and redeem hot sauces for rewards"}
//        ));
//        voucherTemplates.put("Burger King", Arrays.asList(
//                new String[]{"Whopper Win", "Whopper for 30 points"},
//                new String[]{"Breakfast Bonus", "Free coffee with morning sandwich"},
//                new String[]{"2-for-1 Cheeseburger", "Tuesdays only"},
//                new String[]{"King's Combo", "Get a free side on large combo"},
//                new String[]{"Weekend Crown Points", "Earn triple points Saturdays"}
//        ));
//        voucherTemplates.put("Pizza Hut", Arrays.asList(
//                new String[]{"Stuffed Crust Reward", "Redeem for only 40 points"},
//                new String[]{"Family Feast Voucher", "Extra side with large pizza"},
//                new String[]{"Cheesy Bites Treat", "Claim cheesy bites with any redemption"},
//                new String[]{"Pizza Friday Bonus", "Earn 20 bonus points"},
//                new String[]{"Free Dip Pack", "Free dipping sauces with order"}
//        ));
//        voucherTemplates.put("Starbucks", Arrays.asList(
//                new String[]{"Free Drink Toppings", "Free toppings from 2–5pm weekends"},
//                new String[]{"Iced Coffee Upgrade", "Upgrade to venti free"},
//                new String[]{"Morning Brew Deal", "Double points before 10am"},
//                new String[]{"Cake Pop Combo", "Get a cake pop for 15 points"},
//                new String[]{"2x Frappuccino Friday", "Earn double points on frappes"}
//        ));
//
//        for (java.util.Map.Entry<String, String> entry : locations.entrySet()) {
//            String brand = entry.getKey();
//            String locationId = entry.getValue();
//            List<String[]> templates = voucherTemplates.get(brand);
//
//            for (String[] template : templates) {
//                DocumentReference newVoucherRef = vouchersRef.document();
//                java.util.Map<String, Object> voucher = new HashMap<>();
//                voucher.put("voucherId", newVoucherRef.getId());
//                voucher.put("title", template[0]);
//                voucher.put("details", template[1]);
//                voucher.put("code", UUID.randomUUID().toString().substring(0, 10));
//                voucher.put("imageName", brand.toLowerCase().replaceAll("[^a-z]", ""));
//                voucher.put("locationId", locationId);
//                voucher.put("pointsReward", new Random().nextInt(16) + 30); // Random between 30–45
//                voucher.put("status", "Available");
//                voucher.put("createdAt", Timestamp.now());
//
//                batch.set(newVoucherRef, voucher);
//            }
//        }
//
//        batch.commit();
//    }



}