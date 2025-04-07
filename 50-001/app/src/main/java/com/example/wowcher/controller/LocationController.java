package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.Location;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.db.DBSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LocationController extends ViewModel {

    private final DBSource databaseInstance;
    private LocationController instance;

    MutableLiveData<ArrayList<Location>> allLocationList;

    MutableLiveData<ArrayList<Location>> specificLocationList;

    public LocationController(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public void getModelInstance(LocationController instance){
        this.instance = instance;
    }

    public MutableLiveData<ArrayList<Location>> getAllLocations(){
        if (allLocationList == null){
            allLocationList = new MutableLiveData<ArrayList<Location>>();
        }
        return allLocationList;
    }

    public MutableLiveData<ArrayList<Location>> getSomeLocations(){
        if (specificLocationList == null){
            specificLocationList = new MutableLiveData<ArrayList<Location>>();
        }
        return specificLocationList;
    }

    public void getLocationforAll(){
        Consumer<ArrayList<Location>> method = (ArrayList<Location> locations) -> { instance.getAllLocations().setValue((ArrayList<Location>) locations); };
        databaseInstance.getAllData( method );
    }

    public void getSpecificLocations(String category){
        Consumer<ArrayList<Location>> method = (ArrayList<Location> locations) -> { instance.getSomeLocations().setValue((ArrayList<Location>) locations); };
        databaseInstance.getData("category", category, method );
    }

    public void updateLocation(String locationID, String column, Object newValue){
        databaseInstance.update(locationID, column, newValue);
    }
}
