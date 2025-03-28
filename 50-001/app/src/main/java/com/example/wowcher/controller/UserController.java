package com.example.wowcher.controller;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;
import com.example.wowcher.db.DBSource;


import java.util.ArrayList;
import java.util.function.Consumer;

public class UserController extends ViewModel {
//This is where you MutableLiveData is set up and where the database should be called from

    private UserController instance;

    private final DBSource databaseInstance;

    private MutableLiveData<User> user;
    private MutableLiveData<ArrayList<User>> mUserList;

    //Constructor
    public UserController(DBSource databaseInstance){
        this.databaseInstance = databaseInstance;
    }

    public void getModelInstance(UserController instance){
        this.instance = instance;
    }

    //All User getter
    public MutableLiveData<ArrayList<User>> getAll() {
        if (mUserList == null) {
            mUserList = new MutableLiveData<ArrayList<User>>();
        }
        return mUserList;
    }

    //Get User Information
    public MutableLiveData<User> getUserInfo(){
        if (user == null){
            user = new MutableLiveData<User>();
        }
        return user;
    }

    //Get All Users
    //    public void getAllUsersFromDB(FirebaseFirestore db, UserController model){
    //        userSourceInstance.getAll(db, model);
    //    }


    //Get User Info From Database
    public void getUserInfoFromSource(String column, Object comparison){
        Consumer<User> method = (User user) -> { instance.getUserInfo().setValue(user); };
        databaseInstance.getData(instance, column, comparison, method );
    }

    //Add User to Database
    public void addUser(User user){
        databaseInstance.create(user);
    }

    public void updateUser(String userId, String column, Object newValue){
        //Log.d("UPDATE PARAMS", userId + "::" + column);
        databaseInstance.update(userId, column, newValue);
    }

    public void deleteUser(String userId){
        databaseInstance.delete(userId);
    }
}
