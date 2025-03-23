package com.example.wowcher.classes;

public class Voucher {
    //primary key
    private int voucherId = 0;

    private String title = "";
    private String details = "";
    private String status = "";
    private int locationId = 0; // Foreign key to Location
    private String createdAt = "";

    public Voucher(int voucherId, String title, String details, String status, int locationId, String createdAt) {
        this.title = title;
        this.details = details;
        this.status = status;
        this.locationId = locationId;
        this.createdAt = createdAt;
    }
}
