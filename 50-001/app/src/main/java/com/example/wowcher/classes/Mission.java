package com.example.wowcher.classes;

public class Mission {
    private String missionId;
    private String missionName;
    private String description;
    private long criteria;
    private long pointsReward;
    private long progress;
    private String type; // Example: "collect_voucher", "location_voucher", "tier"

    // Optional: for missions that depend on location or tier
    private String locationId;
    private String requiredTier;

    public Mission() {} // Needed for Firestore deserialization

    public Mission(String missionId, String missionName, String description,
                   long criteria, long pointsReward, long progress,
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
    public long getCriteria() { return criteria; }
    public long getPointsReward() { return pointsReward; }
    public long getProgress() { return progress; }
    public String getType() { return type; }
    public String getLocationId() { return locationId; }
    public String getRequiredTier() { return requiredTier; }

    // Setters
    public void setMissionId(String missionId) { this.missionId = missionId; }
    public void setMissionName(String missionName) { this.missionName = missionName; }
    public void setDescription(String description) { this.description = description; }
    public void setCriteria(long criteria) { this.criteria = criteria; }
    public void setPointsReward(long pointsReward) { this.pointsReward = pointsReward; }
    public void setProgress(long progress) { this.progress = progress; }
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
