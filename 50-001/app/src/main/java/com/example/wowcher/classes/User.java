package com.example.wowcher.classes;

public class User {

    //primary key
    public double userId= 0.0;

    public String username = "";
    public String role = "";
    public String createdAt = "";
    public int availableVouchers = 0;
    public int previousVouchers = 0;

    //no-arg constructor
    public User(){}


    //User constructor
    public User(double userId, String username, String role, String createdAt, int availableVouchers, int previousVouchers) {
        this.userId = userId;
        this.username = username;
        this.role = role;
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

    public double getUserId() {
        return userId;
    }

    public void setUserId(double userId) {
        this.userId = userId;
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
}
