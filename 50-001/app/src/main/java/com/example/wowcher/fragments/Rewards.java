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

import com.example.wowcher.MissionAdapter;
import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.RewardsAdapter;
import com.example.wowcher.classes.Missions;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Rewards extends Fragment {

    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;

    FirebaseFirestore db;
    UserController userModel;
    RewardsController rewardsModel;
    MissionController missionsModel;

    FirebaseAuth auth;
    FirebaseUser user;

    TextView tierNameText, pointsNameText, voucherNameText;

    RecyclerView rewardsRecyclerView;
    RewardsAdapter rewardAdapter;
    List<com.example.wowcher.classes.Rewards> rewardList = new ArrayList<>();

    RecyclerView missionsRecyclerView;

    MissionAdapter missionAdapter;
    List<Missions> missionList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        //Get User Info
        userModel.getUserInfoFromSource("userId", getCurrentUserId());

        //Missions Observer
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

        // Rewards Observer
        final Observer<ArrayList<com.example.wowcher.classes.Rewards>> rewardsObserver = new Observer<ArrayList<com.example.wowcher.classes.Rewards>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<com.example.wowcher.classes.Rewards> rewardsList) {
                if (rewardsList != null) {

                    // Log to check if rewards are being passed correctly
                    Log.d("RewardsObserver", "Rewards list size: " + rewardsList.size());

                    // Extract only digits from the text, e.g. "0 p" -> "0"
                    String pointsText = pointsNameText.getText().toString().replaceAll("[^0-9]", "");
                    Log.d("RewardsObserver", "Points Text: " + pointsText);

                    // Truncate long descriptions
                    for (com.example.wowcher.classes.Rewards r : rewardsList) {
                        if (r.getDescription().length() >= 20) {
                            r.setDescription(r.getDescription().substring(0, 20) + "...");
                        }
                    }

                    // Update the rewardAdapter with the filtered rewards
                    rewardAdapter.setSearchList(rewardsList);

                } else {
                    Toast.makeText(requireContext(), "Failed to load rewards", Toast.LENGTH_SHORT).show();
                    Log.e("RewardsObserver", "Rewards list is null");
                }
            }
        };

        //User Observer
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

                    ArrayList<String> redeemedMissions = user.getRedeemedMissions();
                    ArrayList<String> redeemedRewards = user.getRedeemedRewards();
                    rewardsModel.getRewardsforAll(redeemedRewards);
                    missionsModel.getMissionForAll(redeemedMissions);
                } else {
                    Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //All LiveData observer functions
        missionsModel.getAllMission().observe(getViewLifecycleOwner(), missionObserver);
        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);
        rewardsModel.getAllRewards().observe(getViewLifecycleOwner(), rewardsObserver);

        return view;
    }

    //Get User ID from Firebase Auth
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
