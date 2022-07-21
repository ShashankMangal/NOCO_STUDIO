package com.sharkBytesLab.nocostudio.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sharkBytesLab.nocostudio.Adapters.WallpaperAdapter;
import com.sharkBytesLab.nocostudio.Misc.ApiUtilities;
import com.sharkBytesLab.nocostudio.Models.ImageModel;
import com.sharkBytesLab.nocostudio.Models.SearchModel;
import com.sharkBytesLab.nocostudio.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WallpaperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallpaperFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ImageModel> modelClassList;
    private WallpaperAdapter adapter;
    private RecyclerView wallpaperRr;
    private ProgressDialog pd;

    public WallpaperFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WallpaperFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WallpaperFragment newInstance(String param1, String param2) {
        WallpaperFragment fragment = new WallpaperFragment();
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

        View view = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        wallpaperRr = view.findViewById(R.id.wallpaperRecyclerView);


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

        modelClassList = new ArrayList<>();
        wallpaperRr.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        wallpaperRr.setHasFixedSize(true);
        adapter = new WallpaperAdapter(getActivity().getApplicationContext(), modelClassList);
        wallpaperRr.setAdapter(adapter);


        String query = "music";
        getSearchImage(query);

        return view;

    }


    private void getSearchImage(String query) {

        ApiUtilities.getApiInterface().getSearchImage(query, 1, 80).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(retrofit2.Call<SearchModel> call, Response<SearchModel> response) {

                modelClassList.clear();
                if(response.isSuccessful())
                {

                    modelClassList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Error Occurred on getting Images.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

            }
        });

    }



}