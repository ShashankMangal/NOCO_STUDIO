package com.sharkBytesLab.nocostudio.Fragments;

import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.musicFiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<DownloadModel> list;
    private DownloadSongAdapter adapter;
    private SearchView searchView;
    private FirebaseFirestore firestore;
    private ProgressDialog pd;
    private TextView songs_num, server;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InfoModel model;
    private ImageView favSong;
    private FirebaseRemoteConfig remoteConfig;
    private String libaray_code;
    public static MusicAdapter musicAdapter;
    private ConstraintLayout emptyLibrary;


    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = view.findViewById(R.id.downloadRecyclerView);
        searchView = view.findViewById(R.id.search_song);
        swipeRefreshLayout = view.findViewById(R.id.refreshView);
        songs_num = view.findViewById(R.id.songs_num);
        server = view.findViewById(R.id.server_song_num);
        favSong = view.findViewById(R.id.fav_song);
        emptyLibrary = view.findViewById(R.id.empty_library);


        firestore = FirebaseFirestore.getInstance();
        remoteConfig = FirebaseRemoteConfig.getInstance();


        pd = new ProgressDialog(getActivity());
        pd.show();
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);

                    pd.dismiss();


                    super.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        timer.start();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        myRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();

        getInfo();

        clearAll();

        favSong.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavouriteScreen.class)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s);
                return false;
            }
        });

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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error on getting songs list.", Toast.LENGTH_SHORT).show();
        }

        return view;
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


    public void getDataFromFirebase() {

        Runnable objRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build();
                    remoteConfig.setConfigSettingsAsync(configSettings);

                    remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {

                            if (task.isSuccessful()) {
                                libaray_code = remoteConfig.getString("library_ns");
                                if (Integer.parseInt(libaray_code) > 0) {

                                    recyclerView.setVisibility(View.VISIBLE);
                                    songs_num.setVisibility(View.VISIBLE);
                                    server.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    favSong.setVisibility(View.VISIBLE);
                                    searchView.setVisibility(View.VISIBLE);


                                    Query query = myRef.child("DownloadData").orderByChild("serial");
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            clearAll();
                                            int temp = 0;

                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                                DownloadModel model = new DownloadModel();
                                                model.setImage(snapshot1.child("image").getValue().toString());
                                                model.setTitle(snapshot1.child("title").getValue().toString());
                                                model.setLink(snapshot1.child("link").getValue().toString());
                                                model.setVideoId(snapshot1.child("videoId").getValue().toString());
                                                list.add(model);
                                                temp++;
                                            }

                                            adapter = new DownloadSongAdapter(getActivity(), list);
                                            recyclerView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            songs_num.setText("Songs Found : " + String.valueOf(temp));


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                } else {
                                    songs_num.setVisibility(View.GONE);
                                    server.setVisibility(View.GONE);
                                    favSong.setVisibility(View.GONE);
                                    searchView.setVisibility(View.GONE);

                                    if (!(musicFiles.size() < 1)) {
                                        Log.i("GetAllAudio", "IF");
                                        try {
                                            musicAdapter = new MusicAdapter(getContext(), musicFiles);
                                            recyclerView.setAdapter(musicAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }


                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();


    }

    private void processSearch(String s) {

        Query query = myRef.child("DownloadData");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();
                int temp = 0;

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    DownloadModel model = new DownloadModel();

                    if (snapshot1.child("title").getValue().toString().toLowerCase().contains(s.toLowerCase())) {

                        model.setImage(snapshot1.child("image").getValue().toString());
                        model.setTitle(snapshot1.child("title").getValue().toString());
                        model.setLink(snapshot1.child("link").getValue().toString());
                        model.setVideoId(snapshot1.child("videoId").getValue().toString());
                        list.add(model);
                        temp++;


                    }
                }

                adapter = new DownloadSongAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                songs_num.setText("Songs Found : " + String.valueOf(temp));
                if (list.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyLibrary.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLibrary.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearAll() {
        if (list != null) {
            list.clear();

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }

        list = new ArrayList<>();
    }


    void getInfo() {
        firestore.collection("DownloadCount").document("count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                model = documentSnapshot.toObject(InfoModel.class);

                server.setText("Songs on Server : " + String.valueOf(model.getSongs()));

            }
        });
    }

}