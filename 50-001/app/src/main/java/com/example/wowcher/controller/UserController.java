package com.example.wowcher.controller;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;
import com.example.wowcher.db.UserSource;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class UserController extends ViewModel {

    private MutableLiveData<User> user;
    private MutableLiveData<ArrayList<User>> mUserList;

    private UserSource userSourceInstance = new UserSource();

    //All User getter
    public MutableLiveData<ArrayList<User>> getAll() {
        if (mUserList == null) {
            mUserList = new MutableLiveData<ArrayList<User>>();
        }
        return mUserList;
    }

    public void getAllFromDB(FirebaseFirestore db, UserController model){
        userSourceInstance.getAll(db, model);
    }

}
