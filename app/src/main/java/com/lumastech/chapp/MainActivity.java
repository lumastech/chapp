package com.lumastech.chapp;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.lumastech.chapp.databinding.ActivityMainBinding;
import com.lumastech.chapp.ui.home.HomeFragment;
import com.lumastech.chapp.ui.profile.ProfileFragment;

import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentNav {
    NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding, bindingHome;
    public  Utility utility;
    TextView centersTextView, emergenceTextView, myProfileTextView, mapsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utility = new Utility(this);
        utility.writeToFile("notnew", "notnew");
//        centersTextView = findViewById(R.id.centers);
//        emergenceTextView = findViewById(R.id.emergence);
//        myProfileTextView = findViewById(R.id.my_profile);
//        mapsTextView = findViewById(R.id.maps);

//        TextView dName = binding.drawerLayout.findViewById(R.id.d_name);
//        TextView dEmail = binding.drawerLayout.findViewById(R.id.d_email);
//
//        dName.setText(getValueFromJsom("name"));
//        dEmail.setText(getValueFromJsom("email"));


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               utility.sendSos();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_maps, R.id.nav_bmi, R.id.nav_health_centers, R.id.nav_sos_settings, R.id.nav_settings, R.id.my_profile)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onButtonClicked(int id) {
        navController.navigate(id);
    }

    public String getValueFromJsom(String string){
        String value = "";
        try {
            String profileText = utility.readFile("user");
            if(!Objects.equals(profileText, "null")) {
                JSONObject userObject = new JSONObject(profileText);
                value = userObject.getString(string);
            }
        }catch (Exception e){
            utility.generalDialog(e.getLocalizedMessage());
        }

        return  value;
    }
}