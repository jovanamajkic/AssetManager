package com.example.assetsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.assetsmanager.util.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assetsmanager.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_assets, R.id.nav_employees, R.id.nav_locations, R.id.nav_inventories)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.addAssetFragment ||
                    destination.getId() == R.id.addEmployeeFragment ||
                    destination.getId() == R.id.addLocationFragment ||
                    destination.getId() == R.id.addInventoryFragment ||
                    destination.getId() == R.id.assetDetailsFragment
            ) {
                binding.appBarMain.fab.setVisibility(View.GONE);
            } else {
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
            }
        });

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);

                int currentDestination = navController.getCurrentDestination().getId();

                if (currentDestination == R.id.nav_assets) {
                    navController.navigate(R.id.action_nav_assets_to_addAssetFragment);
                } else if (currentDestination == R.id.nav_employees) {
                    navController.navigate(R.id.action_nav_employees_to_addEmployeeFragment);
                } else if (currentDestination == R.id.nav_locations) {
                    navController.navigate(R.id.action_nav_locations_to_addLocationFragment);
                } else if (currentDestination == R.id.nav_inventories) {
                    navController.navigate(R.id.action_nav_inventories_to_addInventoryFragment);
                }
            }
        });

        Log.d("MyApp", "MainActivity started");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.settingsFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String lang = shPreferences.getString(Constants.SELECTED_LANGUAGE, Locale.getDefault().getLanguage());

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        newBase.getResources().updateConfiguration(config,
                newBase.getResources().getDisplayMetrics());

        super.attachBaseContext(newBase);
    }
}