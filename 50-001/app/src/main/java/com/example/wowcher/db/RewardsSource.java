package com.example.wowcher.db;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.wowcher.classes.Rewards;
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
import java.util.List;
import java.util.function.Consumer;

public class RewardsSource implements DBSource{

    private final FirebaseFirestore db;
    private final CollectionReference rewardsCollection;
    public RewardsSource(FirebaseFirestore db){
        this.db = db;
        this.rewardsCollection = db.collection("rewards");
    }
    @Override
    public void getAllData(Consumer<?> method, String column, Object extras) {
        if (!extras.equals("")){
            rewardsCollection
                    .whereNotIn(column, (List<? extends Object>) extras)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Rewards rewards = document.toObject(Rewards.class);
                                    rewardsList.add(rewards);
                                }
                                if (method instanceof Consumer<?>){

                                    Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                    methodCast.accept(rewardsList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

            rewardsCollection
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                            for (QueryDocumentSnapshot document : value) {
                                Rewards rewards = document.toObject(Rewards.class);
                                rewardsList.add(rewards);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                methodCast.accept(rewardsList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        }
                    });
        } else {
            rewardsCollection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Rewards rewards = document.toObject(Rewards.class);
                                    rewardsList.add(rewards);
                                }
                                if (method instanceof Consumer<?>){

                                    Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                    methodCast.accept(rewardsList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

            rewardsCollection
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                            for (QueryDocumentSnapshot document : value) {
                                Rewards rewards = document.toObject(Rewards.class);
                                rewardsList.add(rewards);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                methodCast.accept(rewardsList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        }
                    });
        }

    }

    @Override
    public void getData(String column, Object comparison, Consumer<?> method) {
        if(comparison instanceof ArrayList<?>){
            if (((ArrayList<?>) comparison).isEmpty()){
                if (method instanceof Consumer<?>){
                    Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                    methodCast.accept(new ArrayList<>());
                } else {
                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                }
            } else {
                rewardsCollection
                        .whereIn(column, (List<? extends Object>) comparison)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                                    for (QueryDocumentSnapshot document : task.getResult()){

                                        Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                        Rewards rewards = document.toObject(Rewards.class);
                                        rewardsList.add(rewards);
                                    }

                                    if (method instanceof Consumer<?>){

                                        Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                        methodCast.accept(rewardsList);
                                    } else {
                                        Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                rewardsCollection
                        .whereIn(column, (List<? extends Object>) comparison)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                                for (QueryDocumentSnapshot document : value){

                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Rewards rewards = document.toObject(Rewards.class);
                                    rewardsList.add(rewards);
                                }

                                if (method != null){
                                    Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                    methodCast.accept(rewardsList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            }
                        });
            }

        } else {
            rewardsCollection
                    .whereEqualTo(column, comparison)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                                for (QueryDocumentSnapshot document : task.getResult()){

                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Rewards rewards = document.toObject(Rewards.class);
                                    rewardsList.add(rewards);
                                }

                                if (method instanceof Consumer<?>){

                                    Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                    methodCast.accept(rewardsList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

            rewardsCollection
                    .whereEqualTo(column, comparison)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            ArrayList<Rewards> rewardsList = new ArrayList<Rewards>();
                            for (QueryDocumentSnapshot document : value){

                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                Rewards rewards = document.toObject(Rewards.class);
                                rewardsList.add(rewards);
                            }

                            if (method != null){
                                Consumer<ArrayList<Rewards>> methodCast = (Consumer<ArrayList<Rewards>>) method;
                                methodCast.accept(rewardsList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        }
                    });
        }
    }

    @Override
    public void create(Object t) {
        rewardsCollection
                .add(t)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SUCCESSFUL CREATE", "DocumentSnapshot written with ID: " + documentReference.getId());

                        //FOR CHANGING ID OF REWARDSS TO DOCUMENT REFERENCE
                        rewardsCollection
                                .document(documentReference.getId())
                                .update("rewardId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("SUCCESSFUL UPDATE ID", "DocumentSnapshot successfully updated with ID!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("UNSUCCESSFUL UPDATE ID", "Error updating document with ID", e);
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
        rewardsCollection
                .document(reference)
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
                        Log.w("UNSUCCESSFUL DELETE", "Error deleting document", e);
                    }
                });
    }

    @Override
    public void update(String reference, String column, Object newValues) {
        rewardsCollection
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
