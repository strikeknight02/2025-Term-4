package com.example.wowcher.classes;

public class Missions {
    private String missionId;
    private String missionName;
    private String description;
    private long criteria; // Criteria for the mission (e.g., "redeem 1 voucher")
    private long pointsReward;
    private long progress;

    public Missions() {} // Needed for Firestore deserialization

    public Missions(String missionId, String missionName, String description, long criteria, long pointsReward, long progress) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.description = description;
        this.criteria = criteria;
        this.pointsReward = pointsReward;
        this.progress = progress;
    }

    // Getters and setters
    public String getMissionId() { return missionId; }
    public String getMissionName() { return missionName; }
    public String getDescription() { return description; }
    public long getCriteria() { return criteria; }
    public long getPointsReward() { return pointsReward; }
    public long getProgress() { return progress; }

    public void setMissionId(String missionId) { this.missionId = missionId; }
    public void setMissionName(String missionName) { this.missionName = missionName; }
    public void setDescription(String description) { this.description = description; }
    public void setCriteria(long criteria) { this.criteria = criteria; }
    public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }
    public void setProgress(int progress) { this.progress = progress; }

    // Check if mission criteria are met
    public boolean checkMissionProgress(int userVoucherCount) {
        return userVoucherCount >= criteria;
    }


}
