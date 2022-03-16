package com.example.e_culturetoolbakers.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.e_culturetoolbakers.ModificaOperaRicerca;
import com.example.e_culturetoolbakers.R;
import com.example.e_culturetoolbakers.SchermataOpera;
import com.example.e_culturetoolbakers.SelezionaOpere;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListaOpereAdapter extends ArrayAdapter<String> {
    private SelezionaOpere selezionaOpere;

    public ListaOpereAdapter(SelezionaOpere context, int textViewResourceId, ArrayList<String> lista) {
        super(context, textViewResourceId, lista);
        selezionaOpere = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.lista_opere, null);

        DatabaseHelper databaseHelper =  selezionaOpere.getDb_helper();

        TextView operaView = (TextView)convertView.findViewById(R.id.nomeOpera);
        String opera = getItem(position);
        operaView.setText(opera);

        ImageView info = (ImageView) convertView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(selezionaOpere);
                builder.setTitle(opera);

                ArrayList<String> info = databaseHelper.getInfoOpera(opera);
                String fotoString = info.get(1);

                LayoutInflater layoutInflater = (LayoutInflater)selezionaOpere.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogLayout = layoutInflater.inflate(R.layout.dettaglio_opera,null);
                builder.setView(dialogLayout);

                ImageView foto = dialogLayout.findViewById(R.id.fotoOpera1);

                if(fotoString!= null){
                    String url = "gs://e-culture-tool-c381c.appspot.com/images/";

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference ref1 = storage.getReferenceFromUrl(url);

                    ref1.child(fotoString).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(selezionaOpere).load(uri).into(foto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            foto.setImageResource(R.drawable.placeholder);
                        }
                    });
                } else{
                    foto.setImageResource(R.drawable.placeholder);
                }

                TextView descrizione = dialogLayout.findViewById(R.id.descrizione);
                descrizione.setText(info.get(0));



                builder.setPositiveButton(R.string.chiudi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageView cancella = (ImageView)convertView.findViewById(R.id.iconaX);

        cancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(opera);
            }
        });



        return convertView;
    }

}
