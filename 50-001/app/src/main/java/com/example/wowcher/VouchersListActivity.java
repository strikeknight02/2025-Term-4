package com.example.wowcher;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wowcher.classes.Location;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Map;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class VouchersListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    List<Voucher> vouchersDataList;
    MyAdapter adapter;

    String locationId;

    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers);
        recyclerView = this.findViewById(R.id.vouchersList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        vouchersDataList = new ArrayList<>();

        makeVouchersTestData();

        adapter = new MyAdapter(this, vouchersDataList);
        recyclerView.setAdapter(adapter);

        Bundle b2 = getIntent().getExtras();
        if(b2 != null){
            locationId = b2.getString("locationId");
            System.out.println(locationId);
        }

        Button backButton = this.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

    }

    // Retrieve API key from Manifest
    public String getApiKeyFromManifest(String key) {
        String apiKey;
        try {
            ApplicationInfo applicationInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
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
            destString.append("%7C").append(item.getGeolocation());
        }

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                "?destinations=" + destString +
                "&origins=" + Map.userLocation.latitude + "," + Map.userLocation.longitude +
                "&key="+dirApiKey;


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        // Parse the response and get necessary data
                        JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
                        JsonArray routes = responseObject.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements");

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
        List<Voucher> dataListCopy = List.copyOf(vouchersDataList);
        vouchersDataList.clear();

        for (int step = 1; step < listSize; step++) {
            int currentValue = locationList.get(step).get("value").getAsInt();
            JsonObject currentObject = locationList.get(step).getAsJsonObject();

            int j = step - 1;

            // Compare value with each element on the left of it
            // until an element smaller than it is found.
            while (j >= 0 && currentValue < locationList.get(j).get("value").getAsInt()) {
                locationList.set(j+1,locationList.get(j));
                --j;
            }

            // Place location object at after the element just smaller than it.
            locationList.set(j+1,currentObject);
        }

        // Compare to voucher location ids to add vouchers in order
        for (JsonObject locationObject: locationList) {
            String locationId = locationObject.get("locationId").getAsString();
            for (int i = 0; i < dataListCopy.size(); i++) {
                String voucherLocId = dataListCopy.get(i).getLocationId();
                if (voucherLocId.equals(locationId)){
                    vouchersDataList.add(dataListCopy.get(i));
                }
            }
        }

        adapter.notifyDataSetChanged();

    }

    // todo (Maryse) - this is the call function to get vouchers based on location
    //  locationId is available above
    public void makeVouchersTestData(){
        vouchersDataList.add(new Voucher("1", "Bread", "30% off", "Available", "Uwc62HtzeDrk97Gx3fxh", ""));
        vouchersDataList.add(new Voucher("2", "Eat", "10% off", "Available", "Uwc62HtzeDrk97Gx3fxh", ""));
        vouchersDataList.add(new Voucher("3", "Rice", "20% off", "Available", "Uwc62HtzeDrk97Gx3fxh", ""));
        vouchersDataList.add(new Voucher("4", "Meat", "40% off", "Available", "Uwc62HtzeDrk97Gx3fxh", ""));
        vouchersDataList.add(new Voucher("5", "Sushi", "33% off", "Available", "Uwc62HtzeDrk97Gx3fxh", ""));

        //todo (Maryse) - handle successful repsonse
        // get location based on vouchers
        // call getRoute function and pass in the arrayList of locations
    }

}