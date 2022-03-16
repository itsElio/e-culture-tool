package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.nio.json.JSONImporter;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.example.e_culturetoolbakers.adapter.ListaOpereAdapter;
import com.example.e_culturetoolbakers.adapter.ListaZoneAdapter;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelezionaOpere extends AppCompatActivity {

    private ListView listView;
    private ListaOpereAdapter adapter;
    private AutoCompleteTextView testoZone;
    private ArrayAdapter<String> adapterItems;
    private String zona = null;
    private HashMap<String, ArrayList> mapOpere;
    private HashMap<String, ArrayList> mapOpereTot;
    private ArrayList<String> zone;

    private DatabaseHelper db_helper;
    private ArrayList<Zona> listaZone;
    private String filename;

    private String nomePercorso;
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_opere);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        // Mostra pulsante indietro
        actionBar.setDisplayHomeAsUpEnabled(true);

        //DB
        db_helper = new DatabaseHelper(this);

        //Carica i dati
        Intent intent = getIntent();
        int tipo = intent.getIntExtra("tipoPercorso", -1);
        zone = intent.getStringArrayListExtra("zone");
        nomePercorso = intent.getStringExtra("nomePercorso");
        Serializable serializedMuseo = getIntent().getSerializableExtra("Museo");
        Museo nuovoMuseo = (Museo) serializedMuseo;

        mapOpere = HashMapCreationFromDB2(zone, 1);
        mapOpereTot = HashMapCreationFromDB2(zone, 0);

        String mode = intent.getStringExtra("mode");

        if(mode.equals("mod")) {

            mapOpere = (HashMap<String, ArrayList>) intent.getSerializableExtra("mapOpere");

        }else {
            creaBarra();
        }

        //IMPORT FILE IN STRUTTURA GRAFO
        filename = intent.getStringExtra("filename");
        DefaultDirectedGraph<String, DefaultEdge> g1 = new DefaultDirectedGraph(DefaultEdge.class);
        JSONImporter<String, DefaultEdge> jsonImporter = new JSONImporter<>();
        jsonImporter.setVertexFactory(v -> v);
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);

            jsonImporter.importGraph(g1, fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        listView = findViewById(R.id.listaOpere);

        testoZone = findViewById(R.id.testoZone);
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zone);
        testoZone.setAdapter(adapterItems);

        testoZone.setOnItemClickListener((adapterView, view, i, l) -> {
            zona = adapterView.getItemAtPosition(i).toString();

            adapter = new ListaOpereAdapter(this, R.layout.lista_opere, mapOpere.get(zona));
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        ImageButton aggiungi = (ImageButton) findViewById(R.id.bottoneAggiungi);
        aggiungi.setVisibility(View.VISIBLE);

        Button avanti = (Button) findViewById(R.id.buttonAvanti);
        avanti.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               //AGGIUNGE OPERE AL GRAFO
               for (String zona : mapOpere.keySet()) {
                   for(int i=0; i<mapOpere.get(zona).size(); i++) {
                       DepthFirstIterator depthFirstIterator = new DepthFirstIterator<>(g1);
                       String root = null;

                       while(depthFirstIterator.hasNext()) {
                           root = depthFirstIterator.next().toString();
                           String nome = root.substring(2);
                           if(nome.equals(zona)) {
                               break;
                           }
                       }

                       g1.addVertex((String) "o-" + mapOpere.get(zona).get(i));
                       g1.addEdge(root, (String) "o-" + mapOpere.get(zona).get(i));
                   }
               }
                /*
               //VISUALIZZA GRAFO (TEST)
               DepthFirstIterator depthFirstIterator = new DepthFirstIterator<>(g1);

               while(depthFirstIterator.hasNext()) {
                   Toast.makeText(SelezionaOpere.this, depthFirstIterator.next().toString(), Toast.LENGTH_LONG).show();
               }*/

               //SCRIVE GRAFO SU FILE
               JSONExporter<String, DefaultEdge> jsonExporter= new JSONExporter<>(v1 -> v1);
               FileOutputStream fos;
               try {
                   fos = openFileOutput(filename, MODE_PRIVATE);
                   jsonExporter.exportGraph(g1, fos);
               } catch (IOException e) {
                   e.printStackTrace();
               }

               Intent myIntent = new Intent(getApplicationContext(), RiepilogoPercorso.class);
               myIntent.putExtra("filename",filename);
               myIntent.putExtra("mode", mode);
               startActivity(myIntent);



           }
       });

       aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(zona == null) {
                    return;
                }

                // Dialogo per selezionare le opere per ogni zona
                AlertDialog.Builder builder = new AlertDialog.Builder(SelezionaOpere.this);
                builder.setTitle(R.string.opere_aggiungere);

                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogLayout = layoutInflater.inflate(R.layout.seleleziona_opere_ricerca,null);
                builder.setView(dialogLayout);

                //Visualizzo le opere da aggiungere
                String[] opere;
                ArrayList<String> opereList = new ArrayList<String>(mapOpereTot.get(zona));
                opereList.removeAll(mapOpere.get(zona));
                opere = new String[opereList.size()];
                opere = opereList.toArray(opere);

                ListView listView = dialogLayout.findViewById(R.id.listaOpereRicerca);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelezionaOpere.this, android.R.layout.simple_list_item_multiple_choice, opere);
                listView.setAdapter(arrayAdapter);

                String[] finalOpere = opere;
                builder.setPositiveButton(R.string.conferma, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                mapOpere.get(zona).add(finalOpere[i]);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

                AlertDialog dialog = builder.create();

                SearchView searchView = dialogLayout.findViewById(R.id.search);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String string) {
                        arrayAdapter.getFilter().filter(string);
                        arrayAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("zone", zone);
        outState.putSerializable("mapOpere", mapOpere);
        outState.putString("filename", filename);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mapOpere = (HashMap<String, ArrayList>) savedInstanceState.getSerializable("mapOpere");
        zone = (ArrayList<String>) savedInstanceState.getSerializable("zone");

        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zone);
        testoZone.setAdapter(adapterItems);
        filename = savedInstanceState.getString("filename");

    }

    private void creaBarra() {
        //Step progress bar
        ImageView primo_step = findViewById(R.id.myImageView);
        primo_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView prima_linea_step = findViewById(R.id.lineBar1);
        prima_linea_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView secondo_step = findViewById(R.id.myImageView2);
        secondo_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView seconda_linea_step = findViewById(R.id.lineBar2);
        seconda_linea_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView terzo_step = findViewById(R.id.myImageView3);
        terzo_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView terza_linea_step = findViewById(R.id.lineBar3);
        terza_linea_step.setColorFilter(Color.rgb(176, 255, 87));

        ImageView quarto_step = findViewById(R.id.myImageView4);
        quarto_step.setColorFilter(Color.rgb(50, 203, 0));

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private HashMap<String, ArrayList> HashMapCreationFromDB(ArrayList<Zona> listaZone, int flag) {
        ArrayList<String> nomiOpere;
        HashMap<String, ArrayList> mapOpereTemp = new HashMap<String, ArrayList>();

        for (int i =0; i<listaZone.size();i++) {
            listaZone.get(i).setOpere(db_helper.getListOpere(listaZone.get(i)));//trovo le opere nel db
            nomiOpere=listaZone.get(i).getOpereNames();
            if(flag == 1) {
                nomiOpere = new ArrayList<String>(nomiOpere.subList(0,3));
            }
            mapOpereTemp.put(listaZone.get(i).getNome(), nomiOpere);
        }

        return mapOpereTemp;
    }

    private HashMap<String, ArrayList> HashMapCreationFromDB2(ArrayList<String> listaZone, int flag) {
        ArrayList<String> nomiOpere;
        HashMap<String, ArrayList> mapOpereTemp = new HashMap<String, ArrayList>();

        for (int i =0; i<listaZone.size();i++) {

            String temp = listaZone.get(i);
            if(temp.contains("-")){
                String[] parts = temp.split("-");
                temp = parts[0];
            }

            nomiOpere = db_helper.getListOpere2(temp);//trovo le opere nel db
            if(flag == 1) {
                nomiOpere = new ArrayList<String>(nomiOpere.subList(0,1));
            }
            mapOpereTemp.put(listaZone.get(i), nomiOpere);
        }

        return mapOpereTemp;
    }

    public DatabaseHelper getDb_helper() {
        return db_helper;
    }
}