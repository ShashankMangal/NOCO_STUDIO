package com.sharkBytesLab.nocostudio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.Models.ImageModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.SetWallpaperScreen;

import java.util.ArrayList;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {



    Context context;
    ArrayList<ImageModel> wallpaperList;

    public WallpaperAdapter(Context context, ArrayList<ImageModel> wallpaperList) {
        this.context = context;
        this.wallpaperList = wallpaperList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_items_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.textView.setMovementMethod(LinkMovementMethod.getInstance());
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.pexels.com/"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }
                });

            }
        });

        Glide.with(context).load(wallpaperList.get(position).getSrc().getPortrait()).thumbnail(Glide.with(context).load(R.drawable.spinner)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SetWallpaperScreen.class);
                intent.putExtra("image", wallpaperList.get(position).getSrc().getPortrait());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.wallpaperImage);
            textView = itemView.findViewById(R.id.copyrightWallpaperText);

        }
    }
}
