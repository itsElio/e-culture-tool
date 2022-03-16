package com.example.e_culturetoolbakers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;

public class RiepilogoZone extends AppCompatActivity {

    ListView riepilogoZoneList;
    ArrayList<String> zoneList  = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_zone);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle museo = getIntent().getExtras();
        //Serializable serializedMuseo = getIntent().getSerializableExtra("Museo");

        Serializable serializedMuseo  = museo.getSerializable("museoObject");
        Museo nuovoMuseo = (Museo) serializedMuseo;

        if(nuovoMuseo != null) {
            if(nuovoMuseo.getZone() != null){
                nuovoMuseo.getZone().forEach((zona)-> zoneList.add(zona.getNome()));
            }
        }

        riepilogoZoneList = (ListView) findViewById(R.id.riepilogoZoneList);
        ZoneAdapter zoneAdapter = new ZoneAdapter(this, zoneList, nuovoMuseo, nuovoMuseo.getZone());
        riepilogoZoneList.setAdapter(zoneAdapter);

        FloatingActionButton addZona = (FloatingActionButton) findViewById(R.id.addZona);

        addZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inserisciZona = new Intent(RiepilogoZone.this, InserisciZona.class);
                Bundle museo = new Bundle();
                museo.putSerializable("museoObject", nuovoMuseo);
                inserisciZona.putExtras(museo);
                RiepilogoZone.this.startActivity(inserisciZona);
            }
        });
        Button nextStep = (Button) findViewById(R.id.buttonAvanti);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent riepilogoOpere = new Intent(RiepilogoZone.this, RiepilogoOpere.class);

                if(nuovoMuseo.getZone() == null) {
                    Toast.makeText(RiepilogoZone.this, "Inserisci almeno una zona prima proseguire", Toast.LENGTH_SHORT).show();
                } else {

                    Bundle museo = new Bundle();
                    museo.putSerializable("museoObject", nuovoMuseo);

                    riepilogoOpere.putExtras(museo);
                    RiepilogoZone.this.startActivity(riepilogoOpere);
                }
            }
        });
    }
}
