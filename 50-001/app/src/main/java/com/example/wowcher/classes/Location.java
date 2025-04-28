package com.example.wowcher.classes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Location {
    // Primary key
    private String locationId = "";

    private int locationType = 0;
    private GeoPoint geolocation = new GeoPoint(0.0, 0.0);
    private String createdAt = LocalDateTime.now().toString();
    private String name = ""; // Name of the location
    private String imageName = ""; // New imageName field

    // Full constructor
    public Location(String locationId, int locationType, GeoPoint geolocation, String createdAt, String name, String imageName) {
        this.locationId = locationId;
        this.locationType = locationType;
        this.geolocation = geolocation;
        this.createdAt = createdAt;
        this.name = name;
        this.imageName = imageName;
    }

    // No-arg constructor (required by Firestore)
    public Location() {}

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public GeoPoint getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
