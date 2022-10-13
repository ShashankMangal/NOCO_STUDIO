package com.sharkBytesLab.nocostudio.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sharkBytesLab.nocostudio.Adapters.SectionPagerAdapter;
import com.sharkBytesLab.nocostudio.Misc.MusicFiles;
import com.sharkBytesLab.nocostudio.R;
import java.util.ArrayList;

public class MusicFragment extends Fragment {

    public static final int REQUEST_CODE = 1;
    static public ArrayList<MusicFiles> musicFiles;
    static public ArrayList<MusicFiles> albums = new ArrayList<>();
    private ViewPager viewPager;
    private View myFragment;
    private TabLayout tabLayout;
    static public boolean shuffleBoolean = false, repeatBoolean = false;
    private static EditText search_song_et;
    private static TextView noco_studio_text;
    private static ImageView song_search, song_history;

    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment getInstance() {return new MusicFragment();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myFragment =  inflater.inflate(R.layout.fragment_music, container, false);
        Log.i("GetAllAudio", "CreateView");
        permissions(myFragment);
        viewPager = myFragment.findViewById(R.id.music_viewPager);
        tabLayout = myFragment.findViewById(R.id.music_tab_layout);
        search_song_et = myFragment.findViewById(R.id.song_search_et);
        song_search = myFragment.findViewById(R.id.song_search);
        noco_studio_text = myFragment.findViewById(R.id.song_frag_nocoStudio_text);
        song_history = myFragment.findViewById(R.id.song_history);
        visibleItem();
        return myFragment;
    }


    private void visibleItem()
    {
        song_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(search_song_et.getVisibility()==View.GONE)
                {
                    search_song_et.setVisibility(View.VISIBLE);
                    noco_studio_text.setVisibility(View.GONE);
                }
                else
                {
                    search_song_et.setVisibility(View.GONE);
                    noco_studio_text.setVisibility(View.VISIBLE);
                }
            }
        });

        search_song_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String name = String.valueOf(s).toLowerCase();
                ArrayList<MusicFiles> myFiles = new ArrayList<>();

                for(MusicFiles m : musicFiles)
                {
                    if(m.getTitle().toLowerCase().contains(name))
                    {
                        myFiles.add(m);
                    }
                }
                SongsFragment.musicAdapter.updateList(myFiles);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        musicFiles = getAllAudio(getActivity().getApplicationContext());
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setUpViewPager(ViewPager viewPager)
    {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SongsFragment(), "Songs");
        adapter.addFragment(new AlbumFragment(), "Albums");
        viewPager.setAdapter(adapter);

    }

    public void permissions(View view)
    {
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else
        {
            //Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(getActivity().getApplicationContext());
            initViewPager(view);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "Permission Granted Successfully!", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(getActivity().getApplicationContext());
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

    }

    private void initViewPager(View view)
    {
        Log.i("GetAllAudio", "Init");
        ViewPager viewPager = view.findViewById(R.id.music_viewPager);
        TabLayout tabLayout = view.findViewById(R.id.music_tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context)
    {
        albums.clear();
        Log.i("GetAllAudio", "True");
        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<MusicFiles> tempAudioFiles = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String []projection =
                {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
                };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if(cursor != null)
        {
            while(cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                Log.e("path : " + path, "Album : " + album);
                tempAudioFiles.add(musicFiles);

                if (!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioFiles;
    }

}