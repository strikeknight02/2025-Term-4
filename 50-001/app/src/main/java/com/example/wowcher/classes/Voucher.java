package com.example.wowcher.classes;

import java.time.LocalDateTime;

public class Voucher {
    //primary key
    private String voucherId = "";

    private String title = "";
    private String details = "";
    private String status = "";
    private String locationId = ""; // Foreign key to Location
    private String createdAt = "";

    //no-arg constructor
    public Voucher(){}

    //Voucher constructor
    public Voucher(String voucherId, String title, String details, String status, String locationId, String createdAt) {
        this.title = title;
        this.details = details;
        this.status = status;
        this.locationId = locationId;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLocationId() {
        return locationId;
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
}
