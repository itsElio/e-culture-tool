package com.example.e_culturetoolbakers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.e_culturetoolbakers.adapter.ListMuseiAdapter;
import com.example.e_culturetoolbakers.adapter.ListaZoneAdapter;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SelezionaMuseo extends AppCompatActivity {

    private ArrayList<Museo> listaMuseiLocali;
    private DatabaseHelper db_helper;
    private ListMuseiAdapter adapterMusei;

    private AutoCompleteTextView autoCompleteText;
    private ImageView iv;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_museo);

        Toolbar myToolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        ActionBar actionBar = getSupportActionBar();

        // Mostra pulsante indietro
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv = findViewById(R.id.myImageView);
        iv.setColorFilter(Color.rgb(50, 203, 0));


        //DB
        db_helper = new DatabaseHelper(this);
        //Carica i dati
        listaMuseiLocali = db_helper.getListMuseo();



        autoCompleteText = findViewById(R.id.auto_complete_txt);
        autoCompleteText.setText(listaMuseiLocali.get(0).getNome());
        adapterMusei = new ListMuseiAdapter(this, listaMuseiLocali);
        autoCompleteText.setThreshold(1);
        autoCompleteText.setAdapter(adapterMusei);
        autoCompleteText.setOnTouchListener((paramView, paramMotionEvent) -> {
            if (listaMuseiLocali.size() > 0) {
                TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
                if (textInputLayout.getEditText().toString().equals(""))
                    adapterMusei.getFilter().filter(null);
                autoCompleteText.showDropDown();
            }
            return false;
        });

        Button bottoneVai_a_sceltaTipoPercorso= findViewById(R.id.bottoneVai_a_sceltaTipoPercorso);
        bottoneVai_a_sceltaTipoPercorso.setOnClickListener(v -> apriSceltaTipoPercorso(mode));

        autoCompleteText.setOnItemClickListener((adapterMusei, view, i, l) -> {
            Museo museo = (Museo) adapterMusei.getItemAtPosition(i);
        });


    }


    private void apriSceltaTipoPercorso(String mode) {
        if (!searchMuseo(autoCompleteText.getText().toString())) {
            TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(String.valueOf(R.string.museo_non_presente));
        } else {
            Intent intent = new Intent(this, SelezionaTipoPercorso.class);
            intent.putExtra("Museo",listaMuseiLocali.get(indiceSelezionato()));
            intent.putExtra("mode", mode);
            startActivity(intent);
        }
    }

    private int indiceSelezionato() {
        for (int i=0;i<listaMuseiLocali.size();i++){
            if(listaMuseiLocali.get(i).getNome().equals(autoCompleteText.getText().toString()))
                return i;
        }
        return 0;
    }

    private boolean searchMuseo(String text) {
        for (int i=0;i<listaMuseiLocali.size();i++){
           if( listaMuseiLocali.get(i).getNome().equals(text))
               return true;
        }
        return false;
    }

}