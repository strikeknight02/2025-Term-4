package com.example.wowcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.example.wowcher.db.VoucherSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClaimedVouchersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Voucher> dataList;
    ClaimedVoucherAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    UserController userModel;
    VoucherController voucherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimedvouchers);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView); // Get reference to RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Use 1 column grid layout for simplicity

        dataList = new ArrayList<>();
        //loadVouchersFromFirebase(); // Load vouchers when the activity starts

        // Bind the back icon and set the click listener
        ImageView backIcon = findViewById(R.id.wowcher_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });

        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Voucher
        DBSource voucherSourceInstance = new VoucherSource(db);
        voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        userModel.getUserInfoFromSource("userId", userId);

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {

                if(voucherList != null) {
                    if(!voucherList.isEmpty()){
                        dataList.clear();
                        adapter = new ClaimedVoucherAdapter(this, voucherList); // Initialize your adapter
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ClaimedVouchersActivity.this, "NO VOUCHERS REDEEMED", Toast.LENGTH_LONG).show();
                    }

                }
            }
        };

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if (user != null && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)){
                    ArrayList<String> redeemedVouchers = user.getRedeemedVouchers();

                    voucherModel.getUserRedeemedVouchers("voucherId",redeemedVouchers);
                    voucherModel.getAllVouchers().observe(ClaimedVouchersActivity.this, voucherObserver);
                }

            }
        };
        userModel.getUserInfo().observe(this, userObserver);
    }

}
