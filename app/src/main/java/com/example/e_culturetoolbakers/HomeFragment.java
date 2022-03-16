package com.example.e_culturetoolbakers;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_culturetoolbakers.adapter.ListaPercorsiAdapter;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements ValueEventListener {


    private FloatingActionButton fab;
    private DatabaseHelper db_helper;
    private DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listaPercorsi = (ListView) view.findViewById(R.id.listaPercorsi);
        ArrayList<String> listaFile = new ArrayList<>();

        File[] files = getActivity().getFilesDir().listFiles();
        for(int i=0; i<files.length; i++){
            listaFile.add(files[i].getName());
        }

        ListaPercorsiAdapter adapter = new ListaPercorsiAdapter(getActivity(), R.layout.lista_percorsi, listaFile);
        listaPercorsi.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        fab = view.findViewById(R.id.add_fab);
        db_helper = new DatabaseHelper(getContext());

        if (mUser != null) {
            //Controlla che il DB esiste
            File database = getContext().getDatabasePath(DatabaseHelper.DB_NAME);

            if (!database.exists()) {
                //db_helper.aggiorna();
                db_helper.getReadableDatabase();
                //Copia DB
                if (copiaDatabase(getContext())) {
                    //Toast.makeText(getContext(), "Copia del DB fatta!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getContext(), "Copia del DB fallita", Toast.LENGTH_SHORT).show();
                }
            }else{
                db_helper.aggiorna();
                copiaDatabase(getContext());
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

            fab.setClickable(true);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getActivity(), SelezionaMuseo.class);
                    myIntent.putExtra("mode", "crea");
                    startActivity(myIntent);
                }
            });
            aggiornaDaFirebase();


        } else {
            //Controlla che il DB esiste
            File database = getContext().getDatabasePath(DatabaseHelper.DB_NAME);

            if (!database.exists()) {
                //db_helper.aggiorna();
                db_helper.getReadableDatabase();
                //Copia DB
                if (copiaDatabase(getContext())) {
                    //Toast.makeText(getContext(), "Copia del DB fatta!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getContext(), "Copia del DB fallita", Toast.LENGTH_SHORT).show();
                }
            }else{
                db_helper.aggiorna();
                copiaDatabase(getContext());
            }
            fab.setClickable(true);
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#525a69")));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), R.string.loggati, Toast.LENGTH_LONG).show();
                }
            });


        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(ref!=null){
            ref.removeEventListener(this);
        }
    }

    private boolean copiaDatabase (Context context) {
        try{
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DB_NAME);//Leggo il file assets
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
            Log.v("Seleziona Museo","DB copiato");
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void aggiornaDaFirebase() {


        ArrayList<Museo> listaMusei = db_helper.getListMuseo();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Musei");

        ref.addValueEventListener(this);

        /*ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    System.out.println(dataSnapshot.getValue());
                    String nome = dataSnapshot.child("nome").getValue().toString();
                    String info = dataSnapshot.child("info").getValue().toString();
                    String tipo = dataSnapshot.child("tipo").getValue().toString();

                    Museo museo = new Museo(nome, info, tipo);

                    boolean trov = false;
                    for(Museo museoTemp : listaMusei){
                        if(museoTemp.getNome().equals(museo.getNome())){
                            //Trovato museo
                            trov = true;

                            db_helper.updateMuseo(nome, info, tipo);

                        }
                    }

                    if(trov == false){
                        db_helper.inserisciMuseo(nome, info, tipo);
                        int id = db_helper.getIdMuseo(nome);

                        // Ciclo tra le zone
                        for(DataSnapshot snapshot1 : dataSnapshot.child("zone").getChildren()) {

                            String nomeZona = snapshot1.getKey().toString();
                            String descrizioneZona = snapshot1.child("descrizione").getValue().toString();
                            String fotoZona =null;
                            Zona zona;
                            if(snapshot1.child("foto").getValue() != null){
                                fotoZona = snapshot1.child("foto").getValue().toString();
                                zona = new Zona(nomeZona, descrizioneZona, fotoZona);
                                museo.addZona(zona.getNome(), zona.getDescrizione(), zona.getFoto());
                            }

                            db_helper.inserisciZona(nomeZona, descrizioneZona, id);
                            int id_zona = db_helper.getIdZona(nomeZona);

                            // Ciclo tra le opere
                            for (DataSnapshot snapshot2 : snapshot1.child("opere").getChildren()) {

                                String nomeOpera = snapshot2.getKey().toString();
                                String descrizioneOpera = snapshot2.child("descrizione").getValue().toString();
                                String fotoOpera = null;
                                if(snapshot2.child("foto").getValue() != null){
                                    fotoOpera = snapshot2.child("foto").getValue().toString();
                                    museo.findZona(nomeZona).addOpera(nomeOpera, descrizioneOpera, fotoOpera);
                                }
                                db_helper.inserisciOpera(nomeOpera, descrizioneOpera, id_zona, fotoOpera);

                            }
                        }
                        // Dopo questo blocco si perde ref all'oggetto museo.

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        ArrayList<Museo> listaMusei = db_helper.getListMuseo();
        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
            System.out.println(dataSnapshot.getValue());
            String nome = dataSnapshot.child("nome").getValue().toString();
            String info = dataSnapshot.child("info").getValue().toString();
            String tipo = dataSnapshot.child("tipo").getValue().toString();

            Museo museo = new Museo(nome, info, tipo);

            boolean trov = false;
            for(Museo museoTemp : listaMusei){
                if(museoTemp.getNome().equals(museo.getNome())){
                    //Trovato museo
                    trov = true;

                    db_helper.updateMuseo(nome, info, tipo);

                }
            }

            if(trov == false){
                db_helper.inserisciMuseo(nome, info, tipo);
                int id = db_helper.getIdMuseo(nome);

                // Ciclo tra le zone
                for(DataSnapshot snapshot1 : dataSnapshot.child("zone").getChildren()) {

                    String nomeZona = snapshot1.getKey().toString();
                    String descrizioneZona = snapshot1.child("descrizione").getValue().toString();
                    String fotoZona =null;
                    Zona zona;
                    if(snapshot1.child("foto").getValue() != null){
                        fotoZona = snapshot1.child("foto").getValue().toString();
                        zona = new Zona(nomeZona, descrizioneZona, fotoZona);
                        museo.addZona(zona.getNome(), zona.getDescrizione(), zona.getFoto());
                    }

                    db_helper.inserisciZona(nomeZona, descrizioneZona, id, fotoZona);
                    int id_zona = db_helper.getIdZona(nomeZona);

                    // Ciclo tra le opere
                    for (DataSnapshot snapshot2 : snapshot1.child("opere").getChildren()) {

                        String nomeOpera = snapshot2.getKey().toString();
                        String descrizioneOpera = snapshot2.child("descrizione").getValue().toString();
                        String fotoOpera = null;
                        if(snapshot2.child("foto").getValue() != null){
                            fotoOpera = snapshot2.child("foto").getValue().toString();
                            museo.findZona(nomeZona).addOpera(nomeOpera, descrizioneOpera, fotoOpera);
                        }
                        db_helper.inserisciOpera(nomeOpera, descrizioneOpera, id_zona, fotoOpera);

                    }
                }
                // Dopo questo blocco si perde ref all'oggetto museo.

            }



        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}