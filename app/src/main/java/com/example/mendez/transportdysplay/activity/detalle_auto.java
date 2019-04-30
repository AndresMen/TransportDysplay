package com.example.mendez.transportdysplay.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class detalle_auto extends AppCompatActivity {
    //TextInputEditText edt_nom,edt_mod,edt_pla,edt_mar;
    private AlertDialog loading;
    String x;
    ImageView iv_user_photo,iv_auto_photo;
    TextView tv1,tv2,tv3,tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle2);
        setTitle("Informacion del conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        x=getIntent().getStringExtra("usto");
        tv1=(TextView)findViewById(R.id.tvNumber1);
        tv2=(TextView)findViewById(R.id.tvNumber2);
        tv3=(TextView)findViewById(R.id.tvNumber3);
        tv4=(TextView)findViewById(R.id.tvNumber4);

       iv_user_photo=(ImageView)findViewById(R.id.iv_user_photo);
        iv_auto_photo=(ImageView)findViewById(R.id.iv_auto_photo);
        if (x!=null){
            cargarAdaptador(x);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                //overridePendingTransition(R.anim.right_in,R.anim.right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void cargarAdaptador(String us) {
        Log.e("veer","entro guardar");
        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("token_usu", us);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        loading = ProgressDialog.show(detalle_auto.this,"Cargando...","Espere por favor...",false,false);
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.OBT_US,
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

                    JSONObject mensajes = response.getJSONObject("msg");
                    Log.e("ver","entra-caso1 -"+mensajes);
                    //  VentaDiarias[] productos = gson.fromJson(mensaje.toString(), VentaDiarias[].class);
                    // adapter = new VentasdiariasAdapter(Arrays.asList(productos), getActivity());
                    //lista.setAdapter(adapter);

                    Log.e("ver","TAMAÑO"+mensajes.length());

                        tv1.setText(mensajes.getString("nombre"));
                        tv3.setText(mensajes.getString("modelo"));
                        tv4.setText(mensajes.getString("placa"));
                        tv2.setText(mensajes.getString("marca"));

                    Picasso.with(detalle_auto.this).load(Constantes.IMG_US+"/"+mensajes.getString("imagen"))
                            .error(R.drawable.avatar)
                            .placeholder(R.color.blue_ligth)
                            .fit()
                            .into(iv_user_photo);

                    Picasso.with(detalle_auto.this).load(Constantes.IMG_AU+"/"+mensajes.getString("imagen_auto"))
                            .error(R.drawable.avauto)
                            .placeholder(R.color.yellow)
                            .fit()
                            .into(iv_auto_photo);

                    Log.e("URLUS",Constantes.IMG_US+"/"+mensajes.getString("imagen"));
                    Log.e("URLAU",Constantes.IMG_US+"/"+mensajes.getString("imagen_auto"));
                    loading.dismiss();
                    break;
                case "2": // FALLIDO

                    String mensaje2 = response.getString("mensaje");
                    Log.e("ver","entra-caso2 -"+mensaje2);
                    loading.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
