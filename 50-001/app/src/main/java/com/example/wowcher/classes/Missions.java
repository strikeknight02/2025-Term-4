package com.example.wowcher.classes;

public class Missions {
    private String missionId;
    private String missionName;
    private String description;
    private String criteria;
    private int pointsReward;
    private int progress;

    public Missions() {} // Needed for Firestore deserialization

    public Missions(String missionId, String missionName, String description, String criteria, int pointsReward, int progress) {
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
    public String getCriteria() { return criteria; }
    public int getPointsReward() { return pointsReward; }
    public int getProgress() { return progress; }

    public void setMissionId(String missionId) { this.missionId = missionId; }
    public void setMissionName(String missionName) { this.missionName = missionName; }
    public void setDescription(String description) { this.description = description; }
    public void setCriteria(String criteria) { this.criteria = criteria; }
    public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }
    public void setProgress(int progress) { this.progress = progress; }
}

