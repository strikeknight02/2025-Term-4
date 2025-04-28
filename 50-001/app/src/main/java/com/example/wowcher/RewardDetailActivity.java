package com.example.wowcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.controller.RewardsController;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class RewardDetailActivity extends AppCompatActivity {

    TextView rewardNameText, rewardDescriptionText, rewardPointsText;
    Button redeemButton;

    FirebaseFirestore db;
    FirebaseUser user;
    UserController userModel;
    RewardsController rewardsModel;

    String rewardName, rewardDescription;
    int rewardPoints, rewardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);

        // Initialize the TextViews
        rewardNameText = findViewById(R.id.reward_name_text);
        rewardDescriptionText = findViewById(R.id.reward_description_text);
        rewardPointsText = findViewById(R.id.reward_points_text);
        redeemButton = findViewById(R.id.redeemButton);

        rewardId = getIntent().getIntExtra("RewardId", 0);
        rewardName = getIntent().getStringExtra("RewardName");
        rewardDescription = getIntent().getStringExtra("RewardDescription");
        rewardPoints = getIntent().getIntExtra("RewardPoints", 0);

        rewardNameText.setText(rewardName);
        rewardDescriptionText.setText(rewardDescription);
        rewardPointsText.setText("Points Required: " + rewardPoints);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        redeemButton.setOnClickListener(v -> attemptRedemption());
    }

    private void attemptRedemption() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        userModel.updateUser(userId, "currentPoints", FieldValue.increment(-rewardPoints));
        userModel.updateUser(userId, "redeemedRewards", FieldValue.arrayUnion(Integer.toString(rewardId)));

        // Go back to home
        Intent intent = new Intent(this, ClaimedRewardsActivity.class); // Change if your home activity is named differently
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
