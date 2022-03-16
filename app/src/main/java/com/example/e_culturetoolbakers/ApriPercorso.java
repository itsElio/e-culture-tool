package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culturetoolbakers.adapter.ListaRiepilogoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ApriPercorso extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;

    private ListView listaDiRiepilogo;
    private String fileTestualeInFormatoJson;
    private String fileTestualeLeggibile;
    private String fileName;
    private TextView titoloRiepilogo;

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate del menu...aggiunge il menu alla toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.salva) {
            rendiLeggibile();
            salvaFile();
            try {
                visualizzaPercorsoDaFormatoJSON(fileTestualeInFormatoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.condividi) {
            rendiLeggibile();
            sendMessage(fileTestualeLeggibile);
            try {
                visualizzaPercorsoDaFormatoJSON(fileTestualeInFormatoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.cancella) {
            cancellaFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void rendiLeggibile() {
        JSONObject jsonObject;
        fileTestualeLeggibile = null; //svuoto
        String [] temp = fileName.split("-");
        fileTestualeLeggibile = "NOME MUSEO: " + temp[0] + "\n";
        fileTestualeLeggibile += "NOME PERCORSO: " + temp[1] + "\n";
        fileTestualeLeggibile += "\nZONE DEL PERCORSO e relative opere:\n";
        try {
            jsonObject = new JSONObject(fileTestualeInFormatoJson);

            JSONArray jsonArray = jsonObject.getJSONArray("nodes");
            ArrayList<String> arrayZone = new ArrayList<>();
            int numZone=1;
            for(int i = 0; i< jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String nomeZona= obj.getString("id");
                String tipo  = nomeZona.substring(0,2);
                nomeZona = nomeZona.substring(2);
                if(tipo.startsWith("z")) {
                    fileTestualeLeggibile += "\n" + numZone + ") " + nomeZona;
                    //ricerca opere
                    JSONArray jsonArray2 = jsonObject.getJSONArray("edges");

                    for(int j = 0; j< jsonArray2.length();j++) {
                        JSONObject obj2 = jsonArray2.getJSONObject(j);
                        String nomeZonaCollegamento = obj2.getString("source");
                        nomeZonaCollegamento = nomeZonaCollegamento.substring(2);
                        if (nomeZonaCollegamento.equals(nomeZona)) {
                            String nomeOpera = obj2.getString("target");
                            //Potrebbe esssere un legame tra 2 zone ...quindi verifico che si tratti di un opera

                            if (!nomeOpera.substring(0, 2).startsWith("z"))//è un opera e la prendo
                                fileTestualeLeggibile += "\n   - " + nomeOpera.substring(2);
                        }
                    }
                    fileTestualeLeggibile += "\n";
                }
                numZone++;
            }
            ListaRiepilogoAdapter arrayAdapter = new ListaRiepilogoAdapter(this, arrayZone,fileTestualeInFormatoJson);
            listaDiRiepilogo.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cancellaFile(){
        File file = new File( getFilesDir(), fileName);
        file.delete();
        Toast.makeText(this, R.string.percorso_cancellato, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(ApriPercorso.this, MainActivity.class);
        startActivity(myIntent);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean checkPermission(String permessi) {
        int check = ContextCompat.checkSelfPermission(this,permessi);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void salvaFile() {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Salva in: /storage/emulated/0/Downloads.
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName.substring(0,fileName.length() -5)+".txt"); // tolgo .json dal nome

            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(fileTestualeLeggibile.getBytes(StandardCharsets.UTF_8));
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this,R.string.file_salvato ,Toast.LENGTH_LONG).show();
        } else {
            requestPermissions();
        }

    }
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //mostro spiegazione del permesso richeisto
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permesso_scrittura_req1)
                    .setMessage(R.string.permesso_scrittura_req2)
                    .setPositiveButton(R.string.conferma, (dialogInterface, i) -> ActivityCompat.requestPermissions(
                            ApriPercorso.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton(R.string.annulla,
                            (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permesso concesso", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.permesso_scrittura);
                builder.setPositiveButton(R.string.chiudi, (dialog, which) -> { });
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(this, "Permesso rifiutato", Toast.LENGTH_LONG).show();
            }
        }
    }

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

        String[] parts = fileName.split("-");

        titoloRiepilogo.setText(parts[0] + " :");

        Button button = findViewById(R.id.buttonModifica);

        TextView textView = findViewById(R.id.titoloRiepilogo);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
        params.setMargins(30, 5, 30, 0); //substitute parameters for left, top, right, bottom

        textView.setLayoutParams(params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ApriPercorso.this, SelezionaTipoPercorso.class);
                myIntent.putExtra("mode", "mod");
                myIntent.putExtra("filename", fileName);
                startActivity(myIntent);
            }
        });


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
        titoloRiepilogo = findViewById(R.id.titoloRiepilogo);
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

    private void sendMessage(String message)
    {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

            Intent chooser = Intent.createChooser(intent,getString(R.string.scelta_app_condivisione_percorso));

        // Verifico che un'app sia disponibile
        if (chooser.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.app_per_condividere_non_trovata, Toast.LENGTH_SHORT).show();
            return;
        }

        // Avvio l'app scelta
        startActivity(chooser);
    }
}