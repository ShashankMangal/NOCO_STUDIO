package com.sharkBytesLab.nocostudio.Fragments;

import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.musicFiles;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sharkBytesLab.nocostudio.Adapters.DownloadSongAdapter;
import com.sharkBytesLab.nocostudio.Adapters.MusicAdapter;
import com.sharkBytesLab.nocostudio.Models.DownloadModel;
import com.sharkBytesLab.nocostudio.Models.InfoModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.FavouriteScreen;
import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InfoModel model;
    public static MusicAdapter musicAdapter;
    public TextView pro_version;
    public FirebaseRemoteConfig remoteConfig;


    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = view.findViewById(R.id.downloadRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.refreshView);
        pro_version = view.findViewById(R.id.library_noco_studio_pro);
        remoteConfig = FirebaseRemoteConfig.getInstance();

        pd = new ProgressDialog(getActivity());
        pd.show();
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Thread timer = new Thread()
        {
            @Override
            public void run() {
                try{
                    sleep(1000);

                    pd.dismiss();


                    super.run();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        };
        timer.start();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getDataFromFirebase();


                } catch (Exception e) {
                    e.printStackTrace();

                }


                swipeRefreshLayout.setRefreshing(false);

            }

        });

        try {
            getDataFromFirebase();

            pro_version.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openNOCOPro();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error on getting songs list.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void openNOCOPro() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if(task.isSuccessful())
                {

                    final String noco_pro_link = remoteConfig.getString("noco_pro");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noco_pro_link));
                    startActivity(intent);

                }
                else {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            getDataFromFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error on getting songs list.", Toast.LENGTH_SHORT).show();
        }

    }


    public void getDataFromFirebase()
    {

        Runnable objRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    if(!(musicFiles.size() < 1))
                    {
                        Log.i("GetAllAudio", "IF");
                        try {
                            musicAdapter = new MusicAdapter(getContext(), musicFiles);
                            recyclerView.setAdapter(musicAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();


    }

}