package com.example.wowcher.fragments;

import android.os.Build;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.wowcher.classes.Location;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {


    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;
    SearchView searchView;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    UserController userModel;
    VoucherController voucherModel;
    LocationController locationModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vouchers, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

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
                if (locationList != null){
                    getRoute(locationList);
                }
            }
        };

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {

                if(voucherList != null){
                    if (!voucherList.isEmpty()) {
                        adapter = new MyAdapter(requireContext(), voucherList);
                        recyclerView.setAdapter(adapter);

                        dataList = voucherList;
                    } else {
                        Toast.makeText(requireContext(), "No Voucher to Load", Toast.LENGTH_SHORT).show();
                    }

                    ArrayList<String> voucherIdList = new ArrayList<>();
                    for(Voucher v : voucherList){
                        if (!voucherIdList.contains(v.getLocationId())){
                            voucherIdList.add(v.getLocationId());
                        }
                    }

                    locationModel.getLocationsBasedOnVoucher(voucherIdList);
                    locationModel.locationBasedVouchers().observe(getViewLifecycleOwner(), locationObserver);

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if (user != null){
                    ArrayList<String> redeemedVouchers = user.getRedeemedVouchers();

                    voucherModel.getVouchersforAll(redeemedVouchers);
                    voucherModel.getAllVouchers().observe(getViewLifecycleOwner(), voucherObserver);
                }

            }
        };

        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);

        return view;
    }

    private void searchList(String text) {
        List<Voucher> dataSearchList = new ArrayList<>();
        for (Voucher data : dataList) {
            if (data.getTitle().toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }

    // Retrieve API key from Manifest
    public String getApiKeyFromManifest(String key) {
        String apiKey;
        try {
            ApplicationInfo applicationInfo = requireContext().getPackageManager().getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("API key retrieval failed", "Error: " + e);
            return null;
        }
        return apiKey;
    }

    // Send URL request to retrieve routes for locations
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getRoute(ArrayList<Location> destinations) {
        // Get Directions API key
        String dirApiKey = getApiKeyFromManifest("dirApiKey");

        // Format string for URL request
        StringBuilder destString = new StringBuilder();
        for (Location item: destinations) {
            GeoPoint geoObject = item.getGeolocation();
            String coordinates = geoObject.getLatitude() + "," + geoObject.getLongitude();
            destString.append("%7C").append(coordinates);
        }

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                "?destinations=" + destString +
                "&origins=" + Map.userLocation.latitude + "," + Map.userLocation.longitude +
                "&key="+dirApiKey;


        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        // Parse the response and get necessary data
                        JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
                        JsonArray routes = responseObject.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements");

                        System.out.println(response);
                        // Prepare location details to sort vouchers by distance
                        ArrayList<JsonObject> locationList = new ArrayList<>();
                        for (int i = 0; i < routes.size(); i++) {
                            int value = routes.get(i).getAsJsonObject().getAsJsonObject("distance").get("value").getAsInt();
                            JsonObject locationDetails = JsonParser.parseString("{" +
                                    "locationId:"+destinations.get(i).getLocationId()+","+
                                    "value:" + value +
                                    "}").getAsJsonObject();
                            locationList.add(locationDetails);
                        }

                        insertionSortVouchers(locationList);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                System.out.println("Fail to retrieve routes");
            }
        });

        queue.add(stringRequest);
    }

    // Insertion sort for vouchers based on distance
    public void insertionSortVouchers(ArrayList<JsonObject> locationList) {
        int listSize = locationList.size();
        List<Voucher> dataListCopy = List.copyOf(dataList);
        dataList.clear();

        for (int step = 1; step < listSize; step++) {
            int currentValue = locationList.get(step).get("value").getAsInt();
            JsonObject currentObject = locationList.get(step).getAsJsonObject();

            int j = step - 1;

            // Compare key with each element on the left of it until an element smaller than it is found.
            while (j >= 0 && currentValue < locationList.get(j).get("value").getAsInt()) {
                locationList.set(j+1,locationList.get(j));
                --j;
            }

            // Place key at after the element just smaller than it.
            locationList.set(j+1,currentObject);
        }

        // Compare to voucher location ids to add vouchers in order
        for (JsonObject locationObject: locationList) {
            String locationId = locationObject.get("locationId").getAsString();
            for (int i = 0; i < dataListCopy.size(); i++) {
                String voucherLocId = dataListCopy.get(i).getLocationId();
                if (voucherLocId.equals(locationId)){
                    dataList.add(dataListCopy.get(i));
                }
            }
        }

        adapter.notifyDataSetChanged();

    }

}