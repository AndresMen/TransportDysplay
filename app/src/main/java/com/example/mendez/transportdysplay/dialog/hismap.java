package com.example.mendez.transportdysplay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.adapter.infomarker;
import com.example.mendez.transportdysplay.adapter.viAdapter;
import com.example.mendez.transportdysplay.networking.PathJSONParser;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class hismap extends DialogFragment implements OnMapReadyCallback {
    GoogleMap mMap;
    HashMap<String, Object> mapo;
    HashMap<String, Object> mapd;
    Context mcontext;
    Polyline polyline;
    TextView tvfe,tvho,tvcli;

    JSONObject mensaje =null;
    Marker marker1,marker2;
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    public hismap() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    public AlertDialog createDia() {
        viAdapter.carga.dismiss();
        settings = getContext().getSharedPreferences(Constantes.PREFS_NAME, 0);
        mapo = (HashMap<String, Object>) getArguments().getSerializable("mapo");
        mapd = (HashMap<String, Object>) getArguments().getSerializable("mapd");

        String addresso = String.valueOf(getArguments().get("origenlit"));
        String addressd = String.valueOf(getArguments().get("destinolit"));
        String fe=String.valueOf(getArguments().get("fecha"));
        String ho=String.valueOf(getArguments().get("hora"));
        String cli=String.valueOf(getArguments().get("cliente"));

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyCustomTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.map_dialogdet, null);
        tvfe=(TextView)v.findViewById(R.id.tvfe);
        tvho=(TextView)v.findViewById(R.id.tvho);
        tvcli=(TextView)v.findViewById(R.id.tvcli);
        tvfe.setText(fe);
        tvho.setText(ho);
        tvcli.setText(cli);
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        mapFragment.getMapAsync(this);
        builder.setView(v);

        return builder.create();
    }




    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDia();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng mapa = new LatLng(-22.012524, -63.677903);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapa, 16));

        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setMapToolbarEnabled(false);
        infomarker in=new infomarker(getContext());
        mMap.setInfoWindowAdapter(in);
        getdirec(String.valueOf(mapo.get("lat"))+","+String.valueOf(mapo.get("lng")),String.valueOf(mapd.get("lat"))+","+String.valueOf(mapd.get("lng")));

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }else{
            assert getFragmentManager() != null;
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.fragment1));
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
        editor=settings.edit();
        editor.putString("hmap","error");
        editor.apply();
        super.onDestroyView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }

    public void getdirec(final String origen, final String destino) {
        Log.e("veer","entro guardar");
        Log.e("origen",origen);
        Log.e("destino",destino);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("origen",origen);
        map.put("destino", destino);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.DIR,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");

                                Log.e("puto","respuetsa -"+response);
                                try {
                                    // Obtener estado
                                    String estado = response.getString("estado");
                                    Log.e("puto","esatso  -"+estado);
                                    // Obtener mensaje
                                    switch (estado) {
                                        case "1":
                                            // Mostrar
                                            mensaje = response.getJSONObject("msg");
                                            List<List<HashMap<String, String>>> routes;
                                            JSONObject infor;
                                            String[] latlongor =  origen.split(",");
                                            double latitudeor = Double.parseDouble(latlongor[0]);
                                            double longitudeor = Double.parseDouble(latlongor[1]);
                                            String[] latlongdes =  destino.split(",");
                                            double latitudedes = Double.parseDouble(latlongdes[0]);
                                            double longitudedes = Double.parseDouble(latlongdes[1]);

                                                    try {
                                                        marker1= mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeor,longitudeor)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_pickup)));
                                                        marker2= mMap.addMarker(new MarkerOptions().position(new LatLng(latitudedes,longitudedes)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_destination)));

                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor =parser.infor(mensaje);
                                                        Log.e("routes",""+infor);

                                                        //Log.e("routes",""+infor.getString("duration"));
                                                        final ArrayList<LatLng> points = new ArrayList<LatLng>();;
                                                        PolylineOptions  polyLineOptions = new PolylineOptions();;
                                                        polyLineOptions.width(5);
                                                        polyLineOptions.color(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                                                        // traversing through routes
                                                       // polyline = mMap.addPolyline(polyLineOptions);
                                                        for (int i = 0; i < routes.size(); i++) {


                                                            List<HashMap<String, String>> path = routes.get(i);


                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);

                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);

                                                            }
                                                            polyLineOptions.addAll(points);

                                                        }
                                                        marker2.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker2.setSnippet(infor.getString("time") + "," + infor.getString("distance"));
                                                        marker1.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker1.setSnippet(infor.getString("time") + "," + infor.getString("distance"));

                                                        polyline = mMap.addPolyline(polyLineOptions);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder(); //the include method will calculate the min and max bound.
                                            builder.include(marker1.getPosition());
                                            builder.include(marker2.getPosition());
                                            LatLngBounds bounds = builder.build();
                                            int width = getActivity().getResources().getDisplayMetrics().widthPixels;
                                            int height = getActivity().getResources().getDisplayMetrics().heightPixels;
                                            int padding = (int) (width * 0.10);
                                            // offset from edges of the map 10% of screen
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width/2, height/2, padding);
                                            Log.e("dimension",width+"///"+height);
                                            mMap.animateCamera(cu);

                                            //Log.e("mensa",String.valueOf(mensaje));
                                            break;

                                        case "2":
                                            // Mostrar mensaje
                                            Toast.makeText(
                                                    getContext(),
                                                    "Error",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));

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

}
