package com.example.wowcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ClaimedVoucherActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle,voucherCodeText, detailVoucherId, detailStatus;
    ImageView detailImage, backButton, voucherImage; // corrected imageView name
    Button redeemButton;
    String voucherTitle, voucherDetails, voucherStatus, voucherImageName,voucherCode;
    long voucherPoints;
    String voucherId;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout first
        setContentView(R.layout.activity_claimedvoucher);

        // Initialize UI components
        detailDesc = findViewById(R.id.reward_description_text);
        detailTitle = findViewById(R.id.reward_name_text);
        detailImage = findViewById(R.id.detailImage);
        backButton = findViewById(R.id.backButton);
        voucherImage = findViewById(R.id.voucherImage); // Correct reference for image view
        voucherCodeText = findViewById(R.id.voucherCodeText);

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
            voucherCode = bundle.getString("Code", "");
            voucherImageName = bundle.getString("Image", "");  // assuming this is the image name

            int imageResId = getResources().getIdentifier(voucherImageName, "drawable", getPackageName());

            // Set the voucher details in the UI
            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);
            voucherCodeText.setText(voucherCode);

            // Set the image dynamically
            if (imageResId != 0) {
                voucherImage.setImageResource(imageResId);
            } else {
                voucherImage.setImageResource(R.drawable.lebron_james); // fallback image
            }
        }

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }
}
