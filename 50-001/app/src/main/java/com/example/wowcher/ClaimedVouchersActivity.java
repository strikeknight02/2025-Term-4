package com.example.wowcher;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.classes.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import java.util.ArrayList;
import java.util.List;

public class ClaimedVouchersActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;
    SearchView searchView;

    FirebaseFirestore db;

    TextView voucherTitle, voucherDetails, voucherStatus, voucherCreatedAt, voucherLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimedvouchers);

//        recyclerView = view.findViewById(R.id.recyclerView);
//
//
//        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
//        dataList = new ArrayList<>();
//
//        adapter = new MyAdapter(requireContext(), dataList);
//        recyclerView.setAdapter(adapter);
//
//        db = FirebaseFirestore.getInstance();
//        loadVouchersFromFirebase();
//
//        voucherTitle = findViewById(R.id.voucherTitle);
//        voucherDetails = findViewById(R.id.voucherDetails);
//        voucherStatus = findViewById(R.id.voucherStatus);
//        voucherCreatedAt = findViewById(R.id.voucherCreatedAt);
//        voucherLocation = findViewById(R.id.voucherLocation);
//
//        Intent intent = getIntent();
//
//        String title = intent.getStringExtra("title");
//        String details = intent.getStringExtra("details");
//        String status = intent.getStringExtra("status");
//        String createdAt = intent.getStringExtra("createdAt");
//        String locationId = intent.getStringExtra("locationId");
//
//        voucherTitle.setText(title);
//        voucherDetails.setText(details);
//        voucherStatus.setText(status);
//        voucherCreatedAt.setText(createdAt);
//        voucherLocation.setText(locationId);
    }

//    private void loadVouchersFromFirebase() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("vouchers")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        dataList.clear();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String voucherId = document.getString("voucherId");
//                            String title = document.getString("title");
//                            String details = document.getString("details");
//                            String status = document.getString("status");
//                            String createdAt = document.getString("createdAt");
//                            String locationId = document.getString("locationId");
//
//                            Voucher voucher = new Voucher(voucherId, title, details, status, locationId, createdAt);
//                            dataList.add(voucher);
//                        }
//
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
}
