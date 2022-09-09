package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sharkBytesLab.nocostudio.Models.DownloadModel;
import com.sharkBytesLab.nocostudio.Utilities.PreferenceManager;
import com.sharkBytesLab.nocostudio.databinding.ActivityFavouriteScreenBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavouriteScreen extends AppCompatActivity
{
    private ActivityFavouriteScreenBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<DownloadModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        loadList();

        try {
            Log.e("FAV", String.valueOf(list.get(0).getTitle()));
        } catch (Exception e) {
            Log.e("Fav error", e.getMessage());
        }

    }

    private void init()
    {
        preferenceManager = new PreferenceManager(this);
        list = new ArrayList<>();
    }
    private void loadList()
    {
        Gson gson = new Gson();
        String json = preferenceManager.getString("favList");
        Type type = new TypeToken<ArrayList<DownloadModel>>() {}.getType();
        list = gson.fromJson(json, type);

    }

}