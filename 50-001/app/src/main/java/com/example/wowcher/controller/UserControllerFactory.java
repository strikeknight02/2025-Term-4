package com.example.wowcher.controller;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserControllerFactory implements ViewModelProvider.Factory {
    private DBSource databaseInstance;


    public UserControllerFactory(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserController(databaseInstance);
    }
}
