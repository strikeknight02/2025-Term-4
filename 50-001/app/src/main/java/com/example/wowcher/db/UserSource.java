package com.example.wowcher.db;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.UserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.Consumer;

public class UserSource implements DBSource{
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;
    public UserSource(FirebaseFirestore db){
        this.db = db;
        this.userCollection = db.collection("users");
    }

    @Override
    public void getAllData(Consumer<?> method){
        userCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<User> userList = new ArrayList<User>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<User>> methodCast = (Consumer<ArrayList<User>>) method;
                                methodCast.accept(userList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        userCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<User> userList = new ArrayList<User>();
                        for (QueryDocumentSnapshot document : value) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        if (method instanceof Consumer<?>){

                            Consumer<ArrayList<User>> methodCast = (Consumer<ArrayList<User>>) method;
                            methodCast.accept(userList);
                        } else {
                            Log.d("INVALID PARAMETER", "Invalid Method passed!");
                        }
                    }
                });
    }

    @Override
    public void getData(String column, Object comparison, Consumer<?> method) {
        db.collection("users")
                .whereEqualTo(column, comparison)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task){
                        if (task.isSuccessful()) {
                            ArrayList<User> UserList = new ArrayList<User>();
                            for (QueryDocumentSnapshot document : task.getResult()){

                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                UserList.add(user);
                            }

                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<User>> methodCast = (Consumer<ArrayList<User>>) method;
                                methodCast.accept(UserList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("users")
                .whereEqualTo(column, comparison)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        QueryDocumentSnapshot document = value.iterator().next();

                        Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                        User user = document.toObject(User.class);

                        if (method instanceof Consumer<?>){
                            Consumer<User> methodCast = (Consumer<User>) method;
                            methodCast.accept(user);
                        } else {
                            Log.d("INVALID PARAMETER", "Invalid Method passed!");
                        }
                    }
                });
    }

    @Override
    public void create( Object t) {
        db.collection("users")
                .add(t)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SUCCESSFUL CREATE", "DocumentSnapshot written with ID: " + documentReference.getId());

                        userCollection
                                .document(documentReference.getId())
                                .update("userId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated with ID!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document with ID", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("BOOO NO CREATE", "Error writing document", e);
                    }
                });

    }

    @Override
    public void delete(String reference) {
        db.collection("users").document(reference)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESSFUL DELETE", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    @Override
    public void update( String reference,  String column, Object newValues) {
        db.collection("users")
                .document(reference)
                .update(column, newValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESSFUL UPDATE", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UNSUCCESSFUL UPDATE", "Error updating document", e);
                    }
                });
    }

}
