package com.example.mendez.transportdysplay.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.database.BD;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.adapter.CustomArrayAdapter;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.model.header;
import com.example.mendez.transportdysplay.model.obj_sms;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link message.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class message extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private OnFragmentInteractionListener mListener;
    LinearLayout llme;
    FloatingActionButton faben;
    EditText etem;
    String tkcf;

    String tus,nom,pla;
    ListView listView;
    FirebaseAuth auth;
    FirebaseUser user;

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    TextView tvav;
    LinearLayout llch;
    public static CheckBox cheto;
   public static Button btnbor;
    public message() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        setHasOptionsMenu(true);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        llme=(LinearLayout)view.findViewById(R.id.llme);

        etem=(EditText)view.findViewById(R.id.mensa);
        etem.setFilters(new InputFilter[] { inputfilter });
        faben=(FloatingActionButton)view.findViewById(R.id.fabmensa);
        faben.setOnClickListener(this);
        llch=(LinearLayout)view.findViewById(R.id.llch);
        cheto=(CheckBox)view.findViewById(R.id.cheto);
        btnbor=(Button)view.findViewById(R.id.btnbrs);
        btnbor.setOnClickListener(this);
        settings = getActivity().getSharedPreferences(Constantes.PREFS_NAME, 0);
        tkcf=settings.getString("tkdes","error");
        tus=settings.getString("tus","error");
        nom=settings.getString("nombus","error");
        pla=settings.getString("pla","error");
        Log.e("datos",tus+" --- "+nom+" ---- "+pla);
        if (tus.equals("error")|nom.equals("error")|pla.equals("error")) {
            llme.setVisibility(View.GONE);
        }else{
            llme.setVisibility(View.VISIBLE);
        }
        listView=(ListView)view.findViewById(R.id.lismessa);
        listView.setOnItemLongClickListener(this);
        tvav=(TextView)view.findViewById(R.id.tvav);
        editor=settings.edit();
        editor.putString("chevisi","0");
        editor.apply();
        Log.e("oncre","passs");
        cheto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listView.getAdapter()!=null){
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        Object item = listView.getAdapter().getItem(i);
                        if (item instanceof obj_sms){
                            ((obj_sms)item).setChecked(isChecked);
                            if (isChecked){
                                btnbor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_borr));
                                btnbor.setEnabled(true);
                            }else{
                                btnbor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_borinac));
                                btnbor.setEnabled(false);
                            }
                        }
                    }
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }else{
                    buttonView.setChecked(false);
                }
            }
        });
      //  list();
       // revmesen();
        return view;
    }

    public void revmesen(){
        final DatabaseReference sm = FirebaseDatabase.getInstance().getReference("autos/" + settings.getString("tus", "error") + "/viaje/smsau");
        sm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataChildren : dataSnapshot.getChildren()) {
                        setMsg(dataChildren);
                    }
                    sm.removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Mensajeerror",databaseError.toString());
            }
        });
    }

    public void setMsg(DataSnapshot dataSnapshot){
        Date d = new Date();
        SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = fec.format(d);



        List<Object> list = new ArrayList<Object>();
        obj_sms content ;
        header header ;
        String ca = "";
        String mes=String.valueOf(dataSnapshot.child("sms").getValue());
        String fecc= String.valueOf(dataSnapshot.child("fecha").getValue());
        String horr=String.valueOf(dataSnapshot.child("hora").getValue());
        String nombr=String.valueOf(dataSnapshot.child("nombre").getValue());
        String pla=String.valueOf(dataSnapshot.child("placa").getValue());
        if (!ca.equals(fecc)){
            header = new header();
            if(fecha.equals(fecc))
            {
                header.setTitle("HOY");
            }else{
                header.setTitle(fecc);
            }

            list.add(header);
            ca=fecc;
        }
        content = new obj_sms(mes,fecc,horr,nombr,pla,"1");
        list.add(content);

        listView.setAdapter(new CustomArrayAdapter(getContext(),list));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_mess, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.sel_men:
                /*BD base=new BD(getContext(),"baseSms",null,1);
                final SQLiteDatabase bd=base.getWritableDatabase();
                bd.delete("sms",null , null);
                bd.close();
                list2();*/
               // Log.e("cantidad","es "+listView.getCount());
                if(listView.getCount()!=0){

                    String x=settings.getString("chevisi","error");
                    if (x.equals("1")){
                        editor=settings.edit();
                        editor.putString("chevisi","0");
                        editor.apply();
                        list2();
                        Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.exit_up);
                        llch.setAnimation(animation);
                        llch.setVisibility(View.GONE);
                        cheto.setChecked(false);
                    }else{
                        editor=settings.edit();
                        editor.putString("chevisi","1");
                        editor.apply();
                        list2();
                        Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.enter_up);
                        llch.setAnimation(animation);
                        llch.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(getContext(), "No tiene mensajes", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //list();
        list2();
        Log.e("ssss","pasooonr");
    }

    public void list2(){
        Date d = new Date();
        SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = fec.format(d);

        Calendar c=Calendar.getInstance();
        c.add(Calendar.DATE,-1);
        Date date=c.getTime();
        String ay=fec.format(date);
        //Log.e("datedia", ay);

        List<Object> list = new ArrayList<Object>();
        obj_sms content ;
        header header ;
        BD base=new BD(getContext(),"baseSms",null,1);
        final SQLiteDatabase bd=base.getWritableDatabase();
        String ca = "";
        Cursor fila = bd.rawQuery(
                "select * from sms", null);
        if(fila.getCount()>0){
            listView.setVisibility(View.VISIBLE);
            tvav.setVisibility(View.GONE);
            if (fila.moveToFirst()) {
                do {
                    if (!ca.equals(fila.getString(1))){
                        header = new header();
                        if(fecha.equals(fila.getString(1)))
                        {
                            header.setTitle("Hoy");
                        }else{
                            if (ay.equals(fila.getString(1))){
                                header.setTitle("Ayer");
                            }else{
                                header.setTitle(fila.getString(1));
                            }

                        }

                        list.add(header);
                        ca=fila.getString(1);
                    }
                    content = new obj_sms(fila.getString(0),fila.getString(1),fila.getString(2),fila.getString(3),fila.getString(4),fila.getString(5));
                    list.add(content);
                }while (fila.moveToNext());
            }
            listView.setAdapter(new CustomArrayAdapter(getContext(),list));
        }else{
            Toast.makeText(getContext(), "No tiene mensajes", Toast.LENGTH_SHORT).show();
            listView.setVisibility(View.GONE);
            tvav.setVisibility(View.VISIBLE);
        }
        bd.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabmensa:
                //agarra la fecha
                if (!etem.getText().toString().trim().isEmpty()){
                BD base=new BD(getContext(),"baseSms",null,1);
                final SQLiteDatabase bd=base.getWritableDatabase();
                Date d = new Date();
                SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = fec.format(d);
                //agarra la hora
                Date h = new Date();
                SimpleDateFormat hor = new SimpleDateFormat("HH:mm:ss");
                String ho = hor.format(h);

                ContentValues registro = new ContentValues();
                registro.put("sms",etem.getText().toString());
                registro.put("fecha",fecha);
                registro.put("hora",ho);
                registro.put("nombre",String.valueOf(nom));
                registro.put("placa",String.valueOf(pla));
                registro.put("tipo","2");
                bd.insert("sms", null, registro);
                notif(etem.getText().toString()+"%"+fecha+"%"+ho,"m", user.getUid());

                list2();

                HashMap<String,String>mapasms=new HashMap<>();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("autos").child(settings.getString("tus","error")).child("viaje").child("smscli").push();
                mapasms.put("fecha",fecha);
                mapasms.put("hora",ho);
                mapasms.put("sms",etem.getText().toString());
                mapasms.put("nombrecli",settings.getString("nom","error"));
                reference.setValue(mapasms);
                etem.setText("");
                }else{
                    Toast.makeText(getContext(),"No valido",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnbrs:
               avbor();
                break;

        }
    }

    public void avbor(){
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getContext());
        alertOpcion.setTitle("Aviso");
        alertOpcion.setMessage("Se borrara los mensajes seleccionados");
        alertOpcion.setCancelable(false);
        alertOpcion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BD base=new BD(getContext(),"baseSms",null,1);
                final SQLiteDatabase bd=base.getWritableDatabase();
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    Object item = listView.getAdapter().getItem(i);
                    if (item instanceof obj_sms){
                        //Log.e("ischec","position "+i+" checked "+ ((obj_sms)item).isChecked());
                        if (((obj_sms)item).isChecked()){
                            bd.delete("sms","fecha=? and hora=?", new String[]{String.valueOf(((obj_sms)item).getMfecha()),String.valueOf(((obj_sms)item).getMhora())});
                        }
                    }
                }
                bd.close();
                list2();
                Toast.makeText(getContext(), "Mensaje borrado", Toast.LENGTH_SHORT).show();
            }
        });
        alertOpcion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertOpcion.show();
    }
    private String charactersForbiden = "%"; //*Caracter o caracteres no permitidos.

    private InputFilter inputfilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && charactersForbiden .contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    public void notif(String msg,String av,String tkus) {
        Log.e("veer","entro guardar");
//        Log.e("tokenfcm",tkcf);
        Log.e("msg", msg);
        Log.e("tokenfcm", tkcf);
       Log.e("av",av);
        Log.e("tokenus",tkus);
        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("tokenfcm",tkcf);//token fcm destino
        map.put("msg", msg);//mensaje
        map.put("av",av);//identificador
        map.put("tokenus",tkus);//token del que manda


        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.ENV_NOT,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuestanot(response);

                                Log.e("puto","respuetsa -"+response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));
                                //loading.dismiss();
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
    private void procesarRespuestanot(JSONObject response) {
        Log.e("puto","ENTRA PROESA   ");
        try {
            // Obtener estado
            String estado = response.getString("estado");
            Log.e("puto","esatso  -"+estado);
            // Obtener mensaje
            String mensaje = response.getString("msg");
            Log.e("puto","mensaje -"+mensaje);

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    //loading.dismiss();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    // getApplication().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    //loading.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void infmes(String fe,String ho,String copla){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.infor_mensa2, null);
        dialogBuilder.setView(dialogView);
        final TextView tvfe = (TextView) dialogView.findViewById(R.id.tvfe);
        final TextView tvho = (TextView) dialogView.findViewById(R.id.tvho);
        final TextView tvco = (TextView) dialogView.findViewById(R.id.tvco);
        //dialogBuilder.setTitle("Información");
        final AlertDialog b = dialogBuilder.create();
        tvfe.setText(fe);
        tvho.setText(ho);
        tvco.setText(copla);
        dialogBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        b.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Object item = listView.getAdapter().getItem(position);
        if (item instanceof obj_sms){
            final CharSequence[] opcion={"Informacion","Borrar"};
            final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getContext());
            alertOpcion.setTitle("Seleccione: ");
            alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case 0:
                            infmes(((obj_sms) item).getMfecha(),((obj_sms) item).getMhora(),((obj_sms) item).getMnombreUs()+" - "+((obj_sms) item).getMplacaUs());
                            break;
                        case 1:
                            Toast.makeText(getContext(), "Borrar", Toast.LENGTH_SHORT).show();
                            BD base=new BD(getContext(),"baseSms",null,1);
                            final SQLiteDatabase bd=base.getWritableDatabase();
                            bd.delete("sms","fecha=? and hora=?", new String[]{String.valueOf(((obj_sms)item).getMfecha()),String.valueOf(((obj_sms)item).getMhora())});
                            bd.close();
                            list2();
                            break;
                    }
                }
            });
            alertOpcion.show();
        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
