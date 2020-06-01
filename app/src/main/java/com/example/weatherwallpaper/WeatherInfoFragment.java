package com.example.weatherwallpaper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.weatherwallpaper.databinding.FragmentWallpaperBinding;
import com.example.weatherwallpaper.databinding.FragmentWeatherInfoBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.core.content.ContextCompat.getSystemService;


public class WeatherInfoFragment extends Fragment {

    private static final int PERMISSION_ID = 1234;
    int lat, lon;
    FragmentWeatherInfoBinding binding;
    FusedLocationProviderClient mFusedLocationClient;
    Retrofit retrofit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherInfoBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setTime();
    }

    private void getAllWeaterData(float lat, float lon) {

        retrofit = ClientApiInstance.getInstance();

        WeatherApiCall apiCall = retrofit.create(WeatherApiCall.class);
        Call<WeatherInfo> call = apiCall.getWeatherData(lat, lon, "5b76939fef2379df448ba7473b209f9e");
        call.enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                if (response.body() != null) {
                    setWeatherInfo(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {

            }
        });
    }

    private void setWeatherInfo(WeatherInfo body) {

        if (body.getWeather().get(0).getMain().toLowerCase().equals("haze")) {
            Glide.with(getContext()).load(R.drawable.foggy).into(binding.weatherImageID);

        } else if (body.getWeather().get(0).getMain().toLowerCase().equals("clouds")) {
            Glide.with(getContext()).load(R.drawable.cloud).into(binding.weatherImageID);


        } else if (body.getWeather().get(0).getMain().toLowerCase().equals("rain")) {
            Glide.with(getContext()).load(R.drawable.ico).into(binding.weatherImageID);


        } else {
            Glide.with(getContext()).load(R.drawable.sunny).into(binding.weatherImageID);

        }

        binding.weatherStatusID.setText(body.getWeather().get(0).getMain());
        binding.weatherDescriptionID.setText(body.getWeather().get(0).getDescription());
        binding.weatherTempID.setText(Math.abs(body.getMain().getTemp()-273)+"");
        binding.weatherHumidityID.setText(body.getMain().getHumidity());
        binding.weatherPresssureID.setText(body.getMain().getPressure());


    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    getAllWeaterData(((int) location.getLatitude()), ((int) location.getLongitude()));
                                   /* lat = (int) location.getLatitude();
                                    lon = (int) location.getLongitude();*/

                                }
                            }
                        }
                );
            } else {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == getActivity().getPackageManager().PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == getActivity().getPackageManager().PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == getActivity().getPackageManager().PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    public void setTime() {

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                getLastLocation();

                ha.postDelayed(this, 3000);
            }
        }, 3000);
    }

}