package com.example.wowcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.example.wowcher.db.VoucherSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoucherDetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailVoucherId, detailStatus;
    ImageView detailImage, backButton, voucherImage; // corrected imageView name
    Button redeemButton;
    String voucherTitle, voucherDetails, voucherStatus, voucherImageName,voucherCode,voucherLocationID;
    long voucherPoints;
    String voucherId;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    UserController userModel;
    VoucherController voucherModel;

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
        redeemButton.setEnabled(true); //by default
        voucherImage = findViewById(R.id.voucherImage); // Correct reference for image view

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
            voucherLocationID = bundle.getString("Location", "");

            int imageResId = getResources().getIdentifier(voucherImageName, "drawable", getPackageName());

            // Set the voucher details in the UI
            detailTitle.setText(voucherTitle);
            detailDesc.setText(voucherDetails);

            // Set the image dynamically
            if (imageResId != 0) {
                voucherImage.setImageResource(imageResId);
            } else {
                voucherImage.setImageResource(R.drawable.lebron_james); // fallback image
            }
        }

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        redeemButton.setOnClickListener(v -> redeemVoucher(voucherId));

        String userId = user.getUid();

        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        userModel.getUserInfoFromSource("userId", userId);

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (user !=null)){
                    ArrayList<String> redeemedVouchers = user.getRedeemedVouchers();
                    for (String v : redeemedVouchers){
                        if (v.equals(voucherId)){
                            redeemButton.setText("Owned");
                            redeemButton.setEnabled(false);
                            //detailStatus.setText("Status: Redeemed");
                        }
                    }
                }else{
                    Toast.makeText(VoucherDetailActivity.this, "Error checking voucher status", Toast.LENGTH_SHORT).show();
                }

            }
        };

        userModel.getUserInfo().observe(this, userObserver);

    }

    private void redeemVoucher(String voucherId) {
        String userId = user.getUid();

        //Voucher
        DBSource userSourceInstance = new VoucherSource(db);
        voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(userSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        voucherModel.getVoucherBySomething("voucherId", voucherId);

        final Observer<Voucher> voucherObserver = new Observer<Voucher>() {
            @Override
            public void onChanged(@Nullable final Voucher voucher) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (voucher != null)) {

                    userModel.updateUser(userId, "redeemedVouchers", FieldValue.arrayUnion(voucherId));
                    Toast.makeText(VoucherDetailActivity.this, "Voucher redeemed!", Toast.LENGTH_SHORT).show();
                    redeemButton.setEnabled(false);
                    redeemButton.setText("Owned");
                    userModel.updateUser(userId, "currentPoints", FieldValue.increment((long) voucher.getPointsReward()));
                    userModel.updateUser(userId, "totalPoints", FieldValue.increment((long) voucher.getPointsReward()));

                    // ✅ Redirect to ClaimedVouchersActivity
                    Intent intent = new Intent(VoucherDetailActivity.this, ClaimedVouchersActivity.class);
                    startActivity(intent);
                    finish(); // Optional: finish VoucherDetailActivity so it’s removed from the back stack
                } else {
                    Toast.makeText(VoucherDetailActivity.this, "Failed to redeem voucher", Toast.LENGTH_SHORT).show();
                }
            }
        };

        voucherModel.getRedeemedVouchers().observe(this, voucherObserver);
    }


    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            code.append(chars.charAt(randomIndex));
        }
        return code.toString();
    }
}
