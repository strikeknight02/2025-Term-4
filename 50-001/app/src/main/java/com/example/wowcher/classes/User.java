package com.example.wowcher.classes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    public int totalPoints = 0; // Default points
    public int currentPoints = 0; //Current Points
    public String createdAt = LocalDateTime.now().toString();
    public ArrayList<Voucher> redeemedVouchers = new ArrayList<Voucher>();

    public ArrayList<Missions> redeemedMissions = new ArrayList<Missions>();

    // No-arg constructor
    public User() {}

    // User constructor
    public User(String userId, String username, String email, String password, String mobileNumber, String role, String tier, int totalPoints, int currentPoints, String createdAt, ArrayList<Voucher> redeemedVouchers, ArrayList<Missions> redeemedMissions) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.tier = tier;
        this.totalPoints = totalPoints;
        this.currentPoints = currentPoints;
        this.createdAt = createdAt;
        this.redeemedVouchers = redeemedVouchers;
        this.redeemedMissions = redeemedMissions;
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

    public int getTotalPoints(){
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints){
        this.totalPoints = totalPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Voucher> getRedeemedVouchers() {
        return redeemedVouchers;
    }

    public void setRedeemedVouchers(ArrayList<Voucher> redeemedVouchers) {
        this.redeemedVouchers = redeemedVouchers;
    }

    public ArrayList<Missions> getRedeemedMissions() {
        return redeemedMissions;
    }

    public void setRedeemedMissions(ArrayList<Missions> redeemedMissions) {
        this.redeemedMissions = redeemedMissions;
    }
}