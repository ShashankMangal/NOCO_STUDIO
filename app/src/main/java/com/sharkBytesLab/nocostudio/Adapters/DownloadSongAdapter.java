package com.sharkBytesLab.nocostudio.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sharkBytesLab.nocostudio.Models.DownloadModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.DownloadSongScreen;


import java.util.ArrayList;

public class DownloadSongAdapter extends RecyclerView.Adapter<DownloadSongAdapter.ViewHolder> {

    private static final String TAG = "RecyclerView";
    private Context context;
    private ArrayList<DownloadModel> songList;
    private ProgressDialog pd;


    public DownloadSongAdapter(Context context, ArrayList<DownloadModel> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_song_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DownloadModel downloadModel = songList.get(position);

        holder.textView.setText(downloadModel.getTitle());
        Glide.with(context).load(downloadModel.getImage()).thumbnail(Glide.with(context).load(R.drawable.spinner)).into(holder.imageView);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(context);
                pd.show();
                pd.setContentView(R.layout.progress_dialog);
                pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);



                Intent i = null;
                try {
                    i = new Intent(context, DownloadSongScreen.class);
                    i.putExtra("title",downloadModel.getTitle().toString());
                    i.putExtra("image",downloadModel.getImage().toString());
                    i.putExtra("link",downloadModel.getLink().toString());
                    i.putExtra("videoId",downloadModel.getVideoId().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread timer = new Thread()
                {
                    @Override
                    public void run() {
                        try{
                            sleep(3000);

                            pd.dismiss();


                        super.run();
                    }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                };
                timer.start();

                context.startActivity(i);



            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(context, downloadModel.getTitle().toString(), Toast.LENGTH_LONG).show();

                return true;
            }


        });

        //holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim));

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.song_image);
            textView = itemView.findViewById(R.id.song_title);
            cardView = itemView.findViewById(R.id.song_cardView);

        }
    }
}
