package com.example.wowcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
}