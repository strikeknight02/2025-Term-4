package com.example.wowcher;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wowcher.classes.Voucher;

public class VoucherDetailActivity extends AppCompatActivity {
    TextView detailDesc, detailTitle;
    ImageView detailImage;
    Button backButton, redeemButton;
    String voucherTitle, voucherDetails;
    int voucherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        backButton = findViewById(R.id.backButton);
        redeemButton = findViewById(R.id.redeemButton);

        // Retrieve data passed from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            voucherTitle = bundle.getString("Title", "No title available");
            voucherDetails = bundle.getString("Desc", "No description available");
            voucherImage = bundle.getInt("Image", R.drawable.baseline_discount_24);

            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);
            detailImage.setImageResource(voucherImage);
        }

        // Back Button to return to the previous screen
        backButton.setOnClickListener(v -> finish());

        // Redeem Button to save the voucher
        redeemButton.setOnClickListener(v -> redeemVoucher());
    }

    private void redeemVoucher() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Append voucher to saved list
        String existingVouchers = sharedPreferences.getString("redeemedVouchers", "");
        String updatedVouchers = existingVouchers + voucherTitle + ";"; // Using ';' as a delimiter

        editor.putString("redeemedVouchers", updatedVouchers);
        editor.apply();

        Toast.makeText(this, "Voucher redeemed successfully!", Toast.LENGTH_SHORT).show();
    }
}
