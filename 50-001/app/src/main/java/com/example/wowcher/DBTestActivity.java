package com.example.wowcher;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.UserSource;
import com.example.wowcher.db.VoucherSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DBTestActivity extends AppCompatActivity {

    TextView nameText;
    TextView roleText;
    TextView createdAtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_testing);

        LinearLayout rootView = findViewById(R.id.root_view);

        //Firebase Object Initialisation (NEED IN ACTIVITY)
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //DBSource Initialisation (NEED IN ACTIVITY)
        DBSource voucherSourceInstance = new VoucherSource(db);

        // Initialise the ViewModel. (NEED IN ACTIVITY)
        VoucherController voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        //Initial Database Call and setup Listener (NEED DEPENDING ON WHAT YOU NEED)
        voucherModel.getVouchersforAll();

        //Voucher newVoucher = new Voucher(3, "20% OFF TOFU", "50% of all tofu at the tofu shop", "Redeemed?", 2, "1am");

        //voucherModel.addVoucher(newVoucher);

        //Observer for User List (NEED DEPENDING ON WHAT YOU NEED)
        voucherModel.getAllVouchers().observe(this, new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucher) {
                // Update the UI, in this case, a TextView.
                Log.d("ERROR Check", voucher.toString());

                for (int i = 0; i < voucher.size(); i++) {
                    TextView wordView = new TextView(getApplicationContext());
                    wordView.setText(voucher.get(i).getTitle());
                    rootView.addView(wordView);
                }

//                nameText = findViewById(R.id.nameText);
//                roleText = findViewById(R.id.roleText);
//                createdAtText = findViewById(R.id.createdAtText);
//
//                nameText.setText(voucher);
//                roleText.setText(voucher);
//                createdAtText.setText(voucher);
            }
        });

        //-----------------------TESTING BELOW
        //User newUser = new User("", "Speedy", "Customer", LocalDateTime.now().toString(), 0, 0);

        //Add Test
        //userModel.addUser(newUser);

        //userModel.updateUser(user.getUserId(), "role", "Admin");
    }

}
