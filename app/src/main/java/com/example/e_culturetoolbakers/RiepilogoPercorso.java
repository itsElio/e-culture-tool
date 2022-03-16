package com.example.e_culturetoolbakers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.e_culturetoolbakers.adapter.ListaRiepilogoAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RiepilogoPercorso extends AppCompatActivity {


    private ListView listaDiRiepilogo;
    private String fileTestualeInFormatoJson;
    private String mode;
    private String fileName;


    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        try {
            visualizzaPercorsoDaFormatoJSON(fileTestualeInFormatoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_riepilogo);

        Toolbar myToolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        // Mostra pulsante indietro
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeWidget();

        //Carica i dati
        Intent intent = getIntent();
        fileName = intent.getStringExtra("filename");
        this.mode = intent.getStringExtra("mode");

        Button button = findViewById(R.id.buttonModifica);

        switch (mode) {
            case "crea":
                creaBarra();

                button.setText(R.string.conferma);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(RiepilogoPercorso.this, MainActivity.class);
                        startActivity(myIntent);
                    }
                });

                break;
            case "mod": {

                TextView textView = findViewById(R.id.titoloRiepilogo);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
                params.setMargins(30, 5, 30, 0); //substitute parameters for left, top, right, bottom

                textView.setLayoutParams(params);

                button.setText(R.string.conferma);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(RiepilogoPercorso.this, MainActivity.class);
                        startActivity(myIntent);
                    }
                });


                break;
            }

        }

        if (caricaJSONdelPercorso()) {
            try {
                visualizzaPercorsoDaFormatoJSON(fileTestualeInFormatoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean caricaJSONdelPercorso() {
        Context context = this;
        File file = new File(context.getFilesDir(),fileName);
        BufferedReader bufferedReader;
        try (FileReader fileReader = new FileReader(file)) {
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileTestualeInFormatoJson = stringBuilder.toString();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initializeWidget() {
        listaDiRiepilogo = findViewById(R.id.riepilogo);
    }

    private void visualizzaPercorsoDaFormatoJSON(String fileTestualeInFormatoJson) throws JSONException {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(fileTestualeInFormatoJson);

            JSONArray jsonArray = jsonObject.getJSONArray("nodes");
            ArrayList<String> arrayZone = new ArrayList<>();
            for(int i = 0; i< jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String nomeZona= obj.getString("id");
                String tipo  = nomeZona.substring(0,2);
                nomeZona = nomeZona.substring(2);
                if(tipo.startsWith("z")) {
                    arrayZone.add(nomeZona);
                }
            }
            ListaRiepilogoAdapter arrayAdapter = new ListaRiepilogoAdapter(this, arrayZone,fileTestualeInFormatoJson);
            listaDiRiepilogo.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void creaBarra() {
        //Step progress bar
        ImageView primo_step = findViewById(R.id.myImageView);
        primo_step.setColorFilter(Color.rgb(176, 255, 87));
        primo_step.setVisibility(View.VISIBLE);
        TextView testoStep1 = findViewById(R.id.testoStep1);
        testoStep1.setVisibility(View.VISIBLE);

        ImageView prima_linea_step = findViewById(R.id.lineBar1);
        prima_linea_step.setColorFilter(Color.rgb(176, 255, 87));
        prima_linea_step.setVisibility(View.VISIBLE);

        ImageView secondo_step = findViewById(R.id.myImageView2);
        secondo_step.setColorFilter(Color.rgb(176, 255, 87));
        secondo_step.setVisibility(View.VISIBLE);
        TextView testoStep2 = findViewById(R.id.testoStep2);
        testoStep2.setVisibility(View.VISIBLE);

        ImageView seconda_linea_step = findViewById(R.id.lineBar2);
        seconda_linea_step.setColorFilter(Color.rgb(176, 255, 87));
        seconda_linea_step.setVisibility(View.VISIBLE);

        ImageView terzo_step = findViewById(R.id.myImageView3);
        terzo_step.setColorFilter(Color.rgb(176, 255, 87));
        terzo_step.setVisibility(View.VISIBLE);
        TextView testoStep3 = findViewById(R.id.testoStep3);
        testoStep3.setVisibility(View.VISIBLE);

        ImageView terza_linea_step = findViewById(R.id.lineBar3);
        terza_linea_step.setColorFilter(Color.rgb(176, 255, 87));
        terza_linea_step.setVisibility(View.VISIBLE);

        ImageView quarto_step = findViewById(R.id.myImageView4);
        quarto_step.setColorFilter(Color.rgb(176, 255, 87));
        quarto_step.setVisibility(View.VISIBLE);
        TextView testoStep4 = findViewById(R.id.testoStep4);
        testoStep4.setVisibility(View.VISIBLE);

        ImageView quarta_linea_step = findViewById(R.id.lineBar4);
        quarta_linea_step.setColorFilter(Color.rgb(176, 255, 87));
        quarta_linea_step.setVisibility(View.VISIBLE);

        ImageView quinto_step = findViewById(R.id.myImageView5);
        quinto_step.setColorFilter(Color.rgb(50, 203, 0));
        quinto_step.setVisibility(View.VISIBLE);
        TextView testoStep5 = findViewById(R.id.testoStep5);
        testoStep5.setVisibility(View.VISIBLE);
    }


}
