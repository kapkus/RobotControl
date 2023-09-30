package com.pl.edu.prz.robotui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.pl.edu.prz.robotui.control.panel.ControlFragment;

import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BluetoothFragment.MyInterface {

    SharedPreferences sp;
    private DrawerLayout drawer;
    private ControlFragment controlFragment;
    private BluetoothFragment bluetoothFragment;
    private SettingsFragment settingsFragment;
    private InfoFragment infoFragment;
    private OutputStream outputStream;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check dark theme
        sp = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Boolean darkTheme = sp.getBoolean("darkTheme", false);
        System.out.println(darkTheme);
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        controlFragment = new ControlFragment();
        bluetoothFragment = new BluetoothFragment();
        settingsFragment = new SettingsFragment();
        infoFragment = new InfoFragment();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, controlFragment).addToBackStack(null).commit();
            navigationView.setCheckedItem(R.id.nav_panel);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_panel:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, controlFragment, "panel")
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_bluetooth:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, bluetoothFragment)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, infoFragment)
                        .addToBackStack(null).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void passStreams(OutputStream os, InputStream is) {
        outputStream = os;
        inputStream = is;
    }

    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
