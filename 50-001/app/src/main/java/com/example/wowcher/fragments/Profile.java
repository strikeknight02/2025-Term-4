package com.example.wowcher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.ClaimedVouchersActivity;
import com.example.wowcher.Login;
import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.classes.Voucher;
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

    TextView userNameText; // Declare the TextView
    ConstraintLayout claimedVouchersLayout; // Declare the ConstraintLayout for "Claimed Vouchers"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

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

        db = FirebaseFirestore.getInstance();
        loadUserName(); // Load the user's name
//        loadUserVouchers(); // Load vouchers as before

        // Set the click listener for the "Claimed Vouchers" section
        claimedVouchersLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClaimedVouchersActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserName() {
        if (user == null) return;

        String userId = user.getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        userNameText.setText(name != null ? name : "No Name Found");
                    } else {
                        userNameText.setText("User not found");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load user name", Toast.LENGTH_SHORT).show());
    }

}
