package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class RiepilogoRegistrazione extends AppCompatActivity {

    private ListView riepilogoOpereZoneList;
    //private ArrayList<String> opereList  = new ArrayList<String>();
    private ExpandableListView expandableListZone;
    private Button confirmBtn;
    private ArrayList<String> zoneList = new ArrayList<String>();
    public HashMap<String, ArrayList<String>> listZoneOpere = new HashMap<>();
    RiepilogoRegistrazioneAdapter adapter;
    private Museo nuovoMuseo;
    // Firebase stuff
    private StorageReference mStorageRef;
    private StorageReference opereRef;
    private StorageReference zoneRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_registrazione);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bundle museo = getIntent().getExtras();

        Serializable serializedMuseo = museo.getSerializable("museoObject");
        nuovoMuseo = (Museo) serializedMuseo;

        expandableListZone = (ExpandableListView) findViewById(R.id.expandableZone);

        TextView nomeMuseo = (TextView) findViewById(R.id.nomeMuseo);
        nomeMuseo.setText("Museo: " + nuovoMuseo.getNome());

        TextView descrizioneMuseo = (TextView) findViewById(R.id.descrizioneMuseo);
        descrizioneMuseo.setText("Descrizione del museo: " + nuovoMuseo.getInformazioni());

        for (Zona zona : nuovoMuseo.getZone()) {
            zoneList.add(zona.getNome());
            ArrayList<String> opereList = new ArrayList<String>();
            for (Opera opera : zona.opere) {

                opereList.add(opera.getNomeOpera());
            }
            // Inseriamo l'ArrayList nell'HashMap
            listZoneOpere.put(zona.getNome(), opereList);
        }

        adapter = new RiepilogoRegistrazioneAdapter(RiepilogoRegistrazione.this, zoneList, listZoneOpere, nuovoMuseo);
        expandableListZone.setAdapter(adapter);
        confirmBtn = findViewById(R.id.buttonConferma);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        /* Check connessione
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }

        if(connected == false) {
            System.out.println("Non è connesso!!!");
        }
        */


        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference("Musei");

                myRef.child(nuovoMuseo.getNome()).child("nome").setValue(nuovoMuseo.getNome());
                myRef.child(nuovoMuseo.getNome()).child("info").setValue(nuovoMuseo.getInformazioni());
                myRef.child(nuovoMuseo.getNome()).child("tipo").setValue(nuovoMuseo.getTipo());

                for (Zona zona : nuovoMuseo.getZone()) {
                    myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("nome").setValue(zona.getNome());
                    myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("descrizione").setValue(zona.getDescrizione());

                    String newZonaString = zona.getFoto().substring(zona.getFoto().lastIndexOf("/") + 1);
                    System.out.println(newZonaString);

                    myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("foto").setValue(newZonaString);

                    for (Opera opera : zona.opere) {
                        myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("opere").child(opera.getNome()).setValue(opera.getNome());
                        myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("opere").child(opera.getNome()).child("descrizione").setValue(opera.getDescrizione());

                        //Toast.makeText(getApplicationContext(), opera.getFoto(), Toast.LENGTH_LONG).show();
                        String newOperaString = opera.getFoto().substring(opera.getFoto().lastIndexOf("/") + 1);
                        //Toast.makeText(getApplicationContext(), newOperaString, Toast.LENGTH_LONG).show();

                        myRef.child(nuovoMuseo.getNome()).child("zone").child(zona.getNome()).child("opere").child(opera.getNome()).child("foto").setValue(newOperaString);



                        Uri file = Uri.fromFile(new File(opera.getFoto()));
                        // String newString = myString.substring(myString.lastIndexOf("/")+1, myString.indexOf("."));
                        // Questo if rimuove la parte file : // ecc ecc prima del file, controllare
                        // Il "nome" della foto ricevuta da galleria
                        // Adattarlo all'aggiornamento su database
                        // Trova relazione tra foto e opere

                        opereRef = mStorageRef.child("images/" + newOperaString);


                        opereRef.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                        // ...

                                    }
                                });
                    }
                    Uri file = Uri.fromFile(new File(zona.getFoto()));
                    zoneRef = mStorageRef.child("images/" + newZonaString);
                    zoneRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...

                                }
                            });
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("zoneOpere", listZoneOpere);
        outState.putSerializable("zoneList", zoneList);
        outState.putSerializable("nuovoMuseo", nuovoMuseo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listZoneOpere = (HashMap<String, ArrayList<String>>) savedInstanceState.getSerializable("zoneOpere");
        zoneList = (ArrayList<String>) savedInstanceState.getSerializable("zoneList");

        adapter = new RiepilogoRegistrazioneAdapter(RiepilogoRegistrazione.this, zoneList, listZoneOpere, nuovoMuseo);

        expandableListZone = (ExpandableListView) findViewById(R.id.expandableZone);
        expandableListZone.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        //Adapter non funziona quando si gira il telefono, però se si gira di nuovo sì
    }
}