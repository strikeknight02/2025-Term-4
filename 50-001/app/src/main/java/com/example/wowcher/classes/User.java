package com.example.wowcher.classes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class User {

    // Primary key
    public String userId = "";
    public String username = "";
    public String email = "";
    public String password = "";
    public String mobileNumber = "";
    public String role = "";
    public String tier = "Bronze"; // Default tier
    public int points = 0; // Default points
    public String createdAt = LocalDateTime.now().toString();
    public int availableVouchers = 0;
    public int previousVouchers = 0;

    // No-arg constructor
    public User() {}

    // User constructor
    public User(String userId, String username, String email, String password, String mobileNumber, String role, String tier, int points, String createdAt, int availableVouchers, int previousVouchers) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.tier = tier;
        this.points = points;
        this.createdAt = createdAt;
        this.availableVouchers = availableVouchers;
        this.previousVouchers = previousVouchers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
