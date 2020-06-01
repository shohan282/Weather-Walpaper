package com.example.weatherwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherwallpaper.databinding.FragmentWallpaperBinding;


public class WallpaperFragment extends Fragment {

    SharedPreferences sharedPreferences;
    FragmentWallpaperBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
binding=FragmentWallpaperBinding.inflate(inflater,container,false);

        sharedPreferences= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);


        binding.myTextID.setText(sharedPreferences.getString("name","lol"));

        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }
}