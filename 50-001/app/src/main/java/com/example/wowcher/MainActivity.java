package com.example.wowcher;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Home;
import com.example.wowcher.fragments.Map;
import com.example.wowcher.fragments.Profile;
import com.example.wowcher.fragments.Vouchers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.wowcher.R;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new Home();
        Fragment mapFragment = new Map();
        Fragment voucherFragment = new Vouchers();
        Fragment profileFragment = new Profile();

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                setCurrentFragment(homeFragment);
            } else if (itemId == R.id.map) {
                setCurrentFragment(mapFragment);
            } else if (itemId == R.id.voucher) {
                setCurrentFragment(voucherFragment);
            } else if (itemId == R.id.profile) {
                setCurrentFragment(profileFragment);
            }

            return true;
        });

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If don't have, request for permissions
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}
