package com.example.wowcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Rewards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClaimedRewardsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Rewards> dataList;
    ClaimedRewardAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimedrewards);

        recyclerView = findViewById(R.id.recyclerView); // Get reference to RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Use 1 column grid layout for simplicity

        dataList = new ArrayList<>();
        adapter = new ClaimedRewardAdapter(this, dataList); // Initialize your adapter
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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
                .collection("redeemedRewards")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear(); // Clear previous data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String description = document.getString("description");
                            String title = document.getString("name");
                            int points = document.getLong("points").intValue();
                            int timestamp = document.getLong("timestamp").intValue();

                            Rewards reward = new Rewards(0,title,description,points,String.valueOf(timestamp),true);
                            dataList.add(reward); // Add the voucher to the list
                        }

                        adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
                    } else {
                        Toast.makeText(this, "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
