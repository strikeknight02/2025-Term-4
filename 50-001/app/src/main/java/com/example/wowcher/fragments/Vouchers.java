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
import com.example.wowcher.controller.RewardsController;
import com.example.wowcher.controller.RewardsControllerFactory;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.RewardsSource;
import com.example.wowcher.db.UserSource;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Vouchers extends Fragment {

    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;

    FirebaseFirestore db;
    UserController userModel;
    RewardsController rewardsModel;

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

        userModel.getUserInfoFromSource("userId", getCurrentUserId());
        rewardsModel.getRewardsforAll();

        final Observer<ArrayList<Rewards>> rewardsObserver = new Observer<ArrayList<Rewards>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Rewards> rewardsList) {
                if(rewardsList != null){
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
                    int userPoints = user.getPoints();

                    if (userTier != null) {
                        tierNameText.setText(userTier);
                    }
                    pointsNameText.setText(userPoints + " pts");
                } else {
                    Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }

            }
        };

        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);
        rewardsModel.getAllRewards().observe(getViewLifecycleOwner(), rewardsObserver);

        //loadUserInfo();
        //loadRewards();
        loadMissions();
//        loadUserVoucherCount();

        return view;
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void loadUserInfo() {
        String userId = getCurrentUserId();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tier = documentSnapshot.getString("tier");
                        Long points = documentSnapshot.getLong("points");

                        if (tier != null) {
                            tierNameText.setText(tier);
                        }
                        if (points != null) {
                            pointsNameText.setText(points + " pts");
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadRewards() {
        String userId = getCurrentUserId();  // Assuming rewards are specific to the user
        db.collection("rewards")
                  .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Rewards> rewardsList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Retrieve reward attributes from Firestore document
                            int rewardId = documentSnapshot.getLong("rewardId").intValue();
                            String name = documentSnapshot.getString("name");
                            String description = documentSnapshot.getString("description");
                            int pointsRequired = documentSnapshot.getLong("pointsRequired").intValue();
                            String expirationDate = documentSnapshot.getString("expirationDate");
                            boolean isAvailable = documentSnapshot.getBoolean("available");

                            // Create Rewards object
                            Rewards reward = new Rewards(rewardId, name, description, pointsRequired, expirationDate, isAvailable);

                            // Add to the list
                            rewardsList.add(reward);
                        }
                        Log.d("Firestore", "Loaded rewards: " + rewardsList.size());
                        // Update the rewardAdapter with the new rewards list
                        rewardAdapter.setSearchList(rewardsList);  // or rewardAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load rewards", Toast.LENGTH_SHORT).show();
                });
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
                    "Do something " + i + " times",
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

//    private void loadUserVoucherCount() {
//        String userId = getCurrentUserId();
//
//        db.collection("vouchers")
//                .whereEqualTo("userId", userId)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    int count = querySnapshot.size();
//                    voucherNameText.setText(count + " vouchers");
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(requireContext(), "Failed to load voucher count", Toast.LENGTH_SHORT).show()
//                );
//    }
}
