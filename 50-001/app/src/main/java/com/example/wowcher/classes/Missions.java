package com.example.wowcher.classes;

public class Missions {
    private String missionId;
    private String missionName;
    private String description;
    private int criteria;
    private int pointsReward;
    private int progress;
    private String type; // Example: "collect_voucher", "location_voucher", "tier"

    // Optional: for missions that depend on location or tier
    private String locationId;
    private String requiredTier;

    //No-arg constructor
    public Missions() {} // Needed for Firestore deserialization

    public Missions(String missionId, String missionName, String description,
                    int criteria, int pointsReward, int progress,
                    String type, String locationId, String requiredTier) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.description = description;
        this.criteria = criteria;
        this.pointsReward = pointsReward;
        this.progress = progress;
        this.type = type;
        this.locationId = locationId;
        this.requiredTier = requiredTier;
    }

    // Getters
    public String getMissionId() { return missionId; }
    public String getMissionName() { return missionName; }
    public String getDescription() { return description; }
    public int getCriteria() { return criteria; }
    public int getPointsReward() { return pointsReward; }
    public int getProgress() { return progress; }
    public String getType() { return type; }
    public String getLocationId() { return locationId; }
    public String getRequiredTier() { return requiredTier; }

    // Setters
    public void setMissionId(String missionId) { this.missionId = missionId; }
    public void setMissionName(String missionName) { this.missionName = missionName; }
    public void setDescription(String description) { this.description = description; }
    public void setCriteria(int criteria) { this.criteria = criteria; }
    public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setType(String type) { this.type = type; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public void setRequiredTier(String requiredTier) { this.requiredTier = requiredTier; }

    // Logic for checking completion
    public boolean isCompleted(int userVoucherCount, String userTier, String userLocationId) {
        switch (type) {
            case "collect_voucher":
                return userVoucherCount >= criteria;

            case "location_voucher":
                return userVoucherCount >= criteria && locationId != null && locationId.equals(userLocationId);

            case "tier":
                return requiredTier != null && userTier.equalsIgnoreCase(requiredTier);

            default:
                return false;
        }
    }
}