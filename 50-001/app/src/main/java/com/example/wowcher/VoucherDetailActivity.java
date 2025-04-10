package com.example.wowcher;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class VoucherDetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailVoucherId, detailStatus;
    ImageView detailImage, backButton;
    Button redeemButton;
    String voucherTitle, voucherDetails, voucherStatus;
    long voucherPoints;
    String voucherId;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout first
        setContentView(R.layout.activity_detail);

        // Initialize UI components
        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        backButton = findViewById(R.id.backButton);
        redeemButton = findViewById(R.id.redeemButton);
        redeemButton.setEnabled(false); // Disable by default

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Fetch data from the Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            voucherId = bundle.getString("Id", "-1");
            voucherTitle = bundle.getString("Title", "No title");
            voucherDetails = bundle.getString("Desc", "No description");
            voucherStatus = bundle.getString("Status", "Not Redeemed");
            voucherPoints = bundle.getLong("Points", 10);

            // Set the voucher details in the UI
            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);
        }

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Check if the voucher is already redeemed before showing the rest of the UI
        if (user != null) {
            checkIfVoucherAlreadyRedeemed(); // Make sure to check the redemption status early
        }

        // Redeem button functionality
        redeemButton.setOnClickListener(v -> redeemVoucher());
    }

    private void checkIfVoucherAlreadyRedeemed() {
        String userId = user.getUid();

        // Display a loading state if needed (optional, can use a ProgressDialog or spinner)
        // For now, assuming redeemButton is disabled till the check is done

        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(voucherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Voucher already redeemed
                        redeemButton.setText("Owned");
                        redeemButton.setEnabled(false);
                        detailStatus.setText("Status: Redeemed");
                    } else {
                        // Voucher not redeemed yet
                        redeemButton.setEnabled(true); // Enable the button for redemption
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Error checking voucher status", Toast.LENGTH_SHORT).show();
                });
    }

    private void redeemVoucher() {
        String userId = user.getUid();

        // Prepare data to save when voucher is redeemed
        Map<String, Object> voucherData = new HashMap<>();
        voucherData.put("voucherId", voucherId);
        voucherData.put("title", voucherTitle);
        voucherData.put("details", voucherDetails);
        voucherData.put("points", voucherPoints);
        voucherData.put("timestamp", System.currentTimeMillis());

        // Save the redemption record in Firestore
        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(voucherId)
                .set(voucherData)
                .addOnSuccessListener(docRef -> {
                    // Update user's points after redeeming the voucher
                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                long currentPoints = userSnapshot.contains("currentPoints") ? userSnapshot.getLong("currentPoints") : 0;
                                long totalPoints = userSnapshot.contains("totalPoints") ? userSnapshot.getLong("totalPoints") : 0;

                                long updatedCurrentPoints = currentPoints + voucherPoints;
                                long updatedTotalPoints = totalPoints + voucherPoints;

                                // Update the user's points in Firestore
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("currentPoints", updatedCurrentPoints);
                                updates.put("totalPoints", updatedTotalPoints);

                                db.collection("users")
                                        .document(userId)
                                        .update(updates)
                                        .addOnSuccessListener(unused -> {
                                            // Successfully updated points
                                            Toast.makeText(this, "Voucher redeemed! +" + voucherPoints + " points", Toast.LENGTH_SHORT).show();
                                            redeemButton.setEnabled(false);
                                            redeemButton.setText("Owned");
                                            detailStatus.setText("Status: Redeemed");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any failure in updating points
                                            Toast.makeText(this, "Failed to update points", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                // Handle any failure in fetching user points
                                Toast.makeText(this, "Failed to fetch user points", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle any failure in redeeming voucher
                    Toast.makeText(this, "Failed to redeem voucher", Toast.LENGTH_SHORT).show();
                });
    }
}
