package com.example.wowcher.db;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.wowcher.classes.Missions;
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

public class MissionSource implements DBSource{

    private final FirebaseFirestore db;
    private final CollectionReference missionCollection;
    public MissionSource(FirebaseFirestore db){
        this.db = db;
        this.missionCollection = db.collection("missions");
    }
    @Override
    public void getAllData(Consumer<?> method, Object extras) {
        if (!extras.equals("")){
            missionCollection
                    .whereNotIn("missionId", (List<? extends Object>) extras)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Missions> missionList = new ArrayList<Missions>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Missions mission = document.toObject(Missions.class);
                                    missionList.add(mission);
                                }
                                if (method instanceof Consumer<?>){

                                    Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                                    methodCast.accept(missionList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });


            missionCollection
                    .whereNotIn("missionId", (List<? extends Object>) extras)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            ArrayList<Missions> missionList = new ArrayList<Missions>();
                            for (QueryDocumentSnapshot document : value) {
                                Missions mission = document.toObject(Missions.class);
                                missionList.add(mission);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                                methodCast.accept(missionList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        }
                    });
        } else {
            missionCollection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Missions> missionList = new ArrayList<Missions>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                    Missions mission = document.toObject(Missions.class);
                                    missionList.add(mission);
                                }
                                if (method instanceof Consumer<?>){

                                    Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                                    methodCast.accept(missionList);
                                } else {
                                    Log.d("INVALID PARAMETER", "Invalid Method passed!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });


            missionCollection
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            ArrayList<Missions> missionList = new ArrayList<Missions>();
                            for (QueryDocumentSnapshot document : value) {
                                Missions mission = document.toObject(Missions.class);
                                missionList.add(mission);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                                methodCast.accept(missionList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        }
                    });
        }

    }

    @Override
    public void getData(String column, Object comparison, Consumer<?> method) {
        missionCollection
                .whereEqualTo(column, comparison)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Missions> missionList = new ArrayList<Missions>();
                            for (QueryDocumentSnapshot document : task.getResult()){

                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                Missions mission = document.toObject(Missions.class);
                                missionList.add(mission);
                            }

                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                                methodCast.accept(missionList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        missionCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<Missions> missionList = new ArrayList<Missions>();
                        for (QueryDocumentSnapshot document : value){

                            Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                            Missions mission = document.toObject(Missions.class);
                            missionList.add(mission);
                        }

                        if (method != null){
                            Consumer<ArrayList<Missions>> methodCast = (Consumer<ArrayList<Missions>>) method;
                            methodCast.accept(missionList);
                        } else {
                            Log.d("INVALID PARAMETER", "Invalid Method passed!");
                        }
                    }
                });
    }

    @Override
    public void create(Object t) {
        missionCollection
                .add(t)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SUCCESSFUL CREATE", "DocumentSnapshot written with ID: " + documentReference.getId());

                        //FOR CHANGING ID OF MISSIONS TO DOCUMENT REFERENCE
                        missionCollection
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
        missionCollection
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
        missionCollection
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
