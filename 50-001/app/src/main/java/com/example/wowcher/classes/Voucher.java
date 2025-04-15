package com.example.wowcher.classes;

import java.time.LocalDateTime;

public class Voucher {
    // Primary key
    private String voucherId;

    private String title = "";
    private String details = "";
    private String status = "";
    private String locationId = ""; // Foreign key to Location
    private String createdAt = "";
    private long pointsReward = 0; // Points earned upon redemption

    //no-arg constructor
    public Voucher(){}

    // Constructor
    public Voucher(String voucherId, String title, String details, String status, String locationId, String createdAt, long pointsReward) {
        this.voucherId = voucherId;
        this.title = title;
        this.details = details;
        this.status = status;
        this.locationId = locationId;
        this.createdAt = createdAt;
        this.pointsReward = pointsReward;
    }

    public String getVoucherId() {
        return voucherId;
    }
    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }
    public String getLocationId() {return locationId;}
    public String getCreatedAt() {
        return createdAt;
    }
    public long getPointsReward() {
        return pointsReward;
    }

    // Setters
    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setPointsReward(long pointsReward) {
        this.pointsReward = pointsReward;
    }
}
