package com.example.wowcher.db;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;
import com.example.wowcher.controller.UserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserSource implements DBSource{

    @Override
    public void getAll(FirebaseFirestore db, ViewModel model){
        UserController userVM = (UserController) model;

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<User> mUserList = new ArrayList<User>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                mUserList.add(user);
                                Log.d("SUCCESSFUL GET", mUserList.get(0).getUsername());
                                userVM.getAll().setValue(mUserList);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<User> mUserList = new ArrayList<User>();
                        for (QueryDocumentSnapshot document : value) {
                            User user = document.toObject(User.class);
                            mUserList.add(user);
                        }
                        userVM.getAll().setValue(mUserList);
                    }
                });
    }

    @Override
    public void getOne(FirebaseFirestore db, ViewModel model, String column, Object comparison) {

    }

    @Override
    public void create(FirebaseFirestore db, ViewModel model, Object t) {

    }

    @Override
    public void delete(FirebaseFirestore db, ViewModel model, Object t) {

    }

    @Override
    public void update(FirebaseFirestore db, ViewModel model, String column, Object newValue) {

    }

}
