package com.example.wowcher.classes;

public class User {

    //primary key
    private int userId;

    private String username;
    private String role;
    private String createdAt;
    private int availableVouchers;
    private int previousVouchers;

    //User constructor
    public User(int userId, String username, String role, String createdAt, int availableVouchers, int previousVouchers) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.availableVouchers = availableVouchers;
        this.previousVouchers = previousVouchers;
    }
}
