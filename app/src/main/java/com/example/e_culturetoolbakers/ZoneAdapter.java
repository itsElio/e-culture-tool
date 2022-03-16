package com.example.e_culturetoolbakers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;


public class ZoneAdapter extends ArrayAdapter<String> {

    private InserisciZona inserisciZona;

    Context context;
    ArrayList<String> zoneList;
    Museo museo;
    List<Zona> zone;
    List<Opera> opere;
    Opera operaDaRimuovere;
    LayoutInflater inflater;
    Zona zonaDaRimuovere;
    private Zona zonaOpere;

    // RiepilogoZone
    public ZoneAdapter(RiepilogoZone context, ArrayList<String> zoneList, Museo museo, List<Zona> zone) {
        super(context, R.layout.lista_zone1, zoneList);
        this.context = context;
        this.zoneList = zoneList;
        this.museo = museo;
        this.zone = zone;
        inflater = LayoutInflater.from(context);
    }

    // Riepilogo Opere
    public ZoneAdapter(RiepilogoOpere context, ArrayList<String> zoneList, Museo museo, Zona zonaPerAdapter, List<Opera> opere) {
        super(context, R.layout.lista_zone1, zoneList);
        this.context = context;
        // Sono opere
        this.zoneList = zoneList;
        this.museo = museo;
        this.zonaOpere = zonaPerAdapter;
        this.opere = opere;
        inflater = LayoutInflater.from(context);
    }
    public ZoneAdapter(RiepilogoOpere context, int lista_zone, ArrayList<String> zoneList) {
        super(context, R.layout.lista_zone1, zoneList);
        this.context = context;
        this.zoneList = zoneList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return zoneList.size();
    }

    @Override
    public String getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.lista_zone1, null);
        TextView zonaView = (TextView) view.findViewById(R.id.nomeZona);
        ImageView icon = (ImageView) view.findViewById(R.id.iconaX);
        zonaView.setText(zoneList.get(i));

        String zona = zoneList.get(i);

        ImageView cancella = (ImageView) view.findViewById(R.id.iconaX);

        cancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Rimuove "+ zona);
                if(zone != null) {
                    zonaDaRimuovere = zone.get(i);
                    // Rimuove dalla lista
                    remove(zona);
                    // Rimuove dall'oggetto di tipo museo
                    museo.getZone().remove(zonaDaRimuovere);
                } else {
                    // Riceve l'oggetto opera con il nome selezionato
                    operaDaRimuovere = zonaOpere.findOpera(zona);
                    // index della zona in cui ci troviamo
                    int index = museo.getZone().indexOf(zonaOpere);
                    // Rimuove dalla lista
                    remove(zona);
                    // Rimuove dall'oggetto di tipo museo
                    museo.getZone().get(index).opere.remove(operaDaRimuovere);
                }
            }
        });
        return view;
    }
}
