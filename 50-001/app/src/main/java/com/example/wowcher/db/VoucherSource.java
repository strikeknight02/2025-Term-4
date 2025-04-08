package com.example.wowcher.db;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.wowcher.classes.Voucher;
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

public class VoucherSource implements DBSource{
    FirebaseFirestore db;
    private final CollectionReference voucherCollection;

    public VoucherSource(FirebaseFirestore db){
        this.db = db;
        this.voucherCollection = db.collection("vouchers");
    }

    @Override
    public void getAllData(Consumer<?> method){
        voucherCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                Voucher voucher = document.toObject(Voucher.class);
                                voucherList.add(voucher);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Voucher>> methodCast = (Consumer<ArrayList<Voucher>>) method;
                                methodCast.accept(voucherList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        voucherCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
                        for (QueryDocumentSnapshot document : value) {
                            Voucher voucher = document.toObject(Voucher.class);
                            voucherList.add(voucher);
                        }
                        if (method instanceof Consumer<?>){

                            Consumer<ArrayList<Voucher>> methodCast = (Consumer<ArrayList<Voucher>>) method;
                            methodCast.accept(voucherList);
                        } else {
                            Log.d("INVALID PARAMETER", "Invalid Method passed!");
                        }
                    }
                });
    }

    //Voucher Specific
    public void getAllUserVouchers(Consumer<?> method, ArrayList<String> redeemedVouchers){
        voucherCollection
                .whereNotIn("voucherId", redeemedVouchers)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                Voucher voucher = document.toObject(Voucher.class);
                                voucherList.add(voucher);
                            }
                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Voucher>> methodCast = (Consumer<ArrayList<Voucher>>) method;
                                methodCast.accept(voucherList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getData( String column, Object comparison, Consumer<?> method) {
        voucherCollection
                .whereEqualTo(column, comparison)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
                            for (QueryDocumentSnapshot document : task.getResult()){

                                Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                                Voucher voucher = document.toObject(Voucher.class);
                                voucherList.add(voucher);
                            }

                            if (method instanceof Consumer<?>){

                                Consumer<ArrayList<Voucher>> methodCast = (Consumer<ArrayList<Voucher>>) method;
                                methodCast.accept(voucherList);
                            } else {
                                Log.d("INVALID PARAMETER", "Invalid Method passed!");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        voucherCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
                        for (QueryDocumentSnapshot document : value){

                            Log.d("DOCUMENT OUTPUT", document.getId() + " => " + document.getData());
                            Voucher voucher = document.toObject(Voucher.class);
                            voucherList.add(voucher);
                        }

                        if (method != null){
                            Consumer<ArrayList<Voucher>> methodCast = (Consumer<ArrayList<Voucher>>) method;
                            methodCast.accept(voucherList);
                        } else {
                            Log.d("INVALID PARAMETER", "Invalid Method passed!");
                        }
                    }
                });
    }

    @Override
    public void create(Object t) {
        voucherCollection
                .add(t)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SUCCESSFUL CREATE", "DocumentSnapshot written with ID: " + documentReference.getId());

                        //FOR CHANGING ID OF VOUCHERS TO DOCUMENT REFERENCE
                        voucherCollection
                                .document(documentReference.getId())
                                .update("voucherId", documentReference.getId())
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
        voucherCollection
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
    public void update(String reference,  String column, Object newValues) {
        voucherCollection
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
