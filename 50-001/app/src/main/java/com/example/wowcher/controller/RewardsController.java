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

    public void getRewardsforAll(){
        Consumer<ArrayList<Rewards>> method = (ArrayList<Rewards> rewardss) -> { instance.getAllRewards().setValue((ArrayList<Rewards>) rewardss); };
        databaseInstance.getAllData( method , "");
    }

    public void getSpecificRewards(String column, String value){
        Consumer<ArrayList<Rewards>> method = (ArrayList<Rewards> rewardss) -> { instance.getSomeRewards().setValue((ArrayList<Rewards>) rewardss); };
        databaseInstance.getData(column, value, method );
    }
    
}
