package com.example.wowcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Voucher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClaimedVouchersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Voucher> dataList;
    ClaimedVoucherAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimedvouchers);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView); // Get reference to RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Use 1 column grid layout for simplicity

        dataList = new ArrayList<>();
        loadVouchersFromFirebase(); // Load vouchers when the activity starts

        // Bind the back icon and set the click listener
        ImageView backIcon = findViewById(R.id.wowcher_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });
    }

    private void loadVouchersFromFirebase() {
        // Get the current user's UID from FirebaseAuth
        String userId = auth.getCurrentUser().getUid();  // Dynamically fetch the UID

        // Get redeemed vouchers for the specific user from the `redeemedVouchers` subcollection
        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear(); // Clear previous data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String voucherId = document.getString("voucherId");
                            String title = document.getString("title");
                            String details = document.getString("details");
                            String status = document.getString("status");
                            String createdAt = document.getString("createdAt");
                            String locationId = document.getString("locationId");
                            Long pointsReward = document.getLong("points");
                            String code = document.getString("code");
                            String imageName = document.getString("image");

                            Voucher voucher = new Voucher(voucherId, title, details, status, locationId, createdAt,pointsReward,code,imageName);
                            dataList.add(voucher); // Add the voucher to the list
                        }

                        adapter = new ClaimedVoucherAdapter(this, dataList); // Initialize your adapter
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(this, "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
