package com.sharkBytesLab.nocostudio.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sharkBytesLab.nocostudio.R;

public class MusicFragment extends Fragment {


    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_music, container, false);

        initViewPager(view);

        return view;
    }

    private void initViewPager(View view)
    {
        ViewPager viewPager = view.findViewById(R.id.music);
    }

}