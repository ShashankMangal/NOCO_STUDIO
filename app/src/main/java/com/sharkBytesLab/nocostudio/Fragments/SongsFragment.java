package com.sharkBytesLab.nocostudio.Fragments;


import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.musicFiles;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharkBytesLab.nocostudio.Adapters.MusicAdapter;
import com.sharkBytesLab.nocostudio.R;

public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    public static MusicAdapter musicAdapter;
    private ConstraintLayout emptySongsLayout;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_songs);
        emptySongsLayout = view.findViewById(R.id.empty_songs);

        recyclerView.setHasFixedSize(true);
        if (!musicFiles.isEmpty()) {
            Log.i("GetAllAudio", "inside non empty");
            emptySongsLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.i("GetAllAudio", "IF");
            try {
                musicAdapter = new MusicAdapter(getContext(), musicFiles);
                recyclerView.setAdapter(musicAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                Log.i("Music Files :", String.valueOf(musicFiles.size()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Log.i("GetAllAudio", "inside empty");
            emptySongsLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SongsFrag", "Resume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("SongsFrag", "ViewState");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("SongsFrag", "Viewcreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("SongsFrag", "Start");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("SongsFrag", "pause");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("SongsFrag", "Attach");
    }
}
