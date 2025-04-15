package com.example.wowcher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClaimedRewardActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle,voucherCodeText, detailVoucherId, detailStatus;
    ImageView detailImage, backButton, voucherImage; // corrected imageView name
    Button redeemButton;
    String voucherTitle, voucherDetails, voucherDesc, voucherImageName,voucherCode;
    long voucherPoints;
    String voucherId;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout first
        setContentView(R.layout.activity_claimedreward);

        // Initialize UI components
        detailDesc = findViewById(R.id.reward_description_text);
        detailTitle = findViewById(R.id.reward_name_text);
        backButton = findViewById(R.id.backButton);
        voucherCodeText = findViewById(R.id.voucherCodeText);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Fetch data from the Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            voucherDesc = bundle.getString("description", "-1");
            voucherTitle = bundle.getString("name", "No title");
            voucherPoints = bundle.getLong("points", 0);
            voucherCode = bundle.getString("timestamp", "");

            // Set the voucher details in the UI
            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);
            voucherCodeText.setText(String.valueOf(voucherCode));

        }

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }

}
