package com.example.e_culturetoolbakers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.google.android.material.textfield.TextInputLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class SelezionaTipoPercorso extends AppCompatActivity {

    private ImageView primo_step;
    private ImageView secondo_step;
    private ImageView prima_linea_step;
    private Toolbar myToolbar;
    private TextInputLayout nomePercorosoLayout;
    private Button  bottonePercorsoSemplice;
    private Button bottonePercorsoComplesso;
    private DatabaseHelper db_helper;
    private ImageView info0;
    private ImageView info1;
    private String filename = "";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_percorso);

        initializeWidget();

        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        // Mostra pulsante indietro
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        Serializable serializedMuseo = getIntent().getSerializableExtra("Museo");
        Museo nuovoMuseo = (Museo) serializedMuseo;

        if(mode.equals("crea")) {
            creaBarra();
        } else {
            //DB
            db_helper = new DatabaseHelper(this);
            ArrayList<Museo> listaMuseiLocali = db_helper.getListMuseo();
            String[] parts = intent.getStringExtra("filename").split("-");

            for(int i=0; i<listaMuseiLocali.size(); i++) {
                if(listaMuseiLocali.get(i).getNome().equals(parts[0])) {
                    nuovoMuseo = listaMuseiLocali.get(i);
                    break;
                }
            }

            nomePercorosoLayout.getEditText().setText(parts[1]);
        }

        info0 = findViewById(R.id.info0);
        info1 = findViewById(R.id.info1);

        info0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelezionaTipoPercorso.this);
                builder.setTitle(R.string.perc_semplice_hint);

                builder.setPositiveButton(R.string.chiudi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelezionaTipoPercorso.this);
                builder.setTitle(R.string.perc_complesso_hint);

                builder.setPositiveButton(R.string.chiudi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //comparsa e scomparsa del messaggio d'errore
        Objects.requireNonNull(nomePercorosoLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > nomePercorosoLayout.getCounterMaxLength())
                    nomePercorosoLayout.setError(getString(R.string.nome_troppo_lungo));
                else
                    nomePercorosoLayout.setError(null);
            }
        });

        Museo finalNuovoMuseo = nuovoMuseo;
        bottonePercorsoSemplice.setOnClickListener(v -> apriSelezionaZonePercorso(0, finalNuovoMuseo, intent));
        bottonePercorsoComplesso.setOnClickListener(v -> apriSelezionaZonePercorso(1,finalNuovoMuseo, intent));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nome", String.valueOf(nomePercorosoLayout.getEditText().getText()));
        outState.putString("filename", filename);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nomePercorosoLayout.getEditText().setText(savedInstanceState.getString("nome"));
        filename = savedInstanceState.getString("filename");

    }


    private void creaBarra(){
        //Step progress bar
        primo_step.setColorFilter(Color.rgb(176, 255, 87));
        prima_linea_step.setColorFilter(Color.rgb(176, 255, 87));
        secondo_step.setColorFilter(Color.rgb(50, 203, 0));
        LinearLayout linearLayout = findViewById(R.id.stepBar);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void initializeWidget() {
        myToolbar = findViewById(R.id.toolbar1);
        bottonePercorsoSemplice = findViewById(R.id.bottonePercorsoSemplice);
        bottonePercorsoComplesso = findViewById(R.id.bottonePercorsoComplesso);
        nomePercorosoLayout = findViewById(R.id.textInputLayout);
        primo_step = findViewById(R.id.myImageView);
        prima_linea_step = findViewById(R.id.lineBar1);
        secondo_step = findViewById(R.id.myImageView2);
    }

    public void apriSelezionaZonePercorso(int tipo, Museo nuovoMuseo, Intent previousIntent) {
        Intent intent = new Intent(this, SelezionaZone.class);
        String namePath = Objects.requireNonNull(nomePercorosoLayout.getEditText()).getText().toString().trim();
        if (namePath.isEmpty()) {
            nomePercorosoLayout.setErrorEnabled(true);
            nomePercorosoLayout.setError(getString(R.string.campo_richiesto));
        } else if (namePath.length() <= nomePercorosoLayout.getCounterMaxLength()) {
            intent = new Intent(this, SelezionaZone.class);
            intent.putExtra("tipoPercorso",tipo);
            intent.putExtra("nomePercorso",namePath);
            intent.putExtra("Museo",nuovoMuseo);
            intent.putExtra("mode", previousIntent.getStringExtra("mode"));

            if(previousIntent.getStringExtra("mode").equals("mod")) {
                intent.putExtra("filename", previousIntent.getStringExtra("filename"));
            }

            startActivity(intent);
        }
    }
}