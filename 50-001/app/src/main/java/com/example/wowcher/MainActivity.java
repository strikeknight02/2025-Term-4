package com.example.wowcher;

import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Home;
import com.example.wowcher.fragments.Map;
import com.example.wowcher.fragments.Profile;
import com.example.wowcher.fragments.Vouchers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

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
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish(); // Correct way to finish the activity from a fragment
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new Home();
        Fragment mapFragment = new Map();
        Fragment voucherFragment = new Vouchers();
        Fragment profileFragment = new Profile();

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.map) {
                setCurrentFragment(mapFragment);
            } else if (itemId == R.id.voucher) {
                setCurrentFragment(voucherFragment);
            } else if (itemId == R.id.rewards) {
                setCurrentFragment(homeFragment);
            } else if (itemId == R.id.profile) {
                setCurrentFragment(profileFragment);
            }

            return true;
        });

        if(isDatabaseTesting){
            Intent databaseIntent = new Intent(MainActivity.this, DBTestActivity.class);
            startActivity(databaseIntent);
        }

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}
