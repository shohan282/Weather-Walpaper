package com.example.weatherwallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.weatherwallpaper.databinding.ActivityWeatherBinding;

public class WeatherActivity extends AppCompatActivity {
    ActivityWeatherBinding binding;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WeatherInfoFragment(),"Weather");
        adapter.addFragment(new WallpaperFragment(),"Wallpaper");
        binding.viewPageID.setAdapter(adapter);
        binding.tabLayoutID.setupWithViewPager(binding.viewPageID);

    }
}