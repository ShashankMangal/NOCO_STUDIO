package com.sharkBytesLab.nocostudio.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharkBytesLab.nocostudio.Adapters.DownloadSongAdapter;
import com.sharkBytesLab.nocostudio.Models.DownloadModel;
import com.sharkBytesLab.nocostudio.databinding.ActivityFavouriteScreenBinding;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavouriteScreen extends AppCompatActivity
{
    private ActivityFavouriteScreenBinding binding;
    private ArrayList<String> favLists;
    private Set<String> favSongs;
    private DatabaseReference myRef;
    private ArrayList<DownloadModel> list;
    private DownloadSongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        loadData();

        if(favLists.isEmpty())
        {
            binding.favouriteSongsRecyclerView.setVisibility(View.GONE);
            binding.emptyFavListImage.setVisibility(View.VISIBLE);
            binding.emptyFavListText.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.favouriteSongsRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyFavListImage.setVisibility(View.GONE);
            binding.emptyFavListText.setVisibility(View.GONE);

            favSongs.addAll(favLists);
            getDataFromFirebase();

        }

        binding.favouriteSongImageBack.setOnClickListener(v->onBackPressed());
        binding.favouriteSongInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(binding.favLayout, "Add Songs from server into your favourite list to search and access them faster. ", Snackbar.LENGTH_LONG)
                        .show();
            }
        });


    }

    private void init()
    {
        favLists = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        favSongs = new HashSet<>();
        binding.favouriteSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.favouriteSongsRecyclerView.setHasFixedSize(true);

    }

    private void loadData()
    {

        SharedPreferences sharedPreference = getSharedPreferences("fav", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreference.getString("favList", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        favLists = gson.fromJson(json, type);

        if(favLists == null)
        {
            favLists = new ArrayList<>();
        }

    }

    public void getDataFromFirebase()
    {

                try {

                    Query query = myRef.child("DownloadData");
                    query.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            clearAll();


                            for(DataSnapshot snapshot1 : snapshot.getChildren())
                            {

                                DownloadModel model = new DownloadModel();
                                model.setImage(snapshot1.child("image").getValue().toString());
                                model.setTitle(snapshot1.child("title").getValue().toString());
                                model.setLink(snapshot1.child("link").getValue().toString());
                                model.setVideoId(snapshot1.child("videoId").getValue().toString());

                                if(favSongs.contains(snapshot1.child("title").getValue().toString()))
                                {
                                    Log.e("Name", snapshot1.child("title").getValue().toString());
                                    list.add(model);
                                    Log.e("List added", "true");
                                }

                            }

                            adapter = new DownloadSongAdapter(FavouriteScreen.this, list);
                            binding.favouriteSongsRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    private void clearAll()
    {
        if(list != null)
        {
            list.clear();

            if(adapter != null)
            {
                adapter.notifyDataSetChanged();
            }

        }

        list = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }
}