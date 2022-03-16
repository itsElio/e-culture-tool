package com.example.e_culturetoolbakers.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.e_culturetoolbakers.ApriPercorso;
import com.example.e_culturetoolbakers.HomeFragment;
import com.example.e_culturetoolbakers.MainActivity;
import com.example.e_culturetoolbakers.R;
import com.example.e_culturetoolbakers.RiepilogoPercorso;
import com.example.e_culturetoolbakers.Test;

import java.util.ArrayList;

public class ListaPercorsiAdapter extends ArrayAdapter<String>  {
    private FragmentActivity context;

    public ListaPercorsiAdapter(FragmentActivity context, int textViewResourceId, ArrayList<String> lista) {
        super(context, textViewResourceId, lista);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.lista_percorsi, null);

        TextView textPercorso = (TextView)convertView.findViewById(R.id.nomePercorso);
        String percorso = getItem(position);

        String[] parts = percorso.split("-");
        textPercorso.setText(parts[1]);

        //Button apri = (Button) convertView.findViewById(R.id.apri);

        textPercorso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ApriPercorso.class);
                intent.putExtra("filename", percorso);
                context.startActivity(intent);
            }
        });

        return convertView;
    }



}
