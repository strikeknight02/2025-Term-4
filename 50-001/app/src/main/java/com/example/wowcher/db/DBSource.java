package com.example.wowcher.db;

import android.view.View;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public interface DBSource {

    void getAll(FirebaseFirestore db, ViewModel model);
    void getOne(FirebaseFirestore db, ViewModel model, String column, Object comparison);
    void create(FirebaseFirestore db, ViewModel model, Object t);
    void delete(FirebaseFirestore db, ViewModel model, Object t);
    void update(FirebaseFirestore db, ViewModel model, String column, Object newValue);

}
