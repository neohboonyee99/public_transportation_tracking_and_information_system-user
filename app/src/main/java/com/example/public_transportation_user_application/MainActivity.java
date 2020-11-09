package com.example.public_transportation_user_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    BottomNavigationView bottomNavView;
    CustomPageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.userVP);
        bottomNavView = findViewById(R.id.bottomNav);
        bottomNavView.setOnNavigationItemSelectedListener(this);
        adapter = new CustomPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.routePlanning:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nearByBusStop:
                viewPager.setCurrentItem(1);
                break;
            case R.id.routeNumber:
                viewPager.setCurrentItem(2);
                break;
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch(position){
            case 0:
                bottomNavView.setSelectedItemId(R.id.routePlanning);
                break;
            case 1:
                bottomNavView.setSelectedItemId(R.id.nearByBusStop);
                break;
            case 2:
                bottomNavView.setSelectedItemId(R.id.routeNumber);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class CustomPageAdapter extends FragmentStatePagerAdapter {
        private final int NUM_ITEMS = 3;
        Fragment page;

        CustomPageAdapter(FragmentManager fm){
            super(fm);

        }

        public Fragment getCurrentFragment(){
            return page;
        }

        @Override
        public Fragment getItem(int i){
            switch(i){
                case 0:
                    page = new MapsFragment();
                    return page;
                case 1:
                    page = new NearbyBusStopETAFragment();
                    return page;
                case 2:
                    page = new RouteNumberETAFragment();
                    return page;
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}