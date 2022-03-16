package com.example.e_culturetoolbakers.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.view.*;
import android.widget.*;
import android.widget.BaseAdapter;

import com.example.e_culturetoolbakers.R;
import com.example.e_culturetoolbakers.SelezionaZone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ListaZoneAdapter extends ArrayAdapter<String> implements Serializable {

    private SelezionaZone selezionaZone;
    private String selezionato;

    public ListaZoneAdapter(SelezionaZone context, int textViewResourceId,
                            ArrayList<String> lista) {
        super(context, textViewResourceId, lista);
        selezionaZone = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.lista_zone, null);


        TextView zonaView = (TextView)convertView.findViewById(R.id.nomeZona);
        String zona = getItem(position);

        if (zona.equals(selezionato)) {
            // set selected your color
            convertView.setBackgroundColor(Color.parseColor("#5880c7"));
        }else{
            //set default color
        }

        zonaView.setText(zona);
        ImageView cancella = (ImageView)convertView.findViewById(R.id.iconaX);

        String finalZona = zona;
        cancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(finalZona);
                if(finalZona.equals(selezionato)){
                    selezionaZone.setZona(null);
                }
            }
        });

        return convertView;
    }

    public void setSelezionato(String sel){
        selezionato = sel;
    }

}
