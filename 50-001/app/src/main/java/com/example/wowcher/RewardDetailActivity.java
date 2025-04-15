package com.example.wowcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RewardDetailActivity extends AppCompatActivity {

    TextView rewardNameText, rewardDescriptionText, rewardPointsText;
    Button redeemButton;

    FirebaseFirestore db;
    FirebaseUser user;

    String rewardName, rewardDescription;
    int rewardPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);

        // Initialize the TextViews
        rewardNameText = findViewById(R.id.reward_name_text);
        rewardDescriptionText = findViewById(R.id.reward_description_text);
        rewardPointsText = findViewById(R.id.reward_points_text);
        redeemButton = findViewById(R.id.redeemButton);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        rewardName = getIntent().getStringExtra("RewardName");
        rewardDescription = getIntent().getStringExtra("RewardDescription");
        rewardPoints = getIntent().getIntExtra("RewardPoints", 0);

        rewardNameText.setText(rewardName);
        rewardDescriptionText.setText(rewardDescription);
        rewardPointsText.setText("Points Required: " + rewardPoints);

        redeemButton.setOnClickListener(v -> attemptRedemption());
    }

    private void attemptRedemption() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                long currentPoints = snapshot.getLong("currentPoints") != null ? snapshot.getLong("currentPoints") : 0;

                if (currentPoints >= rewardPoints) {
                    // Enough points: proceed with redemption
                    long newPoints = currentPoints - rewardPoints;

                    // Update user's points
                    userRef.update("currentPoints", newPoints)
                            .addOnSuccessListener(unused -> {
                                // Add reward to redeemedRewards collection
                                Map<String, Object> rewardData = new HashMap<>();
                                rewardData.put("name", rewardName);
                                rewardData.put("description", rewardDescription);
                                rewardData.put("points", rewardPoints);
                                rewardData.put("timestamp", System.currentTimeMillis());

                                db.collection("users")
                                        .document(userId)
                                        .collection("redeemedRewards")
                                        .add(rewardData)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this, "Reward redeemed successfully!", Toast.LENGTH_SHORT).show();
                                            // Go back to home
                                            Intent intent = new Intent(this, MainActivity.class); // Change if your home activity is named differently
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save reward", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update points", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "Not enough points to redeem this reward", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show());
    }
}
