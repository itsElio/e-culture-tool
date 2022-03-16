package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.e_culturetoolbakers.model.Museo;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrazioneMuseo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_museo);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Registrazione museo");
*/
    // Assegnamo a delle variabili i valori inseriti nel form;
        TextInputLayout nome = (TextInputLayout ) findViewById(R.id.nomeMuseo);
        TextInputLayout tipo = (TextInputLayout ) findViewById(R.id.tipoMuseo);
        TextInputLayout informazioni = (TextInputLayout ) findViewById(R.id.informazioniMuseo);


        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable nomeMuseo = nome.getEditText().getText();
                Editable tipoMuseo = tipo.getEditText().getText();
                Editable informazioniMuseo = informazioni.getEditText().getText();

                Intent riepilogoZone = new Intent(RegistrazioneMuseo.this, RiepilogoZone.class);

                //String titoloStringa = titolo.toString();
                if(TextUtils.isEmpty(nomeMuseo) || TextUtils.isEmpty(tipoMuseo) || TextUtils.isEmpty(informazioniMuseo)) {
                    Toast.makeText(RegistrazioneMuseo.this, "Compila tutti i campi prima di proseguire", Toast.LENGTH_SHORT).show();
                } else {
                    Museo nuovoMuseo = new Museo(nomeMuseo.toString(), informazioniMuseo.toString(), tipoMuseo.toString());
                    Bundle museo = new Bundle();
                    museo.putSerializable("museoObject", nuovoMuseo);
                    riepilogoZone.putExtras(museo);
                    RegistrazioneMuseo.this.startActivity(riepilogoZone);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        TextInputLayout nome = findViewById(R.id.nomeMuseo);
        CharSequence nomeText = nome.getEditText().getText();
        outState.putCharSequence("nome", nomeText);

        TextInputLayout tipo = findViewById(R.id.tipoMuseo);
        CharSequence tipoText = tipo.getEditText().getText();
        outState.putCharSequence("tipo", tipoText);


        TextInputLayout descrizione = findViewById(R.id.informazioniMuseo);
        CharSequence descrizioneText = descrizione.getEditText().getText();
        outState.putCharSequence("descrizione", descrizioneText);
        /*
        Editable nomeMuseo = nome.getEditText().getText();
        Editable tipoMuseo = tipo.getEditText().getText();
        Editable informazioniMuseo = informazioni.getEditText().getText();
*/
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextInputLayout nome = findViewById(R.id.nomeMuseo);
        TextInputLayout tipo = findViewById(R.id.tipoMuseo);
        TextInputLayout descrizione = findViewById(R.id.informazioniMuseo);

        CharSequence nomeSalvato = savedInstanceState.getCharSequence("nome");
        CharSequence tipoSalvato = savedInstanceState.getCharSequence("tipo");
        CharSequence descrizioneSalvato = savedInstanceState.getCharSequence("descrizione");

        nome.getEditText().setText(nomeSalvato);
        tipo.getEditText().setText(tipoSalvato);
        descrizione.getEditText().setText(descrizioneSalvato);
    }
}