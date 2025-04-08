package com.example.wowcher.classes;

public class Rewards {

    // Attributes
    private int rewardId;        // Unique ID for the reward
    private String name;         // Name of the reward
    private String description;  // Description of the reward
    private int pointsRequired;  // Points needed to redeem the reward
    private String expirationDate; // Expiration date of the reward
    private boolean isAvailable; // Whether the reward is still available

    // Constructor
    public Rewards(int rewardId, String name, String description, int pointsRequired, String expirationDate, boolean isAvailable) {
        this.rewardId = rewardId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.expirationDate = expirationDate;
        this.isAvailable = isAvailable;
    }

    public Rewards(){}

    // Getters and Setters
    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
