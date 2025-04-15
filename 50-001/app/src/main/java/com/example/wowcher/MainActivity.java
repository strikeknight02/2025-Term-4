package com.example.wowcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.wowcher.fragments.Vouchers;
import com.example.wowcher.fragments.Map;
import com.example.wowcher.fragments.Profile;
import com.example.wowcher.fragments.Rewards;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean isDatabaseTesting = false;

    FirebaseAuth auth;
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new Vouchers();
        Fragment mapFragment = new Map();
        Fragment voucherFragment = new Rewards();
        Fragment profileFragment = new Profile();

        setCurrentFragment(mapFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.map) {
                setCurrentFragment(mapFragment);

            } else if (itemId == R.id.voucher) {
                setCurrentFragment(homeFragment);

            } else if (itemId == R.id.rewards) {
                setCurrentFragment(voucherFragment);

            } else if (itemId == R.id.profile) {
                setCurrentFragment(profileFragment);
            }

            return true;
        });

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }

        if (isDatabaseTesting) {
            Intent databaseIntent = new Intent(MainActivity.this, DBTestActivity.class);
            startActivity(databaseIntent);
        }
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}