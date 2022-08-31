package com.sharkBytesLab.nocostudio.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.sharkBytesLab.nocostudio.Adapters.MusicListAdapter;
import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.Models.AudioModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.AllPerksScreen;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView recyclerView;
    private TextView noMusicTextView;
    private LottieAnimationView not_found;
    private RelativeLayout layout;
    private ArrayList<AudioModel> songsList = new ArrayList<>();
    private ImageView perk_imageView;
    private AdView adView;


    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_music, container, false);

        recyclerView = view.findViewById(R.id.music_recycler_view);
        noMusicTextView = view.findViewById(R.id.no_song);
        not_found = view.findViewById(R.id.no_song_lottie);
        layout = view.findViewById(R.id.music_frag_layout);
        perk_imageView = view.findViewById(R.id.perk_imageView);

        adView = new AdView(getActivity().getApplicationContext(), "772807133924163_774117280459815", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Error: " + adError.getErrorMessage(),
                                Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());

        perk_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), AllPerksScreen.class));
            }
        });

        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            showMusic();
        }



        return view;
    }




    boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if(result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    void requestPermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Toast.makeText(getActivity(), "PERMISSION IS REQUIRED", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(recyclerView!=null)
        {
            recyclerView.setAdapter(new MusicListAdapter(songsList, getActivity()));
        }

    }


    private void showMusic()
    {

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };



        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection, selection, null, null);


            while(cursor.moveToNext())
            {
                AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                if(new File(songData.getPath()).exists())
                    songsList.add(songData);
            }

            if(songsList.size() == 0)
            {
                noMusicTextView.setVisibility(View.VISIBLE);
                not_found.setVisibility(View.VISIBLE);
                layout.setBackgroundResource(R.drawable.night);
            }
            else
            {

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new MusicListAdapter(songsList, getActivity()));


            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}