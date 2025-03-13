package com.example.wowcher.classes;

public class Location {
    //primary key
    private int locationId;

    private int locationType;
    private String geolocation;
    private String createdAt;

    //location constructor
    public Location(int locationId, int locationType, String geolocation, String createdAt) {
        this.locationId = locationId;
        this.locationType = locationType;
        this.geolocation = geolocation;
        this.createdAt = createdAt;
    }
}
