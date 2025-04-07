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
import com.example.wowcher.controller.VoucherController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

        db.collection("vouchers")
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

    }

    @Override
    public void delete(String reference) {

    }

    @Override
    public void update(String reference, String column, Object newValue) {

    }
}
