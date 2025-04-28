package com.example.wowcher.controller;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.db.DBSource;

public class LocationControllerFactory implements ViewModelProvider.Factory {
    private final DBSource databaseInstance;

    public LocationControllerFactory(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LocationController(databaseInstance);
    }
}
