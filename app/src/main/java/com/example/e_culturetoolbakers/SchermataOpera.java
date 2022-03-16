package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class SchermataOpera extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseDatabase database;

    private TextView nomeText;
    private TextView descrizione;
    private TextView descrizione1;
    private TextView descrizione2;
    private ImageView foto;
    private ArrayList<String> dati;
    private DatabaseHelper db_helper;
    private String fotoString;
    private String nome;
    private Button modifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schermata_opera);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        nome = intent.getStringExtra("nome");

        db_helper = new DatabaseHelper(this);
        dati = db_helper.getDatiOpera(nome);

        nomeText = findViewById(R.id.nomeOpera);
        descrizione = findViewById(R.id.descrizioneDettagli);
        descrizione1 = findViewById(R.id.descrizioneDettagli1);
        descrizione2 = findViewById(R.id.descrizioneDettagli2);
        foto = findViewById(R.id.imgOpera);

        nomeText.setText(nome);
        descrizione.setText(dati.get(1));
        descrizione.setMovementMethod(new ScrollingMovementMethod());
        descrizione1.setText(dati.get(2) + " - Zona: " + dati.get(3));
        descrizione2.setText("Codice attività: " + dati.get(0) );


        fotoString = dati.get(4);



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
                            modifica = findViewById(R.id.buttonMoficaOperaRicerca);
                            modifica.setVisibility(View.VISIBLE);
                            modifica.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Opera opera = new Opera(nome, dati.get(1), Integer.valueOf(dati.get(0)), dati.get(4));
                                    Museo museo = db_helper.getMuseoOpera(Integer.valueOf(dati.get(0)));
                                    Zona zona = db_helper.getZonaOpera(Integer.valueOf(dati.get(0)));
                                    Intent intent = new Intent(SchermataOpera.this, ModificaOperaRicerca.class);
                                    intent.putExtra("museo", museo);
                                    intent.putExtra("zona", zona);
                                    intent.putExtra("opera", opera);
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






        /*mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance("https://e-culture-tool-c381c-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference("Musei");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for(DataSnapshot snapshot1 : dataSnapshot.child("zone").getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot1.child("opere").getChildren()) {

                            String nomeOpera = snapshot2.getKey().toString();
                            if(nomeOpera.equals(nome)){
                                fotoString = snapshot2.child("foto").getValue().toString();
                                //Toast.makeText(getApplicationContext(), fotoString, Toast.LENGTH_LONG).show();

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
                                        // Handle any errors
                                    }
                                });
                                return;
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/


    }

}