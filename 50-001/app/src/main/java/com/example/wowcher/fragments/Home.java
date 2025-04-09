package com.example.wowcher.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home extends Fragment {


    RecyclerView recyclerView;
    List<Voucher> dataList;
    MyAdapter adapter;
    SearchView searchView;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    UserController userModel;
    VoucherController voucherModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vouchers, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        db = FirebaseFirestore.getInstance();
        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Voucher
        DBSource voucherSourceInstance = new VoucherSource(db);
        voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        userModel.getUserInfoFromSource("userId", user.getUid());

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {

                if(voucherList != null){
                    if (!voucherList.isEmpty()) {
                        adapter = new MyAdapter(requireContext(), voucherList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(requireContext(), "No Voucher to Load", Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };


        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (user !=null)){
                    ArrayList<String> redeemedVouchers = user.getPreviousVouchers();
                    voucherModel.getVouchersforAll(redeemedVouchers);
                    voucherModel.getAllVouchers().observe(getViewLifecycleOwner(), voucherObserver);
                }

            }
        };

        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);

        //OBSOLETE
        //loadUserVouchers();
        return view;
    }

    //OBSOLETE
    private void searchList(String text) {
        List<Voucher> dataSearchList = new ArrayList<>();
        for (Voucher data : dataList) {
            if (data.getTitle().toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }
    //OBSOLETE
    private void loadVouchersFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vouchers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String voucherId = document.getString("voucherId");
                            String title = document.getString("title");
                            String details = document.getString("details");
                            String status = document.getString("status");
                            String createdAt = document.getString("createdAt");
                            String locationId = document.getString("locationId");

                            Voucher voucher = new Voucher(voucherId, title, details, status, locationId, createdAt);
                            dataList.add(voucher);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}