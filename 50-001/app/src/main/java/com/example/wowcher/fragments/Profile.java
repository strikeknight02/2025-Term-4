package com.example.wowcher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        db = FirebaseFirestore.getInstance();
        loadUserVouchers();

        return view;
    }

    private void loadUserVouchers() {
        if (user == null) return;

        String userId = user.getUid();

        db.collection("user_vouchers")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<Integer> voucherIds = new HashSet<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Long vId = doc.getLong("voucherId");
                        if (vId != null) voucherIds.add(vId.intValue());
                    }

                    fetchVouchersById(voucherIds);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load user vouchers", Toast.LENGTH_SHORT).show());
    }

    private void fetchVouchersById(Set<Integer> voucherIds) {
        db.collection("vouchers")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ownedVouchers.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String voucherId = doc.getString("voucherId");
                        if (voucherId != null && voucherIds.contains(voucherId)) {
                            String title = doc.getString("title");
                            String details = doc.getString("details");
                            String status = doc.getString("status");
                            String createdAt = doc.getString("createdAt");
                            int locationId = doc.getLong("locationId").intValue();

                            Voucher voucher = new Voucher(voucherId, title, details, status, locationId, createdAt);
                            ownedVouchers.add(voucher);
                        }
                    }

                    // Toggle visibility
                    RecyclerView recyclerView = requireView().findViewById(R.id.redeemedVouchersRecyclerView);
                    View noVouchersText = requireView().findViewById(R.id.noVouchersText);

                    if (ownedVouchers.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        noVouchersText.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        noVouchersText.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show());
    }

}
