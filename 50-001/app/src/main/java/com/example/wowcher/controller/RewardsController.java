package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.Rewards;
import com.example.wowcher.db.DBSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class RewardsController extends ViewModel {
    private final DBSource databaseInstance;
    private RewardsController instance;

    MutableLiveData<ArrayList<Rewards>> allRewardsList;

    MutableLiveData<ArrayList<Rewards>> specificRewardsList;

    public RewardsController(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public void getModelInstance(RewardsController instance){
        this.instance = instance;
    }

    public MutableLiveData<ArrayList<Rewards>> getAllRewards(){
        if (allRewardsList == null){
            allRewardsList = new MutableLiveData<ArrayList<Rewards>>();
        }
        return allRewardsList;
    }

    public MutableLiveData<ArrayList<Rewards>> getSomeRewards(){
        if (specificRewardsList == null){
            specificRewardsList = new MutableLiveData<ArrayList<Rewards>>();
        }
        return specificRewardsList;
    }

    // Get Rewards for All except redeemed rewards
    public void getRewardsforAll(ArrayList<String> redeemedRewards){
        Consumer<ArrayList<Rewards>> method = (ArrayList<Rewards> rewards) -> { instance.getAllRewards().setValue((ArrayList<Rewards>) rewards); };
        databaseInstance.getAllData( method, "rewardId", redeemedRewards.isEmpty()? "" : redeemedRewards);
    }

    // Get Rewards based on specific value
    public void getSpecificRewards(String column, Object value){
        Consumer<ArrayList<Rewards>> method = (ArrayList<Rewards> rewards) -> { instance.getSomeRewards().setValue((ArrayList<Rewards>) rewards); };
        databaseInstance.getData(column, value, method );
    }
    
}
