package com.example.mendez.transportdysplay.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mendez.transportdysplay.database.BD;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.fragment.map;
import com.example.mendez.transportdysplay.model.obj_bus;

import java.util.ArrayList;
import java.util.List;

public class adapterbus extends ArrayAdapter<obj_bus> {
    private List<obj_bus> countryListFull;

    public adapterbus(@NonNull Context context, @NonNull List<obj_bus> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bus, parent, false
            );
        }

        TextView textViewName = (TextView) convertView.findViewById(R.id.textView);
        ImageView imageView =(ImageView)convertView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BD base=new BD(getContext(),"baseSms",null,1);
                final SQLiteDatabase bd=base.getWritableDatabase();
                String re=countryListFull.get(position).getCountryName();
                countryListFull.remove(position);
                bd.delete("bus","calle=?",new String[]{re});
                Toast.makeText(getContext(), "Se elimino "+re, Toast.LENGTH_SHORT).show();
                bd.close();
                map.cer();
            }
        });
        obj_bus countryItem = getItem(position);

        if (countryItem != null) {
            textViewName.setText(countryItem.getCountryName());
            imageView.setBackgroundResource(R.drawable.ic_cl);
        }

        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<obj_bus> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (obj_bus item : countryListFull) {
                    if (item.getCountryName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((obj_bus) resultValue).getCountryName();
        }
    };
}
