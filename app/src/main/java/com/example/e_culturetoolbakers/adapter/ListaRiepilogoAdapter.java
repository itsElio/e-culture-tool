package com.example.e_culturetoolbakers.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.e_culturetoolbakers.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListaRiepilogoAdapter extends ArrayAdapter<String> {
    private List<String> zoneLista;
    private Context context;
    private String jsonString;
    public ListaRiepilogoAdapter (Context context, List<String> arrayZone, String jsonString) {
        super(context,0,arrayZone);
        this.zoneLista = arrayZone;
        this.context=context;
        this.jsonString = jsonString;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;//annullo l'effetto del click
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        if (ePari(i)) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lista_riepilogo, null);
        } else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lista_riepilogo2, null);
        }
        if (i == zoneLista.size()-1) {
            ImageView linea = (ImageView)  v.findViewById(R.id.museoLinea);
            linea.setVisibility(View.INVISIBLE);
        }
        String zona = zoneLista.get(i);

            TextView nome_zona = (TextView)  v.findViewById(R.id.NomeZona);
            if (nome_zona != null) {
                String zona_senza_numero = zona;
                if (zona.charAt(zona.length()-3) == '-'){
                    zona_senza_numero = zona.substring(0, zona.length()-3);
                }
                nome_zona.setText(zona_senza_numero);
            }


        ImageView punto =  v.findViewById(R.id.museoPunto);
        punto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_mostra_opere, null, false);



                // Initializza
                ArrayList<String> nomeOpere = new ArrayList<>();
                
                try {
                    nomeOpere = trovaOpereDaJson(zona);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder myDialog = new AlertDialog.Builder(context).setView(dialogView);
                TextView titoloDialog = dialogView.findViewById(R.id.TitoloDialog);
                titoloDialog.setText("Opere della zona:");
                ListView lista = dialogView.findViewById(R.id.ListaDialog);
                lista.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,nomeOpere));
                Button button =  dialogView.findViewById(R.id.bottoneOK);


                myDialog.setView(dialogView);
                final AlertDialog dialog = myDialog.create();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        return v;
    }
    private static boolean ePari(int numero) {
        if ((numero % 2) == 0) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> trovaOpereDaJson(String ZonaDesiderata) throws JSONException {
        JSONObject jsonObject = null;
        ArrayList<String> arrayOpere = new ArrayList<>();

        try {
            jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("edges");

            for(int i = 0; i< jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String nomeZona= obj.getString("source");
                nomeZona = nomeZona.substring(2);
                if (nomeZona.equals(ZonaDesiderata)){
                    String nomeOpera= obj.getString("target");
                    //Potrebbe esssere un legame tra 2 zone ...quindi verifico che si tratti di un opera
                    String tipo  = nomeOpera.substring(0,2);
                    nomeOpera = nomeOpera.substring(2);
                    if(tipo.startsWith("z")) {
                        //non devo prendere il nome
                    } else {
                        arrayOpere.add(nomeOpera); //è un opera e la prendo

                    }

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        return arrayOpere;
    }



}
