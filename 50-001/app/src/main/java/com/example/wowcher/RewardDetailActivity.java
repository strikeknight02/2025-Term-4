package com.example.wowcher;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RewardDetailActivity extends AppCompatActivity {

    // Declare the TextViews to show the reward details
    TextView rewardNameText, rewardDescriptionText, rewardPointsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);

        // Initialize the TextViews
        rewardNameText = findViewById(R.id.reward_name_text);
        rewardDescriptionText = findViewById(R.id.reward_description_text);
        rewardPointsText = findViewById(R.id.reward_points_text);

        // Get the data from the intent
        String rewardName = getIntent().getStringExtra("RewardName");
        String rewardDescription = getIntent().getStringExtra("RewardDescription");
        int rewardPoints = getIntent().getIntExtra("RewardPoints", 0);


        // Set the data to the TextViews
        rewardNameText.setText(rewardName);
        rewardDescriptionText.setText(rewardDescription);
        rewardPointsText.setText("Points Required: " + rewardPoints);
    }
}
