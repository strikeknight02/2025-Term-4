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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
//        searchView = view.findViewById(R.id.search);

//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchList(newText);
//                return true;
//            }
//        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        dataList = new ArrayList<>();

        // Add sample data
        dataList.add(new Voucher(1, "Discount on Electronics", "Get 20% off on all electronics", "Active", 101, "2025-03-24"));
        dataList.add(new Voucher(2, "Free Coffee", "Buy one coffee, get one free", "Active", 102, "2025-03-20"));
        dataList.add(new Voucher(3, "Movie Ticket Discount", "Save $5 on any movie ticket", "Expired", 103, "2025-03-15"));
        dataList.add(new Voucher(4, "Gym Membership Offer", "First month free on new sign-ups", "Active", 104, "2025-03-10"));
        dataList.add(new Voucher(5, "Restaurant Discount", "15% off on dining bills above $50", "Redeemed", 105, "2025-03-18"));

        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

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
}