package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.Location;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.LocationSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LocationController extends ViewModel {

    private final DBSource databaseInstance;
    private LocationController instance;

    private MutableLiveData<ArrayList<Location>> allLocationList;

    private MutableLiveData<ArrayList<Location>> specificLocationList;

    private MutableLiveData<ArrayList<Location>> locationBasedOnVouchers = new MutableLiveData<ArrayList<Location>>();

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

    public MutableLiveData<ArrayList<Location>> locationBasedVouchers(){
        if (locationBasedOnVouchers == null){
            locationBasedOnVouchers = new MutableLiveData<ArrayList<Location>>();
        }
        return locationBasedOnVouchers;
    }

    public void getLocationforAll(){
        Consumer<ArrayList<Location>> method = (ArrayList<Location> locations) -> { instance.getAllLocations().setValue((ArrayList<Location>) locations); };
        databaseInstance.getAllData( method );
    }

    public void getLocationsBasedOnVoucher(ArrayList<String> locationList){
        Consumer<ArrayList<Location>> method = (ArrayList<Location> locations) -> { instance.locationBasedVouchers().setValue((ArrayList<Location>) locations); };
        LocationSource locationDatabaseInstance = (LocationSource) databaseInstance;
        locationDatabaseInstance.getLocationBasedOnVoucher( method, locationList );
    }

    public void getSpecificLocations(String category, Object comparison){
        Consumer<ArrayList<Location>> method = (ArrayList<Location> locations) -> { instance.getSomeLocations().setValue((ArrayList<Location>) locations); };
        databaseInstance.getData(category, comparison, method );
    }

    public void updateLocation(String locationID, String column, Object newValue){
        databaseInstance.update(locationID, column, newValue);
    }

}
