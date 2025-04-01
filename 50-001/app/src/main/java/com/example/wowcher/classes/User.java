package com.example.wowcher.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private int userId;
    private String username;
    private String role;
    private String createdAt;
    private int availableVouchers;
    private int previousVouchers;

    public User(int userId, String username, String role, String createdAt, int availableVouchers, int previousVouchers) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.availableVouchers = availableVouchers;
        this.previousVouchers = previousVouchers;
    }

    public static List<String> getRedeemedVouchers(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String redeemedVouchers = sharedPreferences.getString("redeemedVouchers", "");

        if (redeemedVouchers.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(redeemedVouchers.split(";")));
    }
}
