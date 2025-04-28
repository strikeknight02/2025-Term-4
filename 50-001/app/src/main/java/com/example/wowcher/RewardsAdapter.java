package com.example.wowcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Rewards;
import com.example.wowcher.controller.RewardsController;
import com.example.wowcher.controller.UserController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardViewHolder> {

    private final Context context;
    private List<Rewards> rewardList;
    private FirebaseFirestore db;

    private UserController userModel;
    private RewardsController rewardsModel;

    // Constructor
    public RewardsAdapter(Context context, List<Rewards> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
        db = FirebaseFirestore.getInstance();
    }

    // Set search results or filtered list
    public void setSearchList(List<Rewards> rewardSearchList) {
        this.rewardList = rewardSearchList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {

        // Get the current reward object from the list
        Rewards reward = rewardList.get(position);

        // Set the values to the TextViews
        holder.rewardName.setText(reward.getName());  // Reward Name
        holder.rewardDescription.setText(reward.getDescription());  // Reward Description
        holder.rewardPoints.setText(reward.getPointsRequired() + " Points");  // Points required for the reward

        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's current points
        fetchUserPoints(userId, (currentPoints, errorMessage) -> {
            if (errorMessage != null) {
                // Handle the error (showing a message to the user)
                holder.rewardPoints.setText("Error: " + errorMessage);
                holder.rewardCard.setAlpha(0.5f);  // Dim the card
                holder.rewardCard.setEnabled(false);  // Disable click interaction
            } else {
                // Check if the user has enough points for the reward
                if (currentPoints < reward.getPointsRequired()) {
                    holder.rewardCard.setAlpha(0.5f);  // Dim the card (not enough points)
                    holder.rewardCard.setEnabled(false);  // Disable click interaction
                } else {
                    // Check if the reward has been redeemed by the user
                    checkIfRewardRedeemed(userId, reward.getRewardId(), (isRedeemed, redemptionErrorMessage) -> {
                        if (redemptionErrorMessage != null) {
                            // Handle redemption error
                            holder.rewardPoints.setText("Error: " + redemptionErrorMessage);
                            holder.rewardCard.setAlpha(0.5f);  // Dim the card
                            holder.rewardCard.setEnabled(false);  // Disable click interaction
                        } else {
                            // Update UI based on whether the reward has been redeemed
                            if (isRedeemed) {
                                holder.rewardCard.setAlpha(0.5f);  // Dim the card (redeemed)
                                holder.rewardCard.setEnabled(false);  // Disable click interaction
                            } else {
                                holder.rewardCard.setAlpha(1f);  // Full opacity
                                holder.rewardCard.setEnabled(true);  // Enable click interaction
                            }

                            // Set click listener for each reward card (only enabled if redeemable)
                            holder.rewardCard.setOnClickListener(view -> {
                                if (holder.rewardCard.isEnabled()) {
                                    Intent intent = new Intent(context, RewardDetailActivity.class);
                                    // Pass the reward details to the next activity
                                    intent.putExtra("RewardId", reward.getRewardId());
                                    intent.putExtra("RewardName", reward.getName());
                                    intent.putExtra("RewardDescription", reward.getDescription());
                                    intent.putExtra("RewardPoints", reward.getPointsRequired());
                                    context.startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    // Method to check if a reward has been redeemed by the user
    private void checkIfRewardRedeemed(String userId, int rewardId, RedeemedCallback callback) {

        DocumentReference rewardRef = db.collection("users")
                .document(userId);

        rewardRef.get().addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> redeemedRewards = (ArrayList<String>) documentSnapshot.get("redeemedRewards");
            if (redeemedRewards != null){
                callback.onChecked(redeemedRewards.contains(Integer.toString(rewardId)), null);  // Pass null if there's no error
            } else {
                callback.onChecked(false, "Failed to load reward status"); // If error occurs, assume the reward is not redeemed
            }
        }).addOnFailureListener(e -> {
            callback.onChecked(false, "Failed to load reward status"); // If error occurs, assume the reward is not redeemed
        });
    }

    // Method to fetch the current points of the user
    private void fetchUserPoints(String userId, PointsCallback callback) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long points = documentSnapshot.getLong("currentPoints");
                if (points != null) {
                    callback.onFetched(points.intValue(), null); // Return points value
                } else {
                    callback.onFetched(0, "User points not found");
                }
            } else {
                callback.onFetched(0, "User not found");
            }
        }).addOnFailureListener(e -> {
            callback.onFetched(0, "Failed to load user points");
        });
    }

    // Callback interface to handle the result of the points fetching
    public interface PointsCallback {
        void onFetched(int currentPoints, String errorMessage);
    }

    // Callback interface to handle the result of the redeemed check
    public interface RedeemedCallback {
        void onChecked(boolean isRedeemed, String errorMessage);
    }
}

// ViewHolder class to hold the views for each item
class RewardViewHolder extends RecyclerView.ViewHolder {
    TextView rewardName, rewardDescription, rewardPoints;
    CardView rewardCard;

    public RewardViewHolder(@NonNull View itemView) {
        super(itemView);
        rewardName = itemView.findViewById(R.id.voucher_type);
        rewardDescription = itemView.findViewById(R.id.voucher_discount);
        rewardPoints = itemView.findViewById(R.id.voucher_points_required);
        rewardCard = itemView.findViewById(R.id.voucher_card);
    }
}
