package com.example.e_culturetoolbakers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.e_culturetoolbakers.adapter.ListaPercorsiAdapter;
import com.example.e_culturetoolbakers.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Test extends AppCompatActivity {

/// ACTIVITY DA USARE SOLO PER TEST

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button nuovo = (Button) findViewById(R.id.buttonNuovo);
        nuovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Test.this, SelezionaMuseo.class);
                myIntent.putExtra("mode", "crea");
                startActivity(myIntent);
            }
        });

        ListView listaPercorsi = (ListView) findViewById(R.id.listaPercorsi);
        ArrayList<String> listaFile = new ArrayList<>();

        File[] files = getFilesDir().listFiles();
        for(int i=0; i<files.length; i++){
            listaFile.add(files[i].getName());
        }

        //ListaPercorsiAdapter adapter = new ListaPercorsiAdapter(this, R.layout.lista_percorsi, listaFile);
        //listaPercorsi.setAdapter(adapter);

        Button buttonDownload = (Button) findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(getExternalFilesDir(null) + File.separator + "musei_db.sqlite");
                if(file.exists()){
                    Toast.makeText(Test.this, "Dati già scaricati!", Toast.LENGTH_SHORT).show();
                }else {
                    String url = "https://prenotazionetamponi.altervista.org/musei_db.sqlite";
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription("");
                    request.setTitle("Download dati");
                    // in order for this if to run, you must use the android 3.2 to compile your app
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setDestinationInExternalFilesDir(Test.this, null, "musei_db.sqlite");

                    // get download service and enqueue file
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(Test.this, "Dati scaricati!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button buttonCarica = (Button) findViewById(R.id.buttonCarica);
        buttonCarica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db_helper = new DatabaseHelper(Test.this);

                db_helper.getReadableDatabase();
                //Copia DB
                if (copiaDatabase(Test.this)){
                    Toast.makeText(Test.this, "Copia del DB fatta!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Test.this, "Copia del DB fallita", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        Button buttonQR = (Button) findViewById(R.id.buttonQR);
        buttonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Test.this, QRScanner.class);
                startActivity(myIntent);
            }
        });



        Button firebase = (Button) findViewById(R.id.buttonFirebase);
        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(Test.this, testFirebase.class);
                startActivity(myIntent);
            }
        });

        Button museo = (Button) findViewById(R.id.buttonMuseo);
        museo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(Test.this, CambiaLingua.class);
                startActivity(myIntent);
            }
        });



    }

    private boolean copiaDatabase (Context context) {
        try{
            File file = new File(getExternalFilesDir(null) + File.separator + "musei_db.sqlite");
            FileInputStream inputStream = new FileInputStream(file);
            file.delete();
            String outFileName = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
            File f = new File(outFileName);
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[]buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) >0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            return false;
        }
    }
}