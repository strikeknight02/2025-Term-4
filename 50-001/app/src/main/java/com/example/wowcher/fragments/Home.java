package com.example.wowcher.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wowcher.MyAdapter;
import com.example.wowcher.R;
import com.example.wowcher.classes.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        dataList = new ArrayList<>();

        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadVouchersFromFirebase();

        return view;
    }

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
                            int locationId = document.getLong("locationId").intValue();

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