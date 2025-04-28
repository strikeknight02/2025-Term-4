package com.example.wowcher;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClaimedVoucherActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle,voucherCodeText;
    ImageView backButton, voucherImage; // corrected imageView name

    String voucherTitle, voucherDetails, voucherStatus, voucherImageName,voucherCode;
    long voucherPoints;
    String voucherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout first
        setContentView(R.layout.activity_claimedvoucher);

        // Initialize UI components
        detailDesc = findViewById(R.id.reward_description_text);
        detailTitle = findViewById(R.id.reward_name_text);
        backButton = findViewById(R.id.backButton);
        voucherImage = findViewById(R.id.voucherImage); // Correct reference for image view
        voucherCodeText = findViewById(R.id.voucherCodeText);


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
