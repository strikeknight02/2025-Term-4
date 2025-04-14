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

import com.example.wowcher.classes.Location;
import com.example.wowcher.classes.User;
import com.example.wowcher.classes.Voucher;
import com.example.wowcher.controller.LocationController;
import com.example.wowcher.controller.LocationControllerFactory;
import com.example.wowcher.controller.UserController;
import com.example.wowcher.controller.UserControllerFactory;
import com.example.wowcher.controller.VoucherController;
import com.example.wowcher.controller.VoucherControllerFactory;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.LocationSource;
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

    ArrayList<Location> locationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_testing);

        LinearLayout rootView = findViewById(R.id.root_view);

        //Firebase Object Initialisation (NEED IN ACTIVITY)
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //DBSource Initialisation (NEED IN ACTIVITY)
        DBSource voucherSourceInstance = new VoucherSource(db);
        DBSource locationSourceInstance = new LocationSource(db);
        DBSource userSourceInstance = new UserSource(db);

        // Initialise the ViewModel. (NEED IN ACTIVITY)
        VoucherController voucherModel = new ViewModelProvider(this, new VoucherControllerFactory(voucherSourceInstance)).get(VoucherController.class);
        voucherModel.getModelInstance(voucherModel);

        LocationController locationModel = new ViewModelProvider(this, new LocationControllerFactory(locationSourceInstance)).get(LocationController.class);
        locationModel.getModelInstance(locationModel);

        UserController userModel = new ViewModelProvider(this, new UserControllerFactory(userSourceInstance)).get(UserController.class);
        userModel.getModelInstance(userModel);

        ArrayList<String> redeemedVouchers= new ArrayList<>();
        redeemedVouchers.add("3bcaMDLohBFKWZ2Zglxg");
        redeemedVouchers.add("UnXzipX5lKm7kaDbBscq");

        //Initial Database Call and setup Listener (NEED DEPENDING ON WHAT YOU NEED)
        locationModel.getLocationforAll();

        final Observer<ArrayList<Voucher>> voucherListObserver = new Observer<ArrayList<Voucher>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Voucher> voucher) {
                // Update the UI, in this case, a TextView.
                Log.d("ERROR Check", voucher.toString());
                rootView.removeAllViews();
                for(int j=0; j< voucher.size(); j++){
                    TextView wordView = new TextView(getApplicationContext());
                    wordView.setText(voucher.get(j).getTitle());
                    rootView.addView(wordView);
                }
            }
        };

        final Observer<ArrayList<Location>> locationsListObserver = new Observer<ArrayList<Location>> () {
            @Override
            public void onChanged(@Nullable final ArrayList<Location> locations) {
                // Update the UI, in this case, a TextView.
                Log.d("ERROR Check", locations.toString());
                ArrayList<String> locationIds = new ArrayList<>();
                for (Location location: locations) {
                    Log.i("LOOP CHECK", location.getLocationId());
                    locationIds.add(location.getLocationId());
                }

                voucherModel.getVouchersBasedOnLocation(locationIds);

            }
        };

        //Observer for User List (NEED DEPENDING ON WHAT YOU NEED)
        locationModel.getAllLocations().observe(this, locationsListObserver);
        voucherModel.getVouchersBasedOnLocation().observe(this, voucherListObserver);


//        userModel.getUserInfoFromSource("username", "test1");
//
//        final Observer<User> userInfoObserver = new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                TextView wordView = new TextView(getApplicationContext());
//                wordView.setText(user.getRedeemedVouchers().toString());
//                rootView.addView(wordView);
//            }
//        };
//
//        userModel.getUserInfo().observe(this, userInfoObserver);
        //-----------------------TESTING BELOW
        //User newUser = new User("", "Speedy", "Customer", LocalDateTime.now().toString(), 0, 0);

        //Add Test
        //userModel.addUser(newUser);

        //userModel.updateUser(user.getUserId(), "role", "Admin");
    }

}
