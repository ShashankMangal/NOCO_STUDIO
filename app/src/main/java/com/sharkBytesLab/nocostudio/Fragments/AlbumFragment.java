package com.sharkBytesLab.nocostudio.Fragments;

import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.albums;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sharkBytesLab.nocostudio.Adapters.AlbumAdapter;
import com.sharkBytesLab.nocostudio.R;


public class AlbumFragment extends Fragment
{

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_album);
        recyclerView.setHasFixedSize(true);
        if(!(albums.size() < 1))
        {
            albumAdapter = new AlbumAdapter (getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }
}