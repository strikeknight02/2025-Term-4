package com.example.wowcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Rewards;
import com.example.wowcher.classes.User;
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

public class ClaimedRewardsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Rewards> dataList;
    ClaimedRewardAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    UserController userModel;
    RewardsController rewardsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimedrewards);

        recyclerView = findViewById(R.id.recyclerView); // Get reference to RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Use 1 column grid layout for simplicity

        dataList = new ArrayList<>();
        adapter = new ClaimedRewardAdapter(this, dataList); // Initialize your adapter
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Rewards
        DBSource rewardSourceInstance = new RewardsSource(db);
        rewardsModel= new ViewModelProvider(this, new RewardsControllerFactory(rewardSourceInstance)).get(RewardsController.class);
        rewardsModel.getModelInstance(rewardsModel);

        userModel.getUserInfoFromSource("userId", userId);

        final Observer<ArrayList<Rewards>> rewardsObserver = new Observer<ArrayList<Rewards>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Rewards> rewardsList) {
                if (rewardsList != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    adapter.setSearchList(rewardsList);
                    adapter.notifyDataSetChanged();
                } else{
                    Toast.makeText(ClaimedRewardsActivity.this, "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User userObj) {
                if (userObj != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    ArrayList<String> redeemedRewards = userObj.getRedeemedRewards();
                    ArrayList<Integer> redeemedRewardsInt = new ArrayList<>();
                    for(String r : redeemedRewards){
                        redeemedRewardsInt.add(Integer.parseInt(r));
                    }

                    rewardsModel.getSpecificRewards("rewardId", redeemedRewardsInt);
                    rewardsModel.getSomeRewards().observe(ClaimedRewardsActivity.this, rewardsObserver);
                }
            }
        };

        userModel.getUserInfo().observe(this, userObserver);

        // Bind the back icon and set the click listener
        ImageView backIcon = findViewById(R.id.wowcher_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });
    }

}
