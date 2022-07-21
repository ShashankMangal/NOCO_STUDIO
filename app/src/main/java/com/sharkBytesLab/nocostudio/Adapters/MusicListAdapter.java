package com.sharkBytesLab.nocostudio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharkBytesLab.nocostudio.Misc.MyMediaPlayer;
import com.sharkBytesLab.nocostudio.Models.AudioModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.MusicPlayerScreen;

import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{

    ArrayList<AudioModel> songsList;
    Context context;

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent, false);

        return new MusicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        AudioModel songData = songsList.get(position);

        holder.titleTextView.setText(songData.getTitle());
        holder.titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.titleTextView.setSelected(true);

        if(MyMediaPlayer.currentIndex==position)
        {
            holder.titleTextView.setTextColor(Color.parseColor("#ea0a53"));
        }
        else
        {
            holder.titleTextView.setTextColor(Color.parseColor("#414141"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;

                Intent intent = new Intent(context, MusicPlayerScreen.class);
                intent.putExtra("LIST", songsList);
                intent.putExtra("TITLE", songData.getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.music_title_text);
            iconImageView = itemView.findViewById(R.id.icon_view);

        }
    }


}
