package com.example.wowcher.classes;


public class User {

    // Primary key
    private int userId;

    private String username;
    private String role;
    private String createdAt;
    private int availableVouchers;
    private int previousVouchers;
    private String tier;  // New attribute
    private int points;   // New attribute

    // User constructor
    public User(int userId, String username, String role, String createdAt, int availableVouchers, int previousVouchers, String tier, int points) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.availableVouchers = availableVouchers;
        this.previousVouchers = previousVouchers;
        this.tier = tier;
        this.points = points;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getAvailableVouchers() {
        return availableVouchers;
    }

    public void setAvailableVouchers(int availableVouchers) {
        this.availableVouchers = availableVouchers;
    }

    public int getPreviousVouchers() {
        return previousVouchers;
    }

    public void setPreviousVouchers(int previousVouchers) {
        this.previousVouchers = previousVouchers;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

