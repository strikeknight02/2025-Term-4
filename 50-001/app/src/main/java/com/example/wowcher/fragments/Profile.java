package com.example.wowcher.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.ClaimedVouchersActivity;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null){
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

        db = FirebaseFirestore.getInstance();

        //User
        DBSource userSourceInstance = new UserSource(db);
        userModel= new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        userModel.getUserInfoFromSource("userId", user.getUid());

        //loadUserVouchers();
        final Observer<User> userObserver = new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (user !=null)){

                    String name = user.getUsername();
                    userNameText.setText(name != null ? name : "No Name Found");

                    ArrayList<Voucher> redeemedVouchers = user.getRedeemedVouchers();
                    // Toggle visibility
                    RecyclerView recyclerView = requireView().findViewById(R.id.redeemedVouchersRecyclerView);
                    View noVouchersText = requireView().findViewById(R.id.noVouchersText);

                    ownedVouchers.clear();
                    ownedVouchers.addAll(redeemedVouchers);

                    if(!redeemedVouchers.isEmpty()){
                        recyclerView.setVisibility(View.VISIBLE);
                        noVouchersText.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noVouchersText.setVisibility(View.VISIBLE);
                    }


                } else {
                    userNameText.setText("User not found");
                    Toast.makeText(requireContext(), "Failed to load user", Toast.LENGTH_SHORT).show();
                }

            }
        };

        userModel.getUserInfo().observe(getViewLifecycleOwner(), userObserver);

        // Set the click listener for the "Claimed Vouchers" section
        claimedVouchersLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClaimedVouchersActivity.class);
            startActivity(intent);
        });

        return view;
    }

}
