package com.example.eventhubtfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventhubtfg.databinding.ActivityMainOrgBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityOrg extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainOrgBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainOrgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainOrg.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navViewOrg;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_org, R.id.nav_perfil, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_org);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem logoutMenuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        logoutMenuItem.setOnMenuItemClickListener(item -> {
            try {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivityOrg.this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivityOrg.this, LoginActivity.class));
                finish();
            } catch (Exception e) {
                Toast.makeText(MainActivityOrg.this, "Error al cerrar sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        binding.appBarMainOrg.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityOrg.this, CreateEvent.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_org, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_org);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
