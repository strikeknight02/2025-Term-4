package com.example.wowcher;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Mission;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionAdapter extends RecyclerView.Adapter<MissionViewHolder> {

    private final Context context;
    private List<Mission> missionList;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    private final List<String> tierOrder = Arrays.asList("bronze", "silver", "gold", "platinum");

    public MissionAdapter(Context context, List<Mission> missionList) {
        this.context = context;
        this.missionList = missionList;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void setMissionList(List<Mission> missionList) {
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
        Mission mission = missionList.get(position);

        holder.missionNameTextView.setText(mission.getMissionName());
        holder.descriptionTextView.setText(mission.getDescription());
        holder.pointsRewardTextView.setText(String.valueOf(mission.getPointsReward()));

        if (user == null) return;

        String userId = user.getUid();
        String missionId = mission.getMissionId();

        // First check: if mission is already redeemed, disable it and return early
        db.collection("users")
                .document(userId)
                .collection("redeemedMissions")
                .document(missionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
//                        holder.itemView.setAlpha(0.3f);
                        holder.itemView.setClickable(false);
                        holder.completeIndicator.setBackgroundColor(Color.GREEN);
                        holder.completeIndicator.setText("Completed");
                        holder.itemView.setOnClickListener(null);
                        return; // already redeemed
                    } else {
                        handleMissionTypeLogic(holder, mission, missionId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error checking mission status", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleMissionTypeLogic(MissionViewHolder holder, Mission mission, String missionId) {
        String missionType = mission.getType();

        switch (missionType) {
            case "collect_voucher":
                handleCollectVoucherMissionLogic(holder, mission, missionId);
                break;

            case "location_voucher":
                handleLocationVoucherMissionLogic(holder, mission, missionId);
                break;

            case "tier":
                handleTierMissionLogic(holder, mission, missionId);
                break;

            default:
                handleDefaultMission(holder, mission, false, missionId);
        }
    }

    private void handleCollectVoucherMissionLogic(MissionViewHolder holder, Mission mission, String missionId) {
        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    long redeemedCount = querySnapshot.size();
                    boolean isCompleted = redeemedCount >= mission.getCriteria();
                    handleCollectVoucherMission(holder, mission, isCompleted, missionId);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error fetching redeemed vouchers", Toast.LENGTH_SHORT).show());
    }

    private void handleLocationVoucherMissionLogic(MissionViewHolder holder, Mission mission, String missionId) {
        String userId = user.getUid();
        String missionLocationId = mission.getLocationId();

        db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .get()
                .addOnSuccessListener(snapshot -> {
                    long matchingCount = 0;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String locId = doc.getString("locationId");
                        if (locId != null && locId.equals(missionLocationId)) {
                            matchingCount++;
                        }
                    }
                    boolean isCompleted = matchingCount >= mission.getCriteria();
                    handleLocationVoucherMission(holder, mission, isCompleted, missionId);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error fetching location vouchers", Toast.LENGTH_SHORT).show());
    }


    private void handleTierMissionLogic(MissionViewHolder holder, Mission mission, String missionId) {
        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    String userTier = doc.getString("tier");
                    String requiredTier = mission.getRequiredTier();

                    if (userTier != null && requiredTier != null) {
                        int userTierIndex = tierOrder.indexOf(userTier.toLowerCase());
                        int requiredTierIndex = tierOrder.indexOf(requiredTier.toLowerCase());

                        boolean isCompleted = userTierIndex >= requiredTierIndex;
                        handleTierMission(holder, mission, isCompleted, missionId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error fetching user tier", Toast.LENGTH_SHORT).show());
    }

    // ====== MISSION HANDLERS ======

    private void handleCollectVoucherMission(MissionViewHolder holder, Mission mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Voucher Collected! You earned ");
    }

    private void handleLocationVoucherMission(MissionViewHolder holder, Mission mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Location Voucher Completed! You earned ");
    }

    private void handleTierMission(MissionViewHolder holder, Mission mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Tier Mission Completed! You earned ");
    }

    private void handleDefaultMission(MissionViewHolder holder, Mission mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Mission Completed! You earned ");
    }

    private void handleGenericMission(MissionViewHolder holder, Mission mission, boolean isCompleted, String missionId, String toastMsg) {
        if (isCompleted) {
//            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
            holder.completeIndicator.setBackgroundColor(Color.GREEN);
            holder.completeIndicator.setText("Completed");
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), toastMsg + mission.getPointsReward() + " points!", Toast.LENGTH_SHORT).show();
                redeemMission(missionId, mission.getPointsReward(), holder);
            });
        } else {
//            holder.itemView.setAlpha(0.4f);
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);
        }
    }

    private void redeemMission(String missionId, long basePoints, MissionViewHolder holder) {
        String userId = user.getUid();

        Map<String, Object> redeemedData = new HashMap<>();
        redeemedData.put("redeemedAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("redeemedMissions")
                .document(missionId)
                .set(redeemedData)
                .addOnSuccessListener(unused -> updateUserPoints(basePoints, userId, holder))
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to redeem mission", Toast.LENGTH_SHORT).show());
    }

    private void updateUserPoints(long basePoints, String userId, MissionViewHolder holder) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    long currentPoints = doc.contains("currentPoints") ? doc.getLong("currentPoints") : 0;
                    long totalPoints = doc.contains("totalPoints") ? doc.getLong("totalPoints") : 0;
                    String currentTier = doc.contains("tier") ? doc.getString("tier") : "bronze";

                    double multiplier = getMultiplierForTier(currentTier);
                    long awardedPoints = Math.round(basePoints * multiplier);

                    long updatedCurrent = currentPoints + awardedPoints;
                    long updatedTotal = totalPoints + awardedPoints;

                    String newTier = getTierForPoints(updatedTotal);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("currentPoints", updatedCurrent);
                    updates.put("totalPoints", updatedTotal);
                    if (!newTier.equalsIgnoreCase(currentTier)) {
                        updates.put("tier", newTier);
                    }

                    db.collection("users")
                            .document(userId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Mission redeemed! +" + awardedPoints + " points (x" + multiplier + ")", Toast.LENGTH_SHORT).show();
                                if (!newTier.equalsIgnoreCase(currentTier)) {
                                    Toast.makeText(context, "You've been promoted to " + newTier.toUpperCase() + " tier!", Toast.LENGTH_LONG).show();
                                }
//                                holder.itemView.setAlpha(0.3f);
                                holder.itemView.setClickable(false);
                                holder.completeIndicator.setBackgroundColor(Color.GREEN);
                                holder.completeIndicator.setText("Completed");
                                holder.itemView.setOnClickListener(null);
                            });
                });
    }

    private double getMultiplierForTier(String tier) {
        switch (tier.toLowerCase()) {
            case "silver": return 1.2;
            case "gold": return 1.5;
            case "platinum": return 2.0;
            default: return 1.0;
        }
    }

    private String getTierForPoints(long totalPoints) {
        if (totalPoints >= 1500) return "platinum";
        else if (totalPoints >= 1000) return "gold";
        else if (totalPoints >= 500) return "silver";
        else return "bronze";
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
    TextView completeIndicator;

    public MissionViewHolder(@NonNull View itemView) {
        super(itemView);
        missionNameTextView = itemView.findViewById(R.id.mission_title);
        descriptionTextView = itemView.findViewById(R.id.mission_description);
        pointsRewardTextView = itemView.findViewById(R.id.mission_points);
        progressTextView = itemView.findViewById(R.id.mission_progress_text);  // Initialize the progress text view
        missionProgress = itemView.findViewById(R.id.mission_progress);
        missionCard = itemView.findViewById(R.id.mission_card);
        completeIndicator = itemView.findViewById(R.id.complete_indicator);
    }
}