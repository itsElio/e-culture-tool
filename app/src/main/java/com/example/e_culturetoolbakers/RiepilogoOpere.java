package com.example.e_culturetoolbakers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
// Da rimuovere
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class RiepilogoOpere extends AppCompatActivity {

    ListView riepilogoOpereList;
    ArrayList<String> zoneList  = new ArrayList<String>();
    ArrayList<String> opereList  = new ArrayList<String>();
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    private List<Opera> opere = new ArrayList<>();
    private String item;
    private HashMap<String, ArrayList> hashMapOpere = new HashMap<>();
    private HashMap<String, ArrayList> mapOpere = new HashMap<>();
    public Museo nuovoMuseo;
    private Zona zonaPerAdapter;


    public HashMap<String, ArrayList> aggiornaOpere(Museo nuovoMuseo) {
        if(nuovoMuseo != null) {
            for(Zona zona : nuovoMuseo.getZone()) {

                ArrayList<String> nomeOpere = new ArrayList<String>();

                opere = zona.getOpere();
                for (Opera opera : opere) {
                    nomeOpere.add(opera.getNomeOpera());
                }
                hashMapOpere.put(zona.getNome(), nomeOpere);
            }
        }
        return hashMapOpere;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_opere);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        riepilogoOpereList = (ListView) findViewById(R.id.listaOpere);

        Bundle museo = getIntent().getExtras();
        Serializable serializedMuseo  = museo.getSerializable("museoObject");
        nuovoMuseo = (Museo) serializedMuseo;


        if(nuovoMuseo != null) {
            for(Zona zona : nuovoMuseo.getZone()) {
                zoneList.add(zona.getNome());

                ArrayList<String> nomeOpere = new ArrayList<String>();

                opere = zona.getOpere();
                for (Opera opera : opere) {
                    nomeOpere.add(opera.getNomeOpera());
                }
                hashMapOpere.put(zona.getNome(), nomeOpere);
            }
        }

        autoCompleteText = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(this, R.layout.lista_zone_dropdown, zoneList);
        autoCompleteText.setAdapter(adapterItems);

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                item = parent.getItemAtPosition(position).toString();
                nuovoMuseo.getZone().indexOf(item);
                for (Zona zona : nuovoMuseo.getZone()) {
                    if(zona.getNome().equals(item)) {
                        zonaPerAdapter = zona;
                    }
                }


                ZoneAdapter zoneAdapter = new ZoneAdapter(RiepilogoOpere.this, opereList, nuovoMuseo, zonaPerAdapter, zonaPerAdapter.getOpere());

                //Toast.makeText(getApplicationContext(), "Item: "+item, Toast.LENGTH_SHORT).show();
                mapOpere = aggiornaOpere(nuovoMuseo);

                opereList = mapOpere.get(item);

                // Non dovrebbe servire pulire l'adapter e riaggiungere, ma senza questo codice non funziona
                zoneAdapter.clear();
                zoneAdapter.addAll(opereList);

                riepilogoOpereList.setAdapter(zoneAdapter);
                zoneAdapter.notifyDataSetChanged();
            }
        });


        Button addOpera = (Button) findViewById(R.id.addOpera);

        addOpera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inserisciOpera = new Intent(RiepilogoOpere.this, InserisciOpera.class);

                Bundle museo = new Bundle();

                museo.putSerializable("museoObject", nuovoMuseo);
                inserisciOpera.putExtras(museo);

                inserisciOpera.putExtra("Zona", item);
                RiepilogoOpere.this.startActivity(inserisciOpera);
            }
        });

        Button nextStep = (Button) findViewById(R.id.buttonAvanti);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent riepilogoRegistrazione = new Intent(RiepilogoOpere.this, RiepilogoRegistrazione.class);

                Bundle museo =  new Bundle();
                museo.putSerializable("museoObject", nuovoMuseo);
                riepilogoRegistrazione.putExtras(museo);
                RiepilogoOpere.this.startActivity(riepilogoRegistrazione);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("Museo", nuovoMuseo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        aggiornaOpere(nuovoMuseo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}