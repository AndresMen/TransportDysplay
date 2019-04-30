package com.example.mendez.transportdysplay.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.activity.Inicio;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.networking.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class dialcal extends DialogFragment implements RatingBar.OnRatingBarChangeListener {
    Context mcontext;
    LinearLayout llar;
    LinearLayout llab,llm;
    TextView dc;
    EditText edte,edtem;
    Button btnenca,btncaen;
    boolean x=false;
    ProgressDialog loading = null;
    public dialcal(){
        setRetainInstance(true);
    }

    public AlertDialog createDia(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialcal, null);
        final RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(this);
        llar=(LinearLayout)v.findViewById(R.id.llar);
        llab=(LinearLayout)v.findViewById(R.id.llab);
        llm=(LinearLayout)v.findViewById(R.id.llm);
        edte=(EditText)v.findViewById(R.id.edte);
        edtem=(EditText)v.findViewById(R.id.edtem);
        dc=(TextView)v.findViewById(R.id.dc);
        btnenca=(Button)v.findViewById(R.id.btnenca);
        btncaen=(Button)v.findViewById(R.id.btncanen);

        builder.setView(v);
        setCancelable(false);
        dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!x){
                    edte.setVisibility(View.VISIBLE);
                    dc.setText("No dejar comentario");
                    x=true;
                }else{
                    edte.setVisibility(View.GONE);
                    dc.setText("Dejar comentario");
                    x=false;
                }

            }
        });
        btnenca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent newsm=new Intent(getContext(),Inicio.class);
                newsm.putExtra("me","tv");
                startActivity(newsm);*/
                //loading = ProgressDialog.show(getContext(), "Subiendo...", "Espere por favor...", false, false);
                //guardarvia();
                int x=(int)ratingBar.getRating();
                if (llar.isShown()){

                    if (edte.isShown()){
                        Toast.makeText(getContext(), edte.getText().toString()+" con "+x+" estrellas", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "sin comentarios con "+x+" estrellas", Toast.LENGTH_SHORT).show();
                    }
                }
                if (llm.isShown()){
                    Toast.makeText(getContext(), edtem.getText().toString()+" con "+x+" estrellas", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btncaen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsm=new Intent(getContext(),Inicio.class);
                newsm.putExtra("me","tv");
                startActivity(newsm);
            }
        });
        return  builder.create();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setDialogPosition();
        return createDia();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            if ((int)rating<=3) {
                if (llar.isShown()){
                    Animation a1 = AnimationUtils.loadAnimation(getContext(), R.anim.exit_up);
                    llar.setAnimation(a1);
                    llar.setVisibility(View.GONE);
                    Animation a2 = AnimationUtils.loadAnimation(getContext(), R.anim.enter_up);
                    llm.setAnimation(a2);
                    llm.setVisibility(View.VISIBLE);
                    edte.setVisibility(View.GONE);
                    dc.setVisibility(View.GONE);
            }
        }


        if((int)rating>3) {
            if (llm.isShown()){
                Animation a1 = AnimationUtils.loadAnimation(getContext(), R.anim.enter_up);
                llar.setAnimation(a1);
                llar.setVisibility(View.VISIBLE);
                Animation a2 = AnimationUtils.loadAnimation(getContext(), R.anim.exit_up);
                llm.setAnimation(a2);
                llm.setVisibility(View.GONE);
                dc.setVisibility(View.VISIBLE);
                dc.setText("Dejar comentario");
                x = false;
            }

        }

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    public void guardarvia(String canst,String comen,String ide) {
        Log.e("veer","entro guardar");

        Log.e("canst",canst);
        Log.e("comen",comen);
        Log.e("idevi", ide);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("can_star",canst);
        map.put("comen",comen);
        map.put("id", ide);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(mcontext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_VI_ST,
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
                            mcontext,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    loading.dismiss();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            mcontext,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    // getApplication().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    loading.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
