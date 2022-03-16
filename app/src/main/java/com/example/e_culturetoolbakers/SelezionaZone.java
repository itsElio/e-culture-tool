package com.example.e_culturetoolbakers;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.json.JSONExporter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import com.example.e_culturetoolbakers.adapter.ListaZoneAdapter;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Zona;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelezionaZone extends AppCompatActivity {

    private ListView listView;
    private ListaZoneAdapter adapter;
    private ArrayList<String> lista;
    private String zona = null;
    private ArrayList<Zona> listaZone;
    private DatabaseHelper db_helper;
    private String filename;
    private File fi = null; //File vecchio da modificare


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_zone);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        // Mostra pulsante indietro
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Prendi dati da intent
        Intent intent = getIntent();
        int tipo = intent.getIntExtra("tipoPercorso", -1);
        String nomePercorso = intent.getStringExtra("nomePercorso");
        String mode = intent.getStringExtra("mode");

        //Opere per modifica
        ArrayList<String> opere = new ArrayList();

        //DB
        db_helper = new DatabaseHelper(this);

        Serializable serializedMuseo = getIntent().getSerializableExtra("Museo");
        Museo nuovoMuseo = (Museo) serializedMuseo;

        //Carica i dati
        listaZone = db_helper.getListZone(nuovoMuseo);//trovo le zone nel db
        nuovoMuseo.setZone(listaZone);//passo le zone trovate nel museo

        ArrayList<String> listaTot = nuovoMuseo.getZoneNames();//<-ci passo le stringhe che corrispondono ai nomi delle zone/

        HashMap<String, ArrayList<String>> mapOpere = new HashMap<>();

        if(mode.equals("crea")) {

            creaBarra();
            //questa è la lista che verrà modificata
            lista = new ArrayList<>(listaTot);

        } else if(mode.equals("mod")) {
            //Modifica Percorso, prende i dati dal file in cui è salvato il grafo per visualizzarli e modificarli
            filename = intent.getStringExtra("filename");

            String responce = "";
            fi = new File( getFilesDir(), filename);
            BufferedReader bufferedReader;
            try (FileReader fileReader = new FileReader(fi)) {
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null){
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                responce = stringBuilder.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            lista = new ArrayList();

            JSONObject obj = null;
            try {
                obj = new JSONObject(responce);

                JSONArray array = obj.getJSONArray("nodes");
                String zonaTemp = null;

                for (int i = 0; i < array.length(); i++)
                {
                    String nome = array.getJSONObject(i).getString("id");

                    String tipoNome = nome.substring(0,1);
                    //Toast.makeText(this, tipoNome, Toast.LENGTH_LONG).show();
                    nome = nome.substring(2);

                    if(tipoNome.equals("z")){

                        JSONArray edges = obj.getJSONArray("edges");

                        ArrayList<String> opereTemp = new ArrayList<>();

                        for(int j=0; j<edges.length(); j++){
                            String source = edges.getJSONObject(j).getString("source").substring(2);
                            String tipoTarget = edges.getJSONObject(j).getString("target").substring(0,1);
                            String target = edges.getJSONObject(j).getString("target").substring(2);

                            if(source.equals(nome) && tipoTarget.equals("o")){
                                opereTemp.add(target);
                            }
                        }

                        if(tipo == 1){
                            lista.add(nome);
                            mapOpere.put(nome, opereTemp);
                        } else if (nome.contains("-") && tipo == 0){

                        } else if (tipo == 0){
                            lista.add(nome);
                            mapOpere.put(nome, opereTemp);
                        }

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        filename = nuovoMuseo.getNome() + "-" + nomePercorso + "-" + tipo +".json";

        // Mostra pulsante indietro
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listaZone);

        adapter = new ListaZoneAdapter(this, R.layout.lista_zone, lista);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                SelezionaZone.this.zona = (String)listView.getItemAtPosition(pos);
                adapter.setSelezionato(zona);
                adapter.notifyDataSetChanged();

                return true;
            }
        });

        Button avanti = (Button) findViewById(R.id.buttonAvanti_SelezionaZona);
        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Crea il grafo con aggiungendo le zone selezionate
                DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph(DefaultEdge.class);

                for(int i=0; i<lista.size(); i++) {
                    g.addVertex(lista.get(i));
                }

                for(int i=0; i<lista.size()-1; i++) {
                    g.addEdge(lista.get(i), lista.get(i+1));
                }

                JSONExporter<String, DefaultEdge>  jsonExporter= new JSONExporter<>(v -> "z-"+ v);

                FileOutputStream fos;
                try {
                    fos = openFileOutput(filename, MODE_PRIVATE);
                    jsonExporter.exportGraph(g, fos);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent myIntent = new Intent(getApplicationContext(), SelezionaOpere.class);
                myIntent.putStringArrayListExtra("zone", lista);
                myIntent.putExtra("Museo",nuovoMuseo);
                myIntent.putExtra("filename", filename);
                myIntent.putExtra("nomePercorso",nomePercorso);
                myIntent.putExtra("tipoPercorso",tipo);

                if(mode.equals("mod")) {

                    // Se sta modificando un percorso passo sia le zone che le opere già selezionate
                    for(int i=0; i<lista.size(); i++){
                        boolean trov = true;
                        for (String zona : mapOpere.keySet()) {
                            if(lista.get(i).equals(zona)){
                                trov = true;
                                break;
                            }else{
                                trov = false;
                            }
                        }

                        if(trov == false){

                            mapOpere.put(lista.get(i), new ArrayList<String>());
                        }
                    }

                    if(!fi.getName().equals(filename)){
                        fi.delete();
                    }



                    myIntent.putExtra("mode","mod");
                    //myIntent.putStringArrayListExtra("opereMod", opere);
                    myIntent.putExtra("mapOpere", mapOpere);

                }else {
                    myIntent.putExtra("mode","crea");
                }

                startActivity(myIntent);
            }
        });


        //Tipo 0 Semplice
        //Tipo 1 Complesso

        ImageButton aggiungi = (ImageButton) findViewById(R.id.bottoneAggiungi);
        aggiungi.setVisibility(View.VISIBLE);

        aggiungi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Dialogo per selezionare le zone da aggiungere nel percorso
                AlertDialog.Builder builder = new AlertDialog.Builder(SelezionaZone.this);

                // Add a checkbox list
                String[] zone;
                boolean[] checkedItems;

                if(tipo == 1) {
                    builder.setTitle(R.string.zone_rivisitare);
                    zone = new String[listaTot.size()];
                    for(int i=0; i< listaTot.size(); i++) {
                        zone[i] = listaTot.get(i);
                    }

                    checkedItems = new boolean[zone.length];
                    for(int i=0; i< zone.length; i++) {
                        checkedItems[i] = false;
                    }
                    builder.setNegativeButton("Cancella", null);

                } else {
                    builder.setTitle(R.string.zone_aggiungere);
                    ArrayList<String> zoneList = new ArrayList<>(listaTot);
                    zoneList.removeAll(lista);
                    zone = new String[zoneList.size()];
                    zone = zoneList.toArray(zone);

                    checkedItems = new boolean[zone.length];
                    for(int i=0; i< zone.length; i++) {
                        checkedItems[i] = false;
                    }

                    if(zoneList.isEmpty()) {
                        builder.setTitle(R.string.nessuna_zona);
                    }
                }

                builder.setMultiChoiceItems(zone, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // The user checked or unchecked a box
                    }
                });

                builder.setNegativeButton(R.string.annulla, null);

                // Add OK and Cancel buttons
                String[] finalZone = zone;
                boolean[] finalCheckedItems = checkedItems;
                builder.setPositiveButton(R.string.conferma, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK

                        for(int i = 0; i< finalZone.length; i++) {
                            if(finalCheckedItems[i]) {

                                int cont = 1;
                                for(int j=0; j<lista.size(); j++) {
                                    String[] parti = lista.get(j).split("-");

                                    if(parti[0].equals(finalZone[i])) {
                                        cont++;
                                    }
                                }

                                if(cont > 1) {
                                    lista.add(finalZone[i]+"- "+cont);
                                }else{
                                    lista.add(finalZone[i]);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("lista", lista);
        outState.putString("filename", filename);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lista = (ArrayList<String>) savedInstanceState.getSerializable("lista");
        adapter = new ListaZoneAdapter(this, R.layout.lista_zone, lista);
        listView.setAdapter(adapter);
        filename = savedInstanceState.getString("filename");
    }

    private void creaBarra(){
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
        terzo_step.setColorFilter(Color.rgb(50, 203, 0));
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //toccato pulsante indietro

        }
        return true;
    }

    public void sali(View v){
        if(zona!=null) {
            int zonaPos = adapter.getPosition(zona);

            if(zonaPos != 0) {
                Collections.swap(lista, zonaPos, zonaPos-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void scendi(View v){
        if(zona!=null) {
            int zonaPos = adapter.getPosition(zona);

            if(zonaPos != lista.size()-1) {
                Collections.swap(lista, zonaPos, zonaPos+1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

}