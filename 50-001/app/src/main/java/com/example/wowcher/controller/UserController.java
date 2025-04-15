package com.example.wowcher.controller;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;


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
    public MutableLiveData<ArrayList<User>> getAllUsers() {
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

    //Get All
    public void getUsers(){
        Consumer<ArrayList<User>> method = (ArrayList<User> users) -> { instance.getAllUsers().setValue((ArrayList<User>) users); };
        databaseInstance.getAllData( method , "");
    }

    //Get User Info From Database
    public void getUserInfoFromSource(String column, Object comparison){
        Consumer<ArrayList<User>> method = (ArrayList<User> userList) -> { if(!userList.isEmpty()) {instance.getUserInfo().setValue(userList.get(0));} else {
            Log.d("BRO", "NOT WORKING");} };
        databaseInstance.getData(column, comparison, method );
    }

    //Add User to Database
    public void addUser(User user){
        databaseInstance.create(user);
    }

    //Update a User attribute
    public void updateUser(String userId, String column, Object newValue){
        Log.i("UPDATING...", "USER: " + userId + " AND THIS COLUMN: " + column);
        databaseInstance.update(userId, column, newValue);
    }

    //Delete a User
    public void deleteUser(String userId){
        databaseInstance.delete(userId);
    }
}
