package com.example.e_culturetoolbakers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.e_culturetoolbakers.R;
import com.example.e_culturetoolbakers.model.Museo;


import java.util.ArrayList;
import java.util.List;

public class ListMuseiAdapter extends ArrayAdapter<Museo> {
    private ArrayList<Museo> items;
    private ArrayList<Museo> AllMusei;
    private ArrayList<Museo> suggestions;

    public ListMuseiAdapter(Context mContext, ArrayList<Museo> museiList) {
        super(mContext, 0, museiList);
        this.items = museiList;
        this.AllMusei = (ArrayList<Museo>) museiList.clone();
        this.suggestions = new ArrayList<Museo>();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /*
        Museo museo = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_musei, viewGroup, false);
        }
        TextView tvMuseo = (TextView) view.findViewById(R.id.nomeMuseo);
        tvMuseo.setText(museo.getNome());
        return view;*/
        View v = view;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_musei, null);
        }
        Museo museo = items.get(i);
        if (museo != null) {
            TextView productLabel = (TextView)  v.findViewById(R.id.nomeMuseo);
            if (productLabel != null) {
                productLabel.setText(museo.getNome());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint== null || constraint.toString().length()==0||constraint=="") {
                suggestions.addAll(AllMusei);
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                suggestions.clear();
                for (Museo museo : AllMusei) {
                    if (museo.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(museo);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
        }
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<Museo> filteredList = (ArrayList<Museo>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Museo c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
        @Override
        public  CharSequence convertResultToString(Object resultValue) {
            return ((Museo) resultValue).getNome();
        }

    };
}
