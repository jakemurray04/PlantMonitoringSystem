package com.cvsu.pms.plantmonitoringsystem;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    public static int spinnerRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open, R.string.Close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitleTextColor(0xFFFFFAFA);

        //Open environment immediately after app launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
            navigationView.setCheckedItem(R.id.menu_device);
            getSupportActionBar().setTitle("Device");
        }
        spinnerRes = android.R.layout.simple_spinner_dropdown_item;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_environment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EnvironmentFragment()).commit();
                getSupportActionBar().setTitle("Environment");
                break;
            case R.id.menu_scheduler:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SchedulerFragment()).commit();
                getSupportActionBar().setTitle("Scheduler");
                break;
            case R.id.menu_device:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                getSupportActionBar().setTitle("Device");
                break;
            case R.id.menu_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                getSupportActionBar().setTitle("About");
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
        else {
            super.onBackPressed();
        }
    }
}
