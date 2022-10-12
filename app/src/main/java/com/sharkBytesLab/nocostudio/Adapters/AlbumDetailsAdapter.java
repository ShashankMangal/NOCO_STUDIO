package com.sharkBytesLab.nocostudio.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.Misc.MusicFiles;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.AlbumDetailsScreen;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder>
{

    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    private View view;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles)
    {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {

        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte [] image = getAlbumArt(albumFiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.album_cover_art).into(holder.album_image);
            holder.background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.darkTheme));
            holder.album_name.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView album_image;
        TextView album_name;
        RelativeLayout background;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            background = itemView.findViewById(R.id.background_color);
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
