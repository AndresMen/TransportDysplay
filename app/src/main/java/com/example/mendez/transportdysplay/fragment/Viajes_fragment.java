package com.example.mendez.transportdysplay.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.adapter.viAdapter;
import com.example.mendez.transportdysplay.model.obVia;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Viajes_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Viajes_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView recycler;

    RecyclerView.LayoutManager lManager;
    private SwipeRefreshLayout swipeContainer;
    public List<obVia> items;
    private viAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private FirebaseUser user;
    private AlertDialog loading;
    private FloatingActionButton filtro;
    private TextView tvavvi;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public Viajes_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        viAdapter.lastposition=-1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_viajes, container, false);
        user=FirebaseAuth.getInstance().getCurrentUser();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);
        recycler = (RecyclerView) view.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        items = new ArrayList<>();
        filtro=(FloatingActionButton) view.findViewById(R.id.editTextFil);
        filtro.setOnClickListener(this);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(lManager);
        swipeContainer.setOnRefreshListener(this);
        tvavvi=(TextView)view.findViewById(R.id.tvavvi);
        cargarAdaptador();
        settings=getContext().getSharedPreferences(Constantes.PREFS_NAME,0);
        editor=settings.edit();
        editor.putString("hmap","error");
        editor.apply();
        return view;
    }

    private void filter(String text) {
        ArrayList<obVia> filteredList = new ArrayList<>();

        for (obVia item : items) {
            if (item.getEst().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            adapter.filterList(filteredList);
        }
    }

   /* public void fil(){
        final CharSequence[] opcion={"Todos","Completado","Rechazado"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getContext());
        alertOpcion.setTitle("Escoja Viajes");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opcion[i].equals("Todos")){
                    //filtro.setText(opcion[i].toString());
                    filter("");
                }else {
                    filtro.setText(opcion[i].toString());
                    filter(filtro.getText().toString());
                }
            }
        });
        alertOpcion.show();
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void cargarAdaptador() {
        Log.e("veer","entro guardar");
        Log.e("token_cli", user.getUid());
        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("token_cli", user.getUid());
        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        loading = ProgressDialog.show(getContext(),"Subiendo...","Espere por favor...",false,false);
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.VI_GET,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuesta(response);

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
                        Map<String, String> headers = new HashMap<>();
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
    private void procesarRespuesta(JSONObject response) {
        Log.e("puto","ENTRA PROESA   ");
        try {
            // Obtener estado
            String estado = response.getString("estado");
            Log.e("puto","esatso  -"+estado);
            switch (estado) {
                case "1": // EXITO

                    JSONArray mensajes = response.getJSONArray("msg");
                    Log.e("ver","entra-caso1 -"+mensajes);

                    Log.e("ver","TAMA"+mensajes.length());

                    loading.dismiss();

                   for (int i=0;i<mensajes.length();i++){
                        items.add(new obVia(mensajes.getJSONObject(i).getString("fecha"), mensajes.getJSONObject(i).getString("hora"), mensajes.getJSONObject(i).getString("origen"), mensajes.getJSONObject(i).getString("destino"),mensajes.getJSONObject(i).getString("origen_lit"),mensajes.getJSONObject(i).getString("destino_lit"), mensajes.getJSONObject(i).getString("estado"), mensajes.getJSONObject(i).getString("usuario")));
                    }

                    // Crear un nuevo adaptador
                    adapter = new viAdapter(items,getContext());

                    recycler.setAdapter(adapter);
                    recycler.setVisibility(View.VISIBLE);
                    filtro.setVisibility(View.VISIBLE);
                    tvavvi.setVisibility(View.GONE);
                    break;
                case "2": // FALLIDO

                    String mensaje2 = response.getString("mensaje");
                    Log.e("ver","entra-caso2 -"+mensaje2);
                    loading.dismiss();
                    recycler.setVisibility(View.GONE);
                    filtro.setVisibility(View.GONE);
                    tvavvi.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update data in ListView
                items.clear();
                recycler.setAdapter(null);
                cargarAdaptador();
                // Remove widget from screen.
                swipeContainer.setRefreshing(false);
               // filtro.setText("");
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editTextFil:
                //fil();
                showFiltro();
                break;
        }
    }
    public void showFiltro() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_categoria, null);
        dialogBuilder.setView(dialogView);

        final RadioButton todos_ = (RadioButton) dialogView.findViewById(R.id.id_todos);
        final RadioButton completados_ = (RadioButton) dialogView.findViewById(R.id.id_completados);
        final RadioButton cancelados_= (RadioButton) dialogView.findViewById(R.id.id_cancelados);

        dialogBuilder.setPositiveButton("Buscar por", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(todos_.isChecked()){
                    filter("");
                }
                if (completados_.isChecked()){
                    filter("Completado");
                }
                if(cancelados_.isChecked()){
                    filter("Rechazado");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
