package com.example.weatherwallpaper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientApiInstance {

    private static final String BASE_URL ="http://api.openweathermap.org/" ;
    private static Retrofit retrofit =null;

    public static Retrofit getInstance()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
