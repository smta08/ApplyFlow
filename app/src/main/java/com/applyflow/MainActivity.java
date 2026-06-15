package com.applyflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            Set<Integer> topLevel = new HashSet<>();
            topLevel.add(R.id.dashboardFragment);
            topLevel.add(R.id.applicationListFragment);
            topLevel.add(R.id.statisticsFragment);
            topLevel.add(R.id.settingsFragment);
            appBarConfiguration = new AppBarConfiguration.Builder(topLevel).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                    bottomNav.setVisibility(isTopLevel(destination) ? View.VISIBLE : View.GONE));
        }

        requestNotificationPermissionIfNeeded();
    }

    private boolean isTopLevel(@NonNull NavDestination destination) {
        int id = destination.getId();
        return id == R.id.dashboardFragment
                || id == R.id.applicationListFragment
                || id == R.id.statisticsFragment
                || id == R.id.settingsFragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null
                && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
