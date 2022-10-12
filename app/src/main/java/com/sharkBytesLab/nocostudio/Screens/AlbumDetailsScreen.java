package com.sharkBytesLab.nocostudio.Screens;

import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.Adapters.AlbumDetailsAdapter;
import com.sharkBytesLab.nocostudio.Misc.MusicFiles;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityAlbumDetailsScreenBinding;

import java.util.ArrayList;

public class AlbumDetailsScreen extends AppCompatActivity
{
    private ActivityAlbumDetailsScreenBinding binding;
    private String albumName;
    private ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    private AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumDetailsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkTheme));
        }

        albumName = getIntent().getStringExtra("albumName");
        int j=0;

        binding.albumDetailName.setText(" " + albumName);

        for(int i=0; i<musicFiles.size() ;i++)
        {
            if(albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }

        byte [] image = getAlbumArt(albumSongs.get(0).getPath());

        if(image!=null)
        {
            Glide.with(this).load(image).into(binding.albumPhoto);
        }
        else
        {
            Glide.with(this).load(R.drawable.album_cover_art).into(binding.albumPhoto);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!(albumSongs.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            binding.recyclerView.setAdapter(albumDetailsAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }

    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

}