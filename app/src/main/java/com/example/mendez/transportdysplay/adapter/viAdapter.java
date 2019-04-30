package com.example.mendez.transportdysplay.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.dialog.hismap;
import com.example.mendez.transportdysplay.model.obVia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class viAdapter extends  RecyclerView.Adapter<viAdapter.viViewHolder>  {
    public List<obVia> items;
    private Context context;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public static  int lastposition=-1;
    public static AlertDialog carga;
    public static class viViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView est;
        public TextView fecha;
        public TextView orig;
        public TextView dest;
        public TextView usu;
        public TextView ivm;
        public CardView cdvi;
        public View view,viewc;

        public viViewHolder(View v) {
            super(v);

            est=(TextView)v.findViewById(R.id.es);
            fecha = (TextView) v.findViewById(R.id.fe);
            orig = (TextView) v.findViewById(R.id.ori);
            dest = (TextView) v.findViewById(R.id.des);
            usu = (TextView) v.findViewById(R.id.us);
            ivm = (TextView) v.findViewById(R.id.iv_map);
            cdvi=(CardView) v.findViewById(R.id.cdvi);
            view=(View)v.findViewById(R.id.view);
            viewc=(View)v.findViewById(R.id.viewc);
        }

    }

    public viAdapter(List<obVia> items, Context context) {
        this.items = items;
        this.context=context;
        settings = context.getSharedPreferences(Constantes.PREFS_NAME, 0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public viViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.via_card4, viewGroup, false);
        final viViewHolder vh = new viViewHolder(v);

        return vh;
    }
    private void setAnimation(View viewAnimation,int position){

        if (position>lastposition){
            Animation animation= AnimationUtils.loadAnimation(context,R.anim.enter);
            viewAnimation.startAnimation(animation);
            lastposition=position;
        }
    }

    @Override
    public void onBindViewHolder(viViewHolder viewHolder, final int i) {
        setAnimation(viewHolder.cdvi,i);
        if (items.get(i).getEst().equals("Rechazado")){
            viewHolder.est.setTextColor(ContextCompat.getColor(context,R.color.colorOrange));
            viewHolder.view.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_circle));
            viewHolder.viewc.setBackground(ContextCompat.getDrawable(context,R.color.colorOrange));
        }else{
            if (items.get(i).getEst().equals("Completado")){
                viewHolder.est.setTextColor(ContextCompat.getColor(context,R.color.sucess_color));
                viewHolder.view.setBackground(ContextCompat.getDrawable(context,R.drawable.green_circle));
                viewHolder.viewc.setBackground(ContextCompat.getDrawable(context,R.color.sucess_color));
            }else {
                viewHolder.est.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                viewHolder.view.setBackground(ContextCompat.getDrawable(context,R.drawable.gree_circle));
                viewHolder.viewc.setBackground(ContextCompat.getDrawable(context,R.color.colorPrimary));
            }
        }
        viewHolder.est.setText(items.get(i).getEst());
        viewHolder.fecha.setText(items.get(i).getFecha());
        viewHolder.orig.setText(items.get(i).getOriLit());
        viewHolder.dest.setText(items.get(i).getDesLit());
        viewHolder.usu.setText("Conductor: "+items.get(i).getNomUa());
        viewHolder.ivm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settings.getString("hmap","error").equals("0")) {
                    carga = ProgressDialog.show(context,"Cargando...","Espere por favor...",false,false);
                    String[] latlongo = String.valueOf(items.get(i).getOrig()).split(",");
                    String[] latlongd = String.valueOf(items.get(i).getDest()).split(",");
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("lat", latlongo[0]);
                    hashMap.put("lng", latlongo[1]);
                    HashMap<String,Object> hashMapdialo = new HashMap<>();
                    hashMapdialo.put("lat", latlongd[0]);
                    hashMapdialo.put("lng", latlongd[1]);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("mapo", hashMap);
                    bundle.putSerializable("mapd", hashMapdialo);
                    bundle.putString("origenlit",items.get(i).getOriLit());
                    bundle.putString("destinolit",items.get(i).getDesLit());
                    bundle.putString("fecha",items.get(i).getFecha());
                    bundle.putString("hora",items.get(i).getHora());
                    bundle.putString("cliente",items.get(i).getNomUa());
                    FragmentActivity activity = (FragmentActivity)(context);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    final hismap mapdi = new hismap();
                    mapdi.setArguments(bundle);
                    mapdi.show(fm, "Dialogo");
                    editor=settings.edit();
                    editor.putString("hmap","0");
                    editor.apply();
                }else{
                    Toast.makeText(context, "Ya tiene una ventana abierta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void filterList(ArrayList<obVia> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }
}
