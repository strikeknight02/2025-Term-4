package com.example.wowcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText nameField, emailField, passwordField, confirmPasswordField, mobileField;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;

    private UserController userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        //Initialize UserController ViewModel
        DBSource userSourceInstance = new UserSource(fstore);
        userModel = new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

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

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userID = firebaseUser.getUid();

                                DocumentReference documentReference = fstore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("username", name);
                                user.put("email", email);
                                user.put("mobileNumber", mobile);
                                user.put("role", "User");
                                user.put("tier", "Bronze");
                                user.put("totalPoints", 0);
                                user.put("currentPoints", 0);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    User newUser = new User("", name, email, password, mobile, "User", "Bronze", 0, 0, LocalDateTime.now().toString(), new ArrayList<String>() );
                                    userModel.addUser(newUser);
                                    Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                }

                                //OBSOLETE
//                                DocumentReference documentReference = fstore.collection("users").document(userID);
//                                Map<String, Object> user = new HashMap<>();
//                                user.put("username", name);
//                                user.put("email", email);
//                                user.put("mobileNumber", mobile);
//                                user.put("role", "User");
//                                user.put("tier", "Bronze");
//                                user.put("points", 0);
//                                user.put("createdAt", LocalDateTime.now().toString());
//                                user.put("availableVouchers", new ArrayList<String>());
//                                user.put("previousVouchers", new ArrayList<String>());
//                                user.put("password", password);
//
//                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("NO REGISTER", e.toString());
                    }
                });
    }
}
