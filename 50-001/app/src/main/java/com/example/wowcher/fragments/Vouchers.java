package com.example.wowcher.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.RewardsAdapter;
import com.example.wowcher.MissionAdapter;
import com.example.wowcher.classes.Missions;
import com.example.wowcher.classes.Rewards;
import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.MissionController;
import com.example.wowcher.controller.MissionControllerFactory;
import com.example.wowcher.controller.RewardsController;
import com.example.wowcher.controller.RewardsControllerFactory;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.MissionSource;
import com.example.wowcher.db.RewardsSource;
import com.example.wowcher.db.UserSource;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vouchers extends Fragment {

    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;

    FirebaseFirestore db;
    UserController userModel;
    RewardsController rewardsModel;
    MissionController missionsModel;

    TextView tierNameText, pointsNameText, voucherNameText;

    RecyclerView rewardsRecyclerView;
    RewardsAdapter rewardAdapter;
    List<Rewards> rewardList = new ArrayList<>();

    RecyclerView missionsRecyclerView;

    MissionAdapter missionAdapter;
    List<Missions> missionList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        generateAndUploadMissions(); // Add this to test/seed data

        // Find TextViews
        tierNameText = view.findViewById(R.id.tier_name);
        pointsNameText = view.findViewById(R.id.points_name);
        voucherNameText = view.findViewById(R.id.voucher_name);

        rewardsRecyclerView = view.findViewById(R.id.rewards_recyclerView);
        rewardAdapter = new RewardsAdapter(requireContext(), rewardList);
        rewardsRecyclerView.setAdapter(rewardAdapter);

        // Set up the horizontal layout for the rewardsRecyclerView
        LinearLayoutManager horizontalLayoutManagerForRewards = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rewardsRecyclerView.setLayoutManager(horizontalLayoutManagerForRewards);


        missionsRecyclerView = view.findViewById(R.id.mission_recyclerView);
        missionAdapter = new MissionAdapter(requireContext(), missionList);
        missionsRecyclerView.setAdapter(missionAdapter);

        // Set up the vertical or other layout for missionsRecyclerView
        LinearLayoutManager horizontalLayoutManagerForMissions = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        missionsRecyclerView.setLayoutManager(horizontalLayoutManagerForMissions);


        dataList = new ArrayList<>();
        adapter = new MyAdapter(requireContext(), dataList);

        db = FirebaseFirestore.getInstance();
        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Rewards
        DBSource rewardsSourceInstance = new RewardsSource(db);
        rewardsModel = new ViewModelProvider(this, new RewardsControllerFactory(rewardsSourceInstance)).get(RewardsController.class);
        rewardsModel.getModelInstance(rewardsModel);

        //Missions
        DBSource missionsSourceInstance = new MissionSource(db);
        missionsModel = new ViewModelProvider(this, new MissionControllerFactory(missionsSourceInstance)).get(MissionController.class);
        missionsModel.getModelInstance(missionsModel);

        userModel.getUserInfoFromSource("userId", getCurrentUserId());
        rewardsModel.getRewardsforAll();

        final Observer<ArrayList<Missions>> missionObserver = new Observer<ArrayList<Missions>>() {
            @Override
            public void onChanged(ArrayList<Missions> missions) {
                if (missions != null){
                    missionAdapter.setMissionList(missions);
                } else {
                    Toast.makeText(requireContext(), "Failed to load missions", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading missions");
                }

            }
        };

        final Observer<ArrayList<Rewards>> rewardsObserver = new Observer<ArrayList<Rewards>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Rewards> rewardsList) {
                if(rewardsList != null){
                    //TODO Implement Redeemed Rewards and then check

                    // Update the rewardAdapter with the new rewards list
                    rewardAdapter.setSearchList(rewardsList);  // or rewardAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load rewards", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (user !=null)){
                    String userTier = user.getTier();
                    int userPoints = user.getCurrentPoints();

                    if (userTier != null) {
                        tierNameText.setText(userTier);
                    }
                    pointsNameText.setText(userPoints + " pts");

                    ArrayList<Missions> redeemedMissions =  user.getRedeemedMissions();
                    ArrayList<String> redeemedMissionIds = new ArrayList<>();
                    for (Missions m: redeemedMissions){
                        redeemedMissionIds.add(m.getMissionId());
                    }

                    missionsModel.getMissionForAll(redeemedMissionIds);
                } else {
                    Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }

            }
        };

        missionsModel.getAllMission().observe(getViewLifecycleOwner(), missionObserver);
        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);
        rewardsModel.getAllRewards().observe(getViewLifecycleOwner(), rewardsObserver);

        //TODO Replace Missions
        //loadMissions();

        return view;
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    //OBSOLETE?
    private void loadRewards(int userPoints) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get current user ID
        db.collection("rewards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Rewards> rewardsList = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            int rewardId = documentSnapshot.getLong("rewardId").intValue();
                            String name = documentSnapshot.getString("name");
                            String description = documentSnapshot.getString("description");
                            int pointsRequired = documentSnapshot.getLong("pointsRequired").intValue();
                            String expirationDate = documentSnapshot.getString("expirationDate");
                            boolean isAvailable = documentSnapshot.getBoolean("available");

                            // Check if the user has already redeemed this reward
                            checkIfRewardRedeemed(userId, rewardId, isRedeemed -> {
                                // If the reward is redeemed or the user doesn't have enough points, block it out
                                boolean isRedeemable = userPoints >= pointsRequired && !isRedeemed;

                                // Create Rewards object with the redeemable status
                                Rewards reward = new Rewards(rewardId, name, description, pointsRequired, expirationDate, isAvailable);

                                // Add to the list
                                rewardsList.add(reward);

                                // If this is the last reward, notify adapter
                                if (rewardsList.size() == queryDocumentSnapshots.size()) {
                                    Log.d("Firestore", "Loaded rewards: " + rewardsList.size());
                                    // Update the rewardAdapter with the new rewards list
                                    rewardAdapter.setSearchList(rewardsList);  // or rewardAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load rewards", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfRewardRedeemed(String userId, int rewardId, RedeemedCallback callback) {
        db.collection("users")
                .document(userId)
                .collection("redeemedRewards")
                .document(String.valueOf(rewardId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean isRedeemed = documentSnapshot.exists(); // If the document exists, it means the reward has been redeemed
                    callback.onChecked(isRedeemed);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking redeemed status", e);
                    callback.onChecked(false); // Consider not redeemed if there is an error
                });
    }

    // Callback interface to handle the result of the redeemed check
    public interface RedeemedCallback {
        void onChecked(boolean isRedeemed);
    }

    private void loadMissions() {
        db.collection("missions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        missionList.clear(); // clear existing missions to avoid duplicates

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String missionId = documentSnapshot.getString("missionId");
                            String missionName = documentSnapshot.getString("missionName");
                            String description = documentSnapshot.getString("description");
                            String criteria = documentSnapshot.getString("criteria");
                            int pointsReward = documentSnapshot.getLong("pointsReward").intValue();
                            int progress = documentSnapshot.getLong("progress").intValue();

                            Missions mission = new Missions(missionId, missionName, description, criteria, pointsReward, progress);
                            missionList.add(mission);
                        }

                        Log.d("Firestore", "Loaded missions: " + missionList.size());
                        missionAdapter.notifyDataSetChanged(); // Refresh adapter
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load missions", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading missions", e);
                });
    }

    private void generateAndUploadMissions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Missions> generatedMissions = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Missions mission = new Missions(
                    "mission" + i,
                    "Mission " + i,
                    "Complete task number " + i,
                    i,
                    10 * i, // Reward increases per mission
                    0 // Initial progress
            );
            generatedMissions.add(mission);
        }

        for (Missions mission : generatedMissions) {
            db.collection("missions")
                    .document(mission.getMissionId())
                    .set(mission)
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firestore", "Mission " + mission.getMissionId() + " added"))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Error adding mission " + mission.getMissionId(), e));
        }
    }

}
    private boolean isRewardRedeemed(String userId, int rewardId) {
        final boolean[] isRedeemed = {false};
        db.collection("users")
                .document(userId)
                .collection("redeemedRewards")
                .whereEqualTo("rewardId", rewardId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // If a document exists, it means the user has already redeemed this reward
                        isRedeemed[0] = true;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking redeemed status", e);
                });
        return isRedeemed[0];
    }

}

