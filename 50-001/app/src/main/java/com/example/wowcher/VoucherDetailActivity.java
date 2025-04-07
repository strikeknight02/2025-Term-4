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
    ImageView detailImage;
    Button backButton, redeemButton;
    String voucherTitle, voucherDetails, voucherStatus;
    int voucherId;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        backButton = findViewById(R.id.backButton);
        redeemButton = findViewById(R.id.redeemButton);
        detailVoucherId = findViewById(R.id.detailVoucherId);
        detailStatus = findViewById(R.id.detailStatus);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            voucherId = getIntent().getIntExtra("Id", -1);
            voucherTitle = bundle.getString("Title", "No title");
            voucherDetails = bundle.getString("Desc", "No description");
            voucherStatus = bundle.getString("Status", "Not Redeemed");  // Add Status if needed

            // Display data in the UI
            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);
            detailVoucherId.setText("Voucher ID: " + voucherId);
            detailStatus.setText("Status: " + voucherStatus);
        }

        backButton.setOnClickListener(v -> finish());

        if (user != null) {
            checkIfVoucherAlreadyRedeemed();
        }

        redeemButton.setOnClickListener(v -> redeemVoucher());
    }

    private void checkIfVoucherAlreadyRedeemed() {
        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(String.valueOf(voucherId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        redeemButton.setText("Owned");
                        redeemButton.setEnabled(false);
                        detailStatus.setText("Status: Redeemed");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking voucher status", Toast.LENGTH_SHORT).show());
    }

    private void redeemVoucher() {
        String userId = user.getUid();

        Map<String, Object> voucherData = new HashMap<>();
        voucherData.put("voucherId", voucherId);
        voucherData.put("title", voucherTitle);
        voucherData.put("details", voucherDetails);
        voucherData.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(String.valueOf(voucherId))
                .set(voucherData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Voucher redeemed!", Toast.LENGTH_SHORT).show();
                    redeemButton.setEnabled(false);
                    redeemButton.setText("Owned");
                    detailStatus.setText("Status: Redeemed");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to redeem voucher", Toast.LENGTH_SHORT).show());
    }
}
