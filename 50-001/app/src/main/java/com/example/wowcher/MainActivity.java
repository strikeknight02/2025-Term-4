package com.example.wowcher;

import android.content.Intent;
import android.os.Build;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Home;
import com.example.wowcher.fragments.Map;
import com.example.wowcher.fragments.Profile;
import com.example.wowcher.fragments.Vouchers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean isDatabaseTesting = false;

    FirebaseAuth auth;
    FirebaseUser user;
    private static int SPLASH_SCREEN = 5000;

    //Animation variables
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;

    boolean isDatabaseTesting = true;


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
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If don't have, request for permissions
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.splash_screen_logo);
        logo = findViewById(R.id.splash_screen_text);
        slogan = findViewById(R.id.splash_screen_slogan);

        image.setAnimation(topAnim);
        image.setAnimation(bottomAnim);
        image.setAnimation(bottomAnim);

        // CAN CHANGE SCREEN AFTER SPLASH (change Dashboard,class)
        /*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);*/


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
