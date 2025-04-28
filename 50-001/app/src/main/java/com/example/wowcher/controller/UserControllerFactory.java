package com.example.wowcher.controller;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.db.DBSource;

public class UserControllerFactory implements ViewModelProvider.Factory {
    private final DBSource databaseInstance;

    public UserControllerFactory(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserController(databaseInstance);
    }
}
