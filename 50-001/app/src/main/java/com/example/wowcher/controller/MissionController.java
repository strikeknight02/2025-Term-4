package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.wowcher.classes.Missions;
import com.example.wowcher.db.DBSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MissionController extends ViewModel {
    private final DBSource databaseInstance;
    private MissionController instance;

    MutableLiveData<ArrayList<Missions>> allMissionList;

    MutableLiveData<ArrayList<Missions>> specificMissionList;

    public MissionController(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public void getModelInstance(MissionController instance){
        this.instance = instance;
    }

    public MutableLiveData<ArrayList<Missions>> getAllMission(){
        if (allMissionList == null){
            allMissionList = new MutableLiveData<ArrayList<Missions>>();
        }
        return allMissionList;
    }

    public MutableLiveData<ArrayList<Missions>> getSomeMission(){
        if (specificMissionList == null){
            specificMissionList = new MutableLiveData<ArrayList<Missions>>();
        }
        return specificMissionList;
    }

    public void getMissionforAll(){
        Consumer<ArrayList<Missions>> method = (ArrayList<Missions> Missions) -> { instance.getAllMission().setValue((ArrayList<Missions>) Missions); };
        databaseInstance.getAllData( method );
    }

    public void getSpecificMission(String column, String value){
        Consumer<ArrayList<Missions>> method = (ArrayList<Missions> Missions) -> { instance.getSomeMission().setValue((ArrayList<Missions>) Missions); };
        databaseInstance.getData(column, value, method );
    }
}
