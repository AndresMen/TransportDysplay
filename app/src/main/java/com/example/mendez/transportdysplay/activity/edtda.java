package com.example.mendez.transportdysplay.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class edtda extends AppCompatActivity implements View.OnClickListener {

    EditText edtnom;
    RelativeLayout rlnom,btng;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog progressDialog;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    TextView edtda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edtda2);
        setTitle("Editar nombre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        edtnom=(EditText)findViewById(R.id.edit_nom);
        edtnom.setText(settings.getString("nom","error"));

        rlnom=(RelativeLayout)findViewById(R.id.rlednom);
        btng=(RelativeLayout)findViewById(R.id.layout_gua);
        edtda=(TextView)findViewById(R.id.tvda);
        edtda.setOnClickListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                overridePendingTransition(R.anim.pop_enter,R.anim.pop_exit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvda:
                progressDialog = new ProgressDialog(edtda.this);
                progressDialog.setMessage("Subiendo..");
                progressDialog.show();
                editar(user.getUid(),edtnom.getText().toString(),null);
                break;
        }
    }

    public void editar(String tok,String nomb,String num_cel) {
        Log.e("veer","entro guardar");
        Log.e("token_cli",tok);
        //Log.e("nombre", nomb.toString());
        //Log.e("correo",cor.toString());
        //Log.e("num_cel", num_cel.toString());

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("token_cli",tok);
        map.put("nombre", nomb);
        map.put("num_cel", num_cel);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_CLI,
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
                                progressDialog.dismiss();
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
    private void procesarRespuesta(JSONObject response) {
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
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("clientes").child(user.getUid());
                    HashMap<String,String>mapa=new HashMap<>();
                        editor = settings.edit();
                        editor.putString("nom",edtnom.getText().toString());
                        editor.apply();
                        mapa.put("nombre",edtnom.getText().toString());
                        mapa.put("numero",settings.getString("num","error"));
                        reference.setValue(mapa);
                    finish();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    // getApplication().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    progressDialog.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
