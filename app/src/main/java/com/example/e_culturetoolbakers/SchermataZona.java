package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Scanner;

public class SchermataZona extends AppCompatActivity {

    private TextView nomeText;
    private TextView descrizione;
    private ImageView foto;
    private ArrayList<String> opere;
    private ArrayList<String> dati;
    private DatabaseHelper db_helper;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Button modifica;
    private String fotoString;
    private String nome;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schermata_zona);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        nome = intent.getStringExtra("nome");

        db_helper = new DatabaseHelper(this);
        opere = new ArrayList<>();
        dati = db_helper.getDatiZona(nome, opere);

        nomeText = findViewById(R.id.nomeZona);
        descrizione = findViewById(R.id.descrizioneDettagliZona);
        foto = findViewById(R.id.imgZona);

        nomeText.setText(nome);
        descrizione.setText("Luogo: " + dati.get(2) + "\n" + dati.get(1));
        descrizione.setMovementMethod(new ScrollingMovementMethod());

        listView = (ListView) findViewById(R.id.listaOpereSchermataOpere);
        ///Ciò che fa è disabilitare TouchEvents su ScrollView e fare in modo che ListView sia scollabile.
        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Non consentire a ScrollView di intercettare gli eventi touch.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Consenti a ScrollView di intercettare gli eventi touch.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        ///
        adapter = new ArrayAdapter<String>(this,   android.R.layout.simple_list_item_1, opere);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent intent = new Intent(SchermataZona.this, SchermataOpera.class);
                intent.putExtra("nome", String.valueOf(listView.getItemAtPosition(pos)));
                startActivity(intent);
            }
        });

        fotoString = dati.get(3);

        //Toast.makeText(getApplicationContext(), fotoString, Toast.LENGTH_LONG).show();

        if(fotoString!= null && !fotoString.isEmpty()){
            //SOLO SE é CURATORE
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser mUser = mAuth.getCurrentUser();

            if(mUser!=null){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("type").getValue().toString().equals("cur")){
                            modifica = findViewById(R.id.buttonMoficaZonaRicerca);
                            modifica.setVisibility(View.VISIBLE);
                            modifica.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Museo museo = db_helper.getMuseoZona(Integer.valueOf(dati.get(0)));
                                    Zona zona = new Zona(nome, dati.get(1), dati.get(3), Integer.valueOf(dati.get(0)));
                                    Intent intent = new Intent(SchermataZona.this, ModificaZonaRicerca.class);
                                    intent.putExtra("museo", museo);
                                    intent.putExtra("zona", zona);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    String url = "gs://e-culture-tool-c381c.appspot.com/images/";

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference ref1 = storage.getReferenceFromUrl(url);

                    ref1.child(fotoString).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(foto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            foto.setImageResource(R.drawable.placeholder);
                        }
                    });
                }
            }, 2000);

        } else{
            foto.setImageResource(R.drawable.placeholder);
        }






    }
}