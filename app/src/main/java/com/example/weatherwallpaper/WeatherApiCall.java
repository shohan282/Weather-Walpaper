package com.example.weatherwallpaper;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiCall {

    @GET("data/2.5/weather")
    Call<WeatherInfo>getWeatherData(
            @Query("lat") float lat,
            @Query("lon") float lon,
            @Query("appid")String appid

    );

}
