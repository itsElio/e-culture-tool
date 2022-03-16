package com.example.e_culturetoolbakers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;

import java.util.ArrayList;
import java.util.HashMap;

public class RiepilogoRegistrazioneAdapter extends BaseExpandableListAdapter {

    ArrayList<String> zone;
    HashMap<String, ArrayList<String>> listZoneOpere;
    private Context context;
    Museo museo;

    public RiepilogoRegistrazioneAdapter(Context context, ArrayList<String> zone, HashMap<String, ArrayList<String>> listZoneOpere, Museo museo) {
        this.zone =  zone;
        this.listZoneOpere = listZoneOpere;
        this.context = context;
        this.museo = museo;
    }

    @Override
    public int getGroupCount() {
        return zone.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listZoneOpere.get(zone.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return zone.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listZoneOpere.get(zone.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_expandable_list_item_1, parent, false);

        TextView textView = convertView.findViewById(android.R.id.text1);
        String sGroup = String.valueOf(getGroup(groupPosition));

        textView.setText(sGroup);
        textView.setTypeface(null, Typeface.BOLD);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_selectable_list_item,parent,false);

        TextView textView = convertView.findViewById(android.R.id.text1);
        String sChild = String.valueOf(getChild(groupPosition, childPosition));

        textView.setText(sChild);
        // Da modificare questo Listener (per far modificare le opere?)
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent.getContext(), sChild, Toast.LENGTH_SHORT).show();
                Intent modificaOpera = new Intent(context, ModificaOpera.class);
                //modificaOpera.putExtra();
                String sGroup = String.valueOf(getGroup(groupPosition));

                Bundle museoBundle =  new Bundle();
                museoBundle.putSerializable("museoObject", museo);

                museoBundle.putSerializable("opera", museo.findZona(sGroup).findOpera(sChild));
                museoBundle.putSerializable("zona", museo.findZona(sGroup));

                modificaOpera.putExtras(museoBundle);

                context.startActivity(modificaOpera);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
