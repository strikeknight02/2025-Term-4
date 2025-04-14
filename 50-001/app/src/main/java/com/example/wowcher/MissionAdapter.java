package com.example.wowcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Missions;
import com.example.wowcher.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MissionAdapter extends RecyclerView.Adapter<MissionViewHolder> {

    private final Context context;
    private List<Missions> missionList;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    // Constructor
    public MissionAdapter(Context context, List<Missions> missionList) {
        this.context = context;
        this.missionList = missionList;

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    // Set the mission list
    public void setMissionList(List<Missions> missionList) {
        this.missionList = missionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Missions mission = missionList.get(position);

        holder.missionNameTextView.setText(mission.getMissionName());
        holder.descriptionTextView.setText(mission.getDescription());
        holder.pointsRewardTextView.setText(String.valueOf(mission.getPointsReward()));

        // Check if the mission is completed
        boolean isCompleted = mission.getProgress() == 100;
        String missionId = mission.getMissionId(); // Use missionId to check redemption status

        // Check if the mission is already redeemed
        checkIfMissionRedeemed(missionId, holder, mission);

        // If the mission is completed and not redeemed, enable it for redemption
        if (isCompleted) {
            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(v -> {
                // Add click logic for completed mission
                Toast.makeText(v.getContext(), "Mission Complete! You earned " + mission.getPointsReward() + " points!", Toast.LENGTH_SHORT).show();
                redeemMission(missionId, mission.getPointsReward(), holder);
            });
        } else {
            // Dimmed out and not clickable if not completed
            holder.itemView.setAlpha(0.4f);
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);
        }
    }

    private void checkIfMissionRedeemed(String missionId, MissionViewHolder holder, Missions mission) {
        if (user == null) return;

        String userId = user.getUid();

        // Check if the mission is already redeemed by the user
        db.collection("users")
                .document(userId)
                .collection("redeemedMissions")
                .document(missionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Mission is already redeemed, disable the card
                        holder.itemView.setAlpha(0.3f);
                        holder.itemView.setClickable(false);
                        holder.itemView.setOnClickListener(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error checking redemption status", Toast.LENGTH_SHORT).show();
                });
    }

    private void redeemMission(String missionId, long missionPoints, MissionViewHolder holder) {
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Store the redeemed mission in Firestore
        Map<String, Object> redeemedData = new HashMap<>();
        redeemedData.put("redeemedAt", System.currentTimeMillis());

        // Add the redeemed mission record under the user's redeemedMissions collection
        db.collection("users")
                .document(userId)
                .collection("redeemedMissions")
                .document(missionId)
                .set(redeemedData)
                .addOnSuccessListener(aVoid -> {
                    // Update the user's points after redeeming the mission
                    updateUserPoints(missionPoints, userId, holder);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to redeem mission", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserPoints(long missionPoints, String userId, MissionViewHolder holder) {
        // Retrieve the current points of the user
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    long currentPoints = userSnapshot.contains("currentPoints") ? userSnapshot.getLong("currentPoints") : 0;
                    long totalPoints = userSnapshot.contains("totalPoints") ? userSnapshot.getLong("totalPoints") : 0;

                    // Calculate updated points
                    long updatedCurrentPoints = currentPoints + missionPoints;
                    long updatedTotalPoints = totalPoints + missionPoints;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("currentPoints", updatedCurrentPoints);
                    updates.put("totalPoints", updatedTotalPoints);

                    // Update the user's points in Firestore
                    db.collection("users")
                            .document(userId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                // Successfully updated points
                                Toast.makeText(context, "Mission redeemed! +" + missionPoints + " points", Toast.LENGTH_SHORT).show();
                                // Disable the card after redemption
                                holder.itemView.setAlpha(0.3f);
                                holder.itemView.setClickable(false);
                                holder.itemView.setOnClickListener(null);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to update points", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch user points", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }
}
class MissionViewHolder extends RecyclerView.ViewHolder {

    TextView missionNameTextView;
    TextView descriptionTextView;
    TextView pointsRewardTextView;
    TextView criteriaTextView;
    TextView progressTextView;  // New TextView for progress text
    LinearProgressIndicator missionProgress;
    CardView missionCard;

    public MissionViewHolder(@NonNull View itemView) {
        super(itemView);
        missionNameTextView = itemView.findViewById(R.id.mission_title);
        descriptionTextView = itemView.findViewById(R.id.mission_progress_text);
        pointsRewardTextView = itemView.findViewById(R.id.mission_points);
        progressTextView = itemView.findViewById(R.id.mission_progress_text);  // Initialize the progress text view
        missionProgress = itemView.findViewById(R.id.mission_progress);
        missionCard = itemView.findViewById(R.id.mission_card);
    }
}