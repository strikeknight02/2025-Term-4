package com.example.wowcher;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Home;
import com.example.wowcher.fragments.Map;
import com.example.wowcher.fragments.Profile;
import com.example.wowcher.fragments.Vouchers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.wowcher.R;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.classes.User;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    boolean isDatabaseTesting = true;


    @RequiresApi(api = Build.VERSION_CODES.O)

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
