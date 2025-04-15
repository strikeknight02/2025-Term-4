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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.wowcher.controller.LocationController;
import com.example.wowcher.controller.LocationControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.LocationSource;
import com.example.wowcher.db.VoucherSource;
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

    VoucherController voucherModel;
    LocationController locationModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers);
        recyclerView = this.findViewById(R.id.vouchersList);

        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        vouchersDataList = new ArrayList<>();

        //makeVouchersTestData();

        Bundle b2 = getIntent().getExtras();
        if(b2 != null){
            locationId = b2.getString("locationId");
            System.out.println(locationId);
            //loadVouchersForLocation(locationId);
        }

        Button backButton = this.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        //Voucher
        DBSource voucherSourceInstance = new VoucherSource(db);
        voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        //Location
        DBSource locationSourceInstance = new LocationSource(db);
        locationModel = new ViewModelProvider(this, new LocationControllerFactory(locationSourceInstance)).get(LocationController.class);
        locationModel.getModelInstance(locationModel);

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {

                if(voucherList != null){
                    if (!voucherList.isEmpty()) {
                        adapter = new MyAdapter(VouchersListActivity.this, voucherList);
                        recyclerView.setAdapter(adapter);
                        vouchersDataList = voucherList;
                    }

                    //adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VouchersListActivity.this, "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<ArrayList<Location>> locationObserver = new Observer<ArrayList<Location>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Location> locationList) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (locationList !=null)){

                    if (!locationList.isEmpty()){
                        ArrayList<String> tempArrLocId = new ArrayList<>();
                        for (Location l: locationList){
                            tempArrLocId.add(l.getLocationId());
                        }

                        voucherModel.getVouchersBasedOnLocation(tempArrLocId);
                        voucherModel.getVouchersBasedOnLocation().observe(VouchersListActivity.this, voucherObserver);

                        //getRoute(locationList);
                    } else {
                        Toast.makeText(VouchersListActivity.this, "Error loading locations!", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        };

        locationModel.getSpecificLocations("locationId", locationId);
        locationModel.getSomeLocations().observe(VouchersListActivity.this, locationObserver);
    }

}