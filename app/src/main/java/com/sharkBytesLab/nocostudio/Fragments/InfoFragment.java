package com.sharkBytesLab.nocostudio.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.cat.CountAnimationTextView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sharkBytesLab.nocostudio.BuildConfig;
import com.sharkBytesLab.nocostudio.Models.InfoModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.DownloadSongScreen;
import com.sharkBytesLab.nocostudio.Screens.OnlineMusicScreen;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView version;
    private CountAnimationTextView download, songs;
    private FirebaseFirestore firestore;
    private InfoModel model;
    private RelativeLayout share;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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


        View view =  inflater.inflate(R.layout.fragment_info, container, false);


        version = view.findViewById(R.id.version_count);
        songs = view.findViewById(R.id.server_songs_count);
        download = view.findViewById(R.id.songs_download_count);
        share = view.findViewById(R.id.share_with_friends);

        firestore = FirebaseFirestore.getInstance();
        version.setText("V " + String.valueOf(BuildConfig.VERSION_NAME));



        Runnable objRunnable1 = new Runnable() {
            @Override
            public void run() {
                try {

                getInfo();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error :"+e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("Error",e.getMessage());
                }
            }
        };
        Thread objBgThread = new Thread(objRunnable1);
        objBgThread.start();


//        version.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("Clicked","TRUE");
//                startActivity(new Intent(getActivity(), OnlineMusicScreen.class));
//
//
//            }
//        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareBody = "Hey, I'm using NOCO Studio songs for my social platforms without any copyright. Join using this link below." + " \n" +
                        "Download from Play Store\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(intent);

            }
        });


        return view;
    }





    void getInfo()

    {
        firestore.collection("DownloadCount").document("count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                model = documentSnapshot.toObject(InfoModel.class);

                int count = model.getNum();
                download.setDecimalFormat(new DecimalFormat("###,###,###")).setAnimationDuration(3000).countAnimation(0, count);
                songs.setDecimalFormat(new DecimalFormat("###,###,###")).setAnimationDuration(3000).countAnimation(0, model.getSongs());

            }
        });
    }
}