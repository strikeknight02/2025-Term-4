package com.example.wowcher;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Missions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionAdapter extends RecyclerView.Adapter<MissionViewHolder> {

    private final Context context;
    private List<Missions> missionList;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    private final List<String> tierOrder = Arrays.asList("bronze", "silver", "gold", "platinum");

    public MissionAdapter(Context context, List<Missions> missionList) {
        this.context = context;
        this.missionList = missionList;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

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

        if (user == null) return;

        String userId = user.getUid();
        String missionId = mission.getMissionId();

        db.collection("users")
                .document(userId)
                .collection("redeemedMissions")
                .document(missionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Already redeemed
                        setMissionCompletedUI(holder);
                        return;
                    } else {
                        handleMissionTypeLogic(holder, mission, missionId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("IndivMission", "Error checking mission status", e);
                    Toast.makeText(context, "Error checking mission status", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleMissionTypeLogic(MissionViewHolder holder, Missions mission, String missionId) {
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

    private void handleCollectVoucherMissionLogic(MissionViewHolder holder, Missions mission, String missionId) {
        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    List<String> redeemedVouchers = (List<String>) userDoc.get("redeemedVouchers");
                    int redeemedCount = (redeemedVouchers != null) ? redeemedVouchers.size() : 0;

                    boolean isCompleted = redeemedCount >= mission.getCriteria();
                    handleCollectVoucherMission(holder, mission, isCompleted, missionId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error fetching redeemed vouchers", Toast.LENGTH_SHORT).show()
                );
    }

    private void handleLocationVoucherMissionLogic(MissionViewHolder holder, Missions mission, String missionId) {
        String userId = user.getUid();
        String missionLocationId = mission.getLocationId();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    List<String> redeemedVoucherIds = (List<String>) userDoc.get("redeemedVouchers");
                    if (redeemedVoucherIds == null || redeemedVoucherIds.isEmpty()) {
                        handleLocationVoucherMission(holder, mission, false, missionId);
                        return;
                    }

                    // Fetch each redeemed voucher and check if it matches locationId
                    final int[] matchingCount = {0};
                    final int total = redeemedVoucherIds.size();
                    final int[] processed = {0};

                    for (String voucherId : redeemedVoucherIds) {
                        db.collection("vouchers")
                                .document(voucherId)
                                .get()
                                .addOnSuccessListener(voucherDoc -> {
                                    String locId = voucherDoc.getString("locationId");
                                    if (locId != null && locId.equals(missionLocationId)) {
                                        matchingCount[0]++;
                                    }
                                    processed[0]++;

                                    if (processed[0] == total) {
                                        boolean isCompleted = matchingCount[0] >= mission.getCriteria();
                                        handleLocationVoucherMission(holder, mission, isCompleted, missionId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    processed[0]++;
                                    if (processed[0] == total) {
                                        boolean isCompleted = matchingCount[0] >= mission.getCriteria();
                                        handleLocationVoucherMission(holder, mission, isCompleted, missionId);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error fetching redeemed vouchers", Toast.LENGTH_SHORT).show()
                );
    }

    private void handleTierMissionLogic(MissionViewHolder holder, Missions mission, String missionId) {
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
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error fetching user tier", Toast.LENGTH_SHORT).show()
                );
    }

    private void handleCollectVoucherMission(MissionViewHolder holder, Missions mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Voucher Collected! You earned ");
    }

    private void handleLocationVoucherMission(MissionViewHolder holder, Missions mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Location Voucher Completed! You earned ");
    }

    private void handleTierMission(MissionViewHolder holder, Missions mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Tier Mission Completed! You earned ");
    }

    private void handleDefaultMission(MissionViewHolder holder, Missions mission, boolean isCompleted, String missionId) {
        handleGenericMission(holder, mission, isCompleted, missionId, "Mission Completed! You earned ");
    }

    private void handleGenericMission(MissionViewHolder holder, Missions mission, boolean isCompleted, String missionId, String toastMsg) {
        if (isCompleted) {
            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
            holder.completeIndicator.setBackgroundColor(Color.GREEN);
            holder.completeIndicator.setText("Completed");

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), toastMsg + mission.getPointsReward() + " points!", Toast.LENGTH_SHORT).show();
                redeemMission(missionId, mission.getPointsReward(), holder);
            });
        } else {
            resetMissionUI(holder);  // 👈 IMPORTANT: reset the recycled view state
        }
    }

    private void resetMissionUI(MissionViewHolder holder) {
        holder.itemView.setAlpha(1f);
        holder.itemView.setClickable(false);
        holder.itemView.setOnClickListener(null);
        holder.completeIndicator.setBackgroundColor(Color.GRAY);
        holder.completeIndicator.setText("Incomplete");
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
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to redeem mission", Toast.LENGTH_SHORT).show()
                );
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
                                setMissionCompletedUI(holder);
                            });
                });
    }

    private void setMissionCompletedUI(MissionViewHolder holder) {
        holder.itemView.setAlpha(0.3f);
        holder.itemView.setClickable(false);
        holder.completeIndicator.setBackgroundColor(Color.GREEN);
        holder.completeIndicator.setText("Completed");
        holder.itemView.setOnClickListener(null);
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
        if (totalPoints >= 1500) return "Platinum";
        else if (totalPoints >= 1000) return "Gold";
        else if (totalPoints >= 500) return "Silver";
        else return "Bronze";
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
    CardView missionCard;
    TextView completeIndicator;

    public MissionViewHolder(@NonNull View itemView) {
        super(itemView);
        missionNameTextView = itemView.findViewById(R.id.mission_title);
        descriptionTextView = itemView.findViewById(R.id.mission_description);
        pointsRewardTextView = itemView.findViewById(R.id.mission_points);
        missionCard = itemView.findViewById(R.id.mission_card);
        completeIndicator = itemView.findViewById(R.id.complete_indicator);
    }
}
