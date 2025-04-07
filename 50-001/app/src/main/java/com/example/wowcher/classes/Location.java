package com.example.wowcher.classes;

public class Location {
    //primary key
    private String locationId = "";

    private int locationType = 0;
    private String geolocation = "";
    private String createdAt = "";

    //location constructor
    public Location(String locationId, int locationType, String geolocation, String createdAt) {
        this.locationId = locationId;
        this.locationType = locationType;
        this.geolocation = geolocation;
        this.createdAt = createdAt;
    }

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

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
