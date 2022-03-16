package com.example.e_culturetoolbakers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class CambiaLingua extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_lingua);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button button =(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale current = getResources().getConfiguration().locale;
                if(!current.toString().equals("en") && !current.toString().equals("en_US") && !current.toString().equals("en_us")) {

                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(CambiaLingua.this, Test.class);
                    CambiaLingua.this.startActivity(myIntent);
                } else {
                    Locale locale = new Locale("it_IT");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(CambiaLingua.this, Test.class);
                    CambiaLingua.this.startActivity(myIntent);
                }
            }
        });
    }
}