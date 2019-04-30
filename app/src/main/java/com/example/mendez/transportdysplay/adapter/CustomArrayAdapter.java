package com.example.mendez.transportdysplay.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.fragment.message;
import com.example.mendez.transportdysplay.model.header;
import com.example.mendez.transportdysplay.model.obj_sms;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Object> implements View.OnClickListener {
    private LayoutInflater layoutInflater;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String x;

    public CustomArrayAdapter(Context context, List<Object> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
        settings = context.getSharedPreferences(Constantes.PREFS_NAME, 0);
        x=settings.getString("chevisi","error");
    }




    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        //header
        if (getItem(position) instanceof header)
        {
            //check if this view exists or contains a content
            if (convertView == null || convertView.findViewById(R.id.tvheader) == null)
            {
                convertView = layoutInflater.inflate(R.layout.list_header, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tvheader);
            header header = (header) getItem(position);

            textView.setText(header.getTitle());
        }
        else //ViewHolder Pattern for content
        {
            Holder holder;
            obj_sms content = (obj_sms) getItem(position);
            //
            //Compruebe si esta vista existe o contiene un encabezado

                holder = new Holder();
                if (content.getTipo().equals("1")) {
                    convertView = layoutInflater.inflate(R.layout.cuerpo_sms_rec2, parent, false);
                } else {
                    if (content.getTipo().equals("2")) {
                        convertView = layoutInflater.inflate(R.layout.cuerpo_sms_send2, parent, false);

                    }
                }
                //holder.sms((TextView) convertView.findViewById(R.id.men));
                holder.sms = (TextView) convertView.findViewById(R.id.men);
                holder.hor = (TextView) convertView.findViewById(R.id.hora);
            holder.box=(CheckBox) convertView.findViewById(R.id.checkbox);
            holder.box.setChecked(content.isChecked());
            holder.box.setTag(position);
            holder.box.setOnClickListener(this);
            if (!x.equals("error")&!x.equals("0")){
                holder.box.setVisibility(View.VISIBLE);
                View row = (View) holder.box.getParent();
                if (holder.box.isChecked()){
                    row.setBackground(ContextCompat.getDrawable(getContext(),R.color.checked));
                }else{
                    row.setBackground(null);
                }
            }else{
                holder.box.setVisibility(View.GONE);
            }
            String[]ho=content.getMhora().split(":");
            holder.sms.setText(content.getMmessage());
            holder.hor.setText(String.valueOf(ho[0]+":"+ho[1]));
        }
        return convertView;

    }

    @Override
    public void onClick(View v)  {

        CheckBox checkBox=(CheckBox)v;
        int position = (Integer) v.getTag();
        Log.e("id", String.valueOf(position));
        ((obj_sms)getItem(position)).setChecked(checkBox.isChecked());
        View row = (View) checkBox.getParent();
        if (checkBox.isChecked()) {
            boolean c=false;
            row.setBackground(ContextCompat.getDrawable(getContext(),R.color.checked));
            for (int i = 0; i < getCount(); i++) {
                Object item = getItem(i);
                if (item instanceof obj_sms){
                    if (((obj_sms)item).isChecked()){
                        message.btnbor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_borr));
                        message.btnbor.setEnabled(true);
                    }else{
                        c=true;
                    }

                }
            }
            if (!c){
                message.cheto.setChecked(true);
            }
        } else {
            boolean c=false;
            row.setBackground(null);
            for (int i = 0; i < getCount(); i++) {
                //Log.e("idd", String.valueOf(i));
                Object item = getItem(i);
                if (item instanceof obj_sms){
                    if (((obj_sms)item).isChecked()){
                        c=true;
                    }
                }
            }
            if (!c){
                message.btnbor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_borinac));
                message.btnbor.setEnabled(false);
                message.cheto.setChecked(false);
            }
        }


    }

    class Holder
    {
        TextView sms;
        TextView hor;
        CheckBox box;
    }
}
