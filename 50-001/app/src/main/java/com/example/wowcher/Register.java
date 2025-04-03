package com.example.wowcher;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.classes.User;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity{
    private EditText nameField, emailField, passwordField, confirmPasswordField, mobileField;
    private Button registerButton;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;  // Firestore database instance

    // Initialise the ViewModel.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        //Firebase Object Initialisation
        FirebaseFirestore db = FirebaseFirestore.getInstance();




        //Initial Database Call and setup Listener
//        userModel.getUserInfoFromSource("username", "Speedy");

        nameField = findViewById(R.id.name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirmPassword);
        mobileField = findViewById(R.id.mobile);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    private void registerUser() {

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        String mobile = mobileField.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //DBSource Initialisation
                                 User newUser = new User(
                                        "",  // userId
                                        name,  // username
                                        email,  // email
                                        password,  // password
                                        mobile,  // mobileNumber
                                        "User",  // role
                                        "Bronze",  // tier
                                        0,  // points
                                        LocalDateTime.now().toString(),  // createdAt
                                        5,  // availableVouchers
                                        2   // previousVouchers
                                );


                            }


                        }
                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveUserToDatabase(String userId, String name, String email, String mobile) {
        // Creating a user object
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("mobile", mobile);
        user.put("tier", "Bronze"); // Default tier
        user.put("points", 0); // Default points
        user.put("role", "User"); // Default role

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(Register.this, "Error saving user data.", Toast.LENGTH_SHORT).show());
    }
}
