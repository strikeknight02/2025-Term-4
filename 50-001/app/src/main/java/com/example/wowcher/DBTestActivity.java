package com.example.wowcher;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.classes.User;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DBTestActivity extends AppCompatActivity {

    TextView nameText;
    TextView roleText;
    TextView createdAtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_testing);

        //Firebase Object Initialisation
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //DBSource Initialisation
        DBSource userSourceInstance = new UserSource(db);

        // Initialise the ViewModel.
        UserController userModel = new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        User newUser = new User(
                "12345",  // userId
                "john_doe",  // username
                "john.doe@example.com",  // email
                "securepassword123",  // password
                "9876543210",  // mobileNumber
                "User",  // role
                "Gold",  // tier
                150,  // points
                LocalDateTime.now().toString(),  // createdAt
                5,  // availableVouchers
                2   // previousVouchers
        );
        userModel.addUser(newUser);

        //Initial Database Call and setup Listener
        userModel.getUserInfoFromSource("username", "john_doe");

        //Observer for User List
        userModel.getUserInfo().observe(this, new Observer<User> () {
            @Override
            public void onChanged(@Nullable final User user) {
                // Update the UI, in this case, a TextView.
                Log.d("ERROR Check", user.getUsername());
                nameText = findViewById(R.id.nameText);
                roleText = findViewById(R.id.roleText);
                createdAtText = findViewById(R.id.createdAtText);

                nameText.setText(user.getUsername());
                roleText.setText(user.getRole());
                createdAtText.setText(user.getCreatedAt());
            }

        });

        //-----------------------TESTING BELOW
        //        Add Test
//        User newUser = new User(
//                "12345",  // userId
//                "john_doe",  // username
//                "john.doe@example.com",  // email
//                "securepassword123",  // password
//                "9876543210",  // mobileNumber
//                "User",  // role
//                "Gold",  // tier
//                150,  // points
//                LocalDateTime.now().toString(),  // createdAt
//                5,  // availableVouchers
//                2   // previousVouchers
//        );
//        userModel.addUser(newUser);

//        userModel.updateUser(user.getUserId(), "role", "Admin");  //User newUser = new User("", "Speedy", "Customer", LocalDateTime.now().toString(), 0, 0);


    }

}
