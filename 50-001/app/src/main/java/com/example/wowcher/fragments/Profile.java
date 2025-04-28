package com.example.wowcher.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.ClaimedRewardsActivity;
import com.example.wowcher.ClaimedVouchersActivity;
import com.example.wowcher.HelpActivity;
import com.example.wowcher.Login;
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

import java.util.ArrayList;
import java.util.List;

public class Profile extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    RecyclerView recyclerView;
    MyAdapter adapter;
    List<Voucher> ownedVouchers;
    FirebaseFirestore db;
    UserController userModel;
    VoucherController voucherModel;

    TextView userNameText; // Declare the TextView
    ConstraintLayout claimedVouchersLayout; // Declare the ConstraintLayout for "Claimed Vouchers"

    ConstraintLayout claimedRewardsLayout;

    Button btnlogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnlogout = view.findViewById(R.id.elevatedButton);

        if (user == null) {
            startActivity(new Intent(requireContext(), Login.class));
            requireActivity().finish();
        }

        recyclerView = view.findViewById(R.id.redeemedVouchersRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        ownedVouchers = new ArrayList<>();
        adapter = new MyAdapter(requireContext(), ownedVouchers);
        recyclerView.setAdapter(adapter);

        userNameText = view.findViewById(R.id.userNameText); // Bind the TextView
        claimedVouchersLayout = view.findViewById(R.id.constraintLayout2); // Bind the "Claimed Vouchers" section
        claimedRewardsLayout = view.findViewById(R.id.constraintLayout3); // Bind the "Claimed Vouchers" section

        db = FirebaseFirestore.getInstance();
        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        //Voucher
        DBSource voucherSourceInstance = new VoucherSource(db);
        voucherModel= new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        userModel.getUserInfoFromSource("userId", user.getUid());

        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User userObj) {
                if (userObj != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    String name = userObj.getUsername();
                    userNameText.setText(name != null ? name : "No Name Found");

                    if (!userObj.getRedeemedVouchers().isEmpty()){
                        voucherModel.getUserRedeemedVouchers("voucherId", userObj.getRedeemedVouchers());
                    }
                } else {
                    String displayName = user.getDisplayName();
                    userNameText.setText(displayName != null ? displayName : "No Name Found");
                    Toast.makeText(requireContext(), "Failed to load user name", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final Observer<ArrayList<Voucher>> voucherObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucherList) {
                if (voucherList != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    adapter.setSearchList(voucherList);
                } else {
                    Toast.makeText(requireContext(), "Failed to load redeemed vouchers", Toast.LENGTH_SHORT).show();
                }
            }
        };

        voucherModel.getAllVouchers().observe(getViewLifecycleOwner(), voucherObserver);
        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);

        // Handle "Claimed Vouchers" click
        claimedVouchersLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClaimedVouchersActivity.class);
            startActivity(intent);
        });

        // Handle "Claimed Vouchers" click
        claimedRewardsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClaimedRewardsActivity.class);
            startActivity(intent);
        });

        btnlogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        });

        // Inside onCreateView(...)
        TextView helpTextView = view.findViewById(R.id.help);
        helpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            startActivity(intent);
        });


        return view;
    }

}
