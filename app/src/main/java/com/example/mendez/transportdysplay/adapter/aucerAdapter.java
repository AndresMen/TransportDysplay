package com.example.mendez.transportdysplay.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.activity.Inicio;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.fragment.map;
import com.example.mendez.transportdysplay.model.obau;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class aucerAdapter extends  RecyclerView.Adapter<aucerAdapter.aucerViewHolder> {
    public List<obau> items;
    private Context context;
    private CardView cd;
    private ProgressDialog loading;
    private String iden =null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static class aucerViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item

        public TextView nom;
        public TextView pla;
        public aucerViewHolder(View v) {
            super(v);

            nom = (TextView) v.findViewById(R.id.textViewNom);
            pla = (TextView) v.findViewById(R.id.textViewPla);

        }

    }

    public aucerAdapter(List<obau> items, Context context) {
        this.items = items;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public aucerViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.au_card2, viewGroup, false);
        cd=(CardView)v.findViewById(R.id.cd);
        final aucerViewHolder vh = new aucerViewHolder(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = vh.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (map.dest()!=null) {
                                showDialogNus(position);
                            }else {
                                Toast.makeText(context,"Coloque su destino",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.translate_enter_up);
        v.startAnimation(animation);
        return vh;
    }
    private void showDialogNus(final int position) {
        iden=items.get(position).getId();
        final Double lato=map.orig().latitude;
        final Double lono=map.orig().longitude;
        final Double latd= Objects.requireNonNull(map.dest()).latitude;
        final Double lond= Objects.requireNonNull(map.dest()).longitude;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addor ;
        List<Address> addes ;
        String aor = null;
        String ades =null;
        final ProgressDialog pros = ProgressDialog.show(context,"Cargando...","Espere por favor...",false,false);
        int i=0;
        do {
            try {
                addor= geocoder.getFromLocation(lato,lono,1);
                addes= geocoder.getFromLocation(latd,lond,1);
                aor=addor.get(0).getAddressLine(0);
                ades=addes.get(0).getAddressLine(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("whipas","paso "+i);
            i++;

        }while (ades==null&aor==null&i<=5);

           if (i>=5){
               pros.dismiss();
               Toast.makeText(context,"Fallo en la red",Toast.LENGTH_SHORT).show();
           }else{
               pros.dismiss();
               final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

               dialogBuilder.setTitle("Solicitud");
               dialogBuilder.setMessage("Origen: "+aor+"\n Destino: "+ades);
               final String finalAor = aor;
               final String finalAdes = ades;
               dialogBuilder.setPositiveButton("Mandar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       //origen lugar del cliente
                       loading = ProgressDialog.show(context,"Subiendo...","Espere por favor...",false,false);
                              preferences=context.getSharedPreferences(Constantes.PREFS_NAME, 0);
                              editor=preferences.edit();
                               editor.putString("lato", String.valueOf(lato));
                               editor.putString("lono", String.valueOf(lono));
                               editor.putString("latd", String.valueOf(latd));
                               editor.putString("lond", String.valueOf(lond));
                               editor.putString("tus",items.get(position).getId());
                               editor.putString("nombus", items.get(position).getNomb());
                               editor.putString("pla", items.get(position).getPla());
                               editor.putString("tkdes", items.get(position).getFcm());
                               editor.putString("verecy","0");
                               editor.apply();

                               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                               guardarvia(String.valueOf(lato+","+lono),String.valueOf(latd+","+lond), finalAor, finalAdes,"esperando",user.getUid(),items.get(position).getId(),items.get(position).getFcm(), FirebaseInstanceId.getInstance().getToken());
                   }
               });
               dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       dialog.dismiss();
                   }
               });
               AlertDialog b = dialogBuilder.create();
               b.getWindow().setWindowAnimations(R.style.dialog_animation_scale);
               b.show();
           }

    }

    @Override
    public void onBindViewHolder(aucerViewHolder viewHolder, int i) {

        viewHolder.nom.setText(items.get(i).getNomb());
        viewHolder.pla.setText(items.get(i).getPla());

        if (items.get(i).getEstado().equals("Ocupado")){
            cd.setVisibility(View.GONE);
        }else{
            if (items.get(i).getEstado().equals("Desocupado") | items.get(i).getEstado().equals("Espera")|items.get(i).getEstado().equals("Desconectado")){
                cd.setVisibility(View.VISIBLE);
            }
        }
    }

    private void guardarvia(final String ori, final String des, String orilit, String deslit, String est, String tc, String tu, String tkd, String ctkf) {
        Log.e("veer","entro guardar");
        Date d = new Date();
        SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = fec.format(d);

        Date h = new Date();
        SimpleDateFormat ho = new SimpleDateFormat("H:mm");
        String hora = ho.format(h);
        Log.e("fecha",fecha);
        Log.e("hora", hora);
        Log.e("origen",ori);
        Log.e("destino", des);
        Log.e("origen_lit",orilit);
        Log.e("destino_lit", deslit);
        Log.e("estado", est);
        Log.e("token_cli", tc);
        Log.e("token_usu", tu);
        Log.e("tkf_auto",tkd);
        Log.e("tkf_cliente",ctkf);

        HashMap<String, String> mapa = new HashMap<>();// Mapeo previo

        //tabla usuario
        mapa.put("fecha",fecha);
        mapa.put("hora", hora);
        mapa.put("origen",ori);
        mapa.put("destino", des);
        mapa.put("origen_lit",orilit);
        mapa.put("destino_lit", deslit);
        mapa.put("estado", est);
        mapa.put("token_cli", tc);
        mapa.put("token_usu", tu);
        mapa.put("tokenfcm", tkd);
        mapa.put("tokenfcm_cli",ctkf);

        JSONObject jobject = new JSONObject(mapa);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.IN_VI,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                String[]lo=ori.split(",");
                                String []lt=des.split(",");
                                procesarRespuesta(response,lo[0],lo[1],lt[0],lt[1]);

                                Log.e("puto","respuetsa -"+response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));
                                loading.dismiss();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void procesarRespuesta(JSONObject response,String lto,String lno,String ltd,String lnd) {
        DatabaseReference esta= FirebaseDatabase.getInstance().getReference("autos/"+iden+"/estado");
        HashMap<String,String> mapa = new HashMap<String, String>();// Mapeo previo
        Log.e("puto","ENTRA PROESA   ");
        try {
            // Obtener estado
            String estado = response.getString("estado");
            Log.e("puto","esatso  -"+estado);
            // Obtener mensaje
            final String mensaje = response.getString("msg");
            Log.e("puto","mensaje -"+mensaje);

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            "Se pidio correctamente",
                            Toast.LENGTH_LONG).show();
                    loading.dismiss();

                    mapa.put("estado","Espera");
                    esta.setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                ((Activity)context).finish();
                                ((Activity)context).overridePendingTransition(0, 0);
                                Intent refr=new Intent(context,Inicio.class);
                                refr.putExtra("rf","1");
                                context.startActivity(refr);
                                ((Activity)context).overridePendingTransition(0, 0);

                            }
                        }
                    });
                    DatabaseReference datos= FirebaseDatabase.getInstance().getReference("autos/"+iden+"/viaje");
                    HashMap<String,String>dat=new HashMap<>();
                    dat.put("lato",lto);
                    dat.put("lono",lno);
                    dat.put("latd",ltd);
                    dat.put("lond",lnd);
                    dat.put("idvi",mensaje);
                    dat.put("tkfcm",FirebaseInstanceId.getInstance().getToken());
                    dat.put("tokcli",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dat.put("nomcli",preferences.getString("nom","error"));
                    dat.put("numcli",preferences.getString("num","error"));
                    datos.setValue(dat);
                    editor=preferences.edit();
                    editor.putString("ped","0");
                    editor.putString("idvia",mensaje);
                    editor.apply();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    loading.dismiss();
                    Constantes.termi(context);
                    mapa.put("estado","Desocupado");
                    esta.setValue(mapa);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
