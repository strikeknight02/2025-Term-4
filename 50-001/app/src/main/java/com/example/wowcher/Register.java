package com.example.wowcher;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

public class Register extends AppCompatActivity{
//    private EditText nameField, emailField, passwordField, confirmPasswordField, mobileField;
//    private Button registerButton;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        nameField = findViewById(R.id.name);
//        emailField = findViewById(R.id.email);
//        passwordField = findViewById(R.id.password);
//        confirmPasswordField = findViewById(R.id.confirmPassword);
//        mobileField = findViewById(R.id.mobile);
//        registerButton = findViewById(R.id.registerButton);
//
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registerUser();
//            }
//        });
//    }
//
//    private void registerUser() {
//        String name = nameField.getText().toString().trim();
//        String email = emailField.getText().toString().trim();
//        String password = passwordField.getText().toString().trim();
//        String confirmPassword = confirmPasswordField.getText().toString().trim();
//        String mobile = mobileField.getText().toString().trim();
//
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(mobile)) {
//            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                        finish();
//                    } else {
//                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
}
