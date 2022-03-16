package com.example.e_culturetoolbakers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MuseumRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MuseumRegistrationFragment extends Fragment {

    private boolean connected = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MuseumRegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MuseumRegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MuseumRegistrationFragment newInstance(String param1, String param2) {
        MuseumRegistrationFragment fragment = new MuseumRegistrationFragment();
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
        View view = inflater.inflate(R.layout.activity_network_check, container, false);

        Button button =(Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Da modificare questa intent
                Intent myIntent = new Intent(getActivity(), RegistrazioneMuseo.class);

                ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else {
                    connected = false;
                }
                if(connected) {
                    getActivity().startActivity(myIntent);
                } else {
                    Toast.makeText(getContext(), "Assicurati di essere connesso a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}