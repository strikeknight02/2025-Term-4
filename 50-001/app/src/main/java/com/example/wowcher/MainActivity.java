package com.example.wowcher;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.classes.User;
import com.example.wowcher.controller.UserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserController model;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Firebase Object Initialisation
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the ViewModel.
        model = new ViewModelProvider(this).get(UserController.class);

        //Initial Database Call and setup Listener
        model.getAllFromDB(db, model);

        //Observer for User List
        model.getAll().observe(this, new Observer<ArrayList<User>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<User> newName) {
                // Update the UI, in this case, a TextView.
                mainText = findViewById(R.id.mainText);
                String mText = "";
                for (User u: newName){
                    mText += u.username + "\n";
                }
                mainText.setText(mText);
            }

        });

    }
}