package com.example.wowcher.classes;

public class Voucher {
    // Primary key
    private String voucherId;

    private String title = "";
    private String details = "";
    private String status = "";
    private String locationId = ""; // Foreign key to Location
    private String createdAt = "";
    private int pointsReward = 0; // Points earned upon redemption
    private String code = ""; // Unique voucher code
    private String imageName = ""; // Name of the drawable resource (e.g., "voucher_tofu")

    //no-arg constructor
    // Needed for Firestore deserialization
    public Voucher(){}

    // Constructor
    public Voucher(String voucherId, String title, String details, String status, String locationId, String createdAt, int pointsReward, String code, String imageName) {
        this.voucherId = voucherId;
        this.title = title;
        this.details = details;
        this.status = status;
        this.locationId = locationId;
        this.createdAt = createdAt;
        this.pointsReward = pointsReward;
        this.code = code;
        this.imageName = imageName;
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
    public int getPointsReward() {
        return pointsReward;
    }

    public String getCode() {
        return code;
    }

    public String getImageName() {
        return imageName;
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
    public void setPointsReward(int pointsReward) {
        this.pointsReward = pointsReward;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
