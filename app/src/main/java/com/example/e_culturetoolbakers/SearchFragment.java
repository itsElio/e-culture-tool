package com.example.e_culturetoolbakers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.e_culturetoolbakers.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private ArrayList<String> lista;
    private DatabaseHelper db_helper;
    private SearchView searchView;
    private Spinner spinner;
    private String tipo = "";
    private ArrayList<String> listaZone;
    private ArrayList<String> listaOpere;
    private ArrayAdapter<String> adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.activity_ricerca, container, false);


        db_helper = new DatabaseHelper(getContext());
        listaOpere = db_helper.getListOpereTot();
        listaZone = db_helper.getListZoneTot();

        listView = (ListView) view.findViewById(R.id.listaOpereSchermataOpere);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                if(tipo.equals("Opera")){
                    Intent intent = new Intent(getActivity(), SchermataOpera.class);
                    intent.putExtra("nome", String.valueOf(listView.getItemAtPosition(pos)));
                    startActivity(intent);
                } else if(tipo.equals("Zona")){
                    Intent intent = new Intent(getActivity(), SchermataZona.class);
                    intent.putExtra("nome", String.valueOf(listView.getItemAtPosition(pos)));
                    startActivity(intent);
                }

            }
        });

        searchView = (SearchView) view.findViewById(R.id.ricerca);
        //searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if(!tipo.equals("")) {
                    adapter.getFilter().filter(s);
                    return false;
                } else{
                    Toast.makeText(getContext(), "Seleziona tipo", Toast.LENGTH_LONG) .show();
                    return false;
                }

            }
        });

        spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] tipi = { "", "Opera" , "Zona" };
        ArrayAdapter<String> ad = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, tipi);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tipo = tipi[position];
                if(tipi[position].equals("Zona")){
                    adapter = new ArrayAdapter<String>(getActivity(),   android.R.layout.simple_list_item_1, listaZone);
                    listView.setAdapter(adapter);
                }else if(tipi[position].equals("Opera")){
                    adapter = new ArrayAdapter<String>(getActivity(),   android.R.layout.simple_list_item_1, listaOpere);
                    listView.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getContext(), "NULLA", Toast.LENGTH_LONG) .show();
            }
        });

        return view;
    }
}