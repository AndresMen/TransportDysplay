package com.example.mendez.transportdysplay.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.util.AnimationMarker;
import com.example.mendez.transportdysplay.database.BD;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.activity.Inicio;
import com.example.mendez.transportdysplay.util.LatlngInterpolator;
import com.example.mendez.transportdysplay.networking.PathJSONParser;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.activity.detalle_auto;
import com.example.mendez.transportdysplay.adapter.aucerAdapter;
import com.example.mendez.transportdysplay.adapter.adapterbus;
import com.example.mendez.transportdysplay.networking.gac_service;
import com.example.mendez.transportdysplay.adapter.infomarker;
import com.example.mendez.transportdysplay.model.obau;
import com.example.mendez.transportdysplay.model.obj_bus;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class map extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    MapView mapView;
    String id, nom;
    static Marker mi;
    private Circle circle;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager lManager;
    private aucerAdapter adapter;
    private List<obau> items;

    public static Marker marker1, marker2;
    public static Polyline polyline;
    FloatingActionButton fabzo;
    public static Marker auto = null;
    int dis = 500;
    boolean mm = false;
    GeoQuery geoQuery;
    JSONObject mensaje = null;
    public static String tus;
    //public static ImageView iviva;
    public static DatabaseReference refer;
    public static ValueEventListener valuelis;
    SharedPreferences settings;
    LatlngInterpolator latlngInterpolator = new LatlngInterpolator.Linear();

    static AutoCompleteTextView autoCompleteTextView;
    ImageView btnbus,btncle;
    private List<obj_bus> countryList;
    adapterbus ada;
    private String direccion;
    private List<Address> address;
    CardView cardviewbus;
    String ped;
    public static LinearLayout iviva,lliuser,llicar,llimes;


    public map() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map1, container, false);

        settings = getActivity().getSharedPreferences(Constantes.PREFS_NAME, 0);
        id = settings.getString("id", "error");
        nom = settings.getString("nombus", "error");
        tus = settings.getString("tus", "error");
        recyclerView = (RecyclerView) view.findViewById(R.id.rec);
        recyclerView.setHasFixedSize(false);
        items = new ArrayList<>();

        lManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lManager);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            mapView.getMapAsync(this);
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        iviva = (LinearLayout) view.findViewById(R.id.iviva);
        lliuser = (LinearLayout) view.findViewById(R.id.lliuser);
        llicar = (LinearLayout) view.findViewById(R.id.llicar);
        llimes = (LinearLayout) view.findViewById(R.id.llimes);
        lliuser.setOnClickListener(this);
        llicar.setOnClickListener(this);
        llimes.setOnClickListener(this);

        fabzo = (FloatingActionButton) view.findViewById(R.id.fabzo);
        Animation mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pr);
        fabzo.setAnimation(mAnimation);
        fabzo.setOnClickListener(this);
        marker2 = null;
        adapter = new aucerAdapter(items, getContext());
        recyclerView.setAdapter(adapter);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.actvbus);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (btncle.isShown()){
                    btnbus.setVisibility(View.VISIBLE);
                    btncle.setVisibility(View.GONE);
                }
            }
        });
        btnbus = (ImageView) view.findViewById(R.id.i2);
        btnbus.setOnClickListener(this);
        btncle =(ImageView)view.findViewById(R.id.ivcle);
        btncle.setOnClickListener(this);

        initlist();
        cardviewbus = (CardView) view.findViewById(R.id.cardviewbus);

        ped=settings.getString("ped","error");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (marker2 == null) {
            vm();
        }

    }

    BroadcastReceiver broadCastPosition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] latLngs;
            if (intent.getStringExtra("loc") != null) {
                latLngs = intent.getStringExtra("loc").split(",");
                if (latLngs[0] != null & latLngs[1] != null) {
                   // Log.e("latlngrec", latLngs[0] + "," + latLngs[1]);
                    if (ped.equals("error")) {
                        agremar(Double.parseDouble(latLngs[0]), Double.parseDouble(latLngs[1]));
                    }
                    if (!settings.getString("bo", "error").equals("error")) {
                        iviva.setVisibility(View.VISIBLE);

                    } else {
                        iviva.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // mMap.setMaxZoomPreference(16);
        LatLng mapa = new LatLng(-22.012524, -63.677903);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapa, 16));
        setupGoogleMapScreenSettings(mMap);
        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        infomarker in = new infomarker(getContext());
        mMap.setInfoWindowAdapter(in);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void agremar(double lat, double lng) {
        LatLng coor = new LatLng(lat, lng);
        if (mi == null | circle == null) {
            mi = mMap.addMarker(new MarkerOptions().position(coor).title("Punto de inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__partner_funnel_helix_pin)));
            mi.setDraggable(true);
           // rec =mMap.addMarker(new MarkerOptions().position(coor).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(coor)
                    .radius(500)
                    .strokeColor(Color.RED)
                    .strokeWidth(2)
                    .fillColor(Color.TRANSPARENT));
            if (settings.getString("verecy", "error").equals("error")) {
                cerca(lat, lng);
            }
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coor,16));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    circle.getCenter(), getZoomLevel(circle)));
        } else {
            //mi.setPosition(coor);
             AnimationMarker.animateMarkerToICS(coor,latlngInterpolator,circle);
            //circle.setCenter(coor);
            if (settings.getString("verecy", "error").equals("error")) {
                cerca(lat, lng);
            }
        }

    }
    public void cerca(double x, double y) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        GeoFire geoFire = new GeoFire(ref);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(x, y), 0.5);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                Double lati = Double.parseDouble(String.valueOf(location.latitude));
                Double lngi = Double.parseDouble(String.valueOf(location.longitude));
                subscribeToUpdates(dataSnapshot.getKey(), lati, lngi,String.valueOf(dataSnapshot.child("rota").getValue()));
                Log.e("area", "entro al area " + dataSnapshot.getKey());
                Log.e("area_da","datos bearing "+dataSnapshot.child("rota").getValue());
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                removeMarker(dataSnapshot.getKey());
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private void removeMarker(String key) {
        if (mMarkers.containsKey(key)) {
            mMarkers.get(key).remove();
            mMarkers.remove(key);
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(key)) {
                items.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
        if (polyline != null) {
            polyline.remove();
        }
        if (marker2 != null) {
            marker2.remove();
        }
    }
    private void subscribeToUpdates(final String key, final Double lat, final Double lon, final String rota) {
        DatabaseReference refau = FirebaseDatabase.getInstance().getReference().child("autos").child(key);
        refau.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setMarker2(dataSnapshot, lat, lon, key,rota);
                //Log.e("locaaa", String.valueOf(dataSnapshot.getValue())+" susc");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setMarker2(DataSnapshot dataSnapshot, Double lat, Double lng, String key, String rota) {
        LatLng latLng = new LatLng(lat, lng);

        if (!mMarkers.containsKey(key)) {
            if (Objects.equals(dataSnapshot.child("estado").child("estado").getValue(), "Desocupado")) {
                mMarkers.put(key, mMap.addMarker(new MarkerOptions().title("Placa: " + String.valueOf(dataSnapshot.child("personal").child("placa").getValue())).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_lux))));
            }
        } else {
            if (Objects.equals(dataSnapshot.child("estado").child("estado").getValue(), "Desocupado")) {
               // mMarkers.get(key).setPosition(latLng);
                AnimationMarker.animateMarkerCar(mMarkers.get(key),latLng,latlngInterpolator,rota);
            }else{
                mMarkers.get(key).remove();
                mMarkers.remove(key);
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        for (HashMap.Entry<String, Marker> entry : mMarkers.entrySet()) {
            Log.e("Marker", String.valueOf(entry));
        }
        setAdapter(dataSnapshot, key);
    }

    public void setAdapter(DataSnapshot dataSnapshot, String key) {
        obau obau = new obau(String.valueOf(key), String.valueOf(dataSnapshot.child("personal").child("user").getValue()), String.valueOf(dataSnapshot.child("personal").child("placa").getValue()), String.valueOf(dataSnapshot.child("estado").getValue()), String.valueOf(dataSnapshot.child("personal").child("tkfcm").getValue()));
        if (mMarkers.containsKey(key)) {
            if (!items.isEmpty()) {
                Log.e("items", "no vacio");

                if (!co(key)) {
                    items.add(obau);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("items", "vacio");
                items.add(obau);
                adapter.notifyDataSetChanged();
            }
        }else {
            Log.e("key",key);
            for (int i=0;i<items.size();i++){
                if (items.get(i).getId().equals(key)){
                    items.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        if (adapter.items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public boolean co(String key) {
        for (obau p : items) if (p.getId().equals(key)) return true;
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Añadir marker en la posición
        if (mi != null) {
            if (marker2 == null) {
                getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),
                        String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude), 1, latLng);
                //obtegeoco(latLng);
            }
        }
    }

    public void getdirec(String origen, String destino, final int x, final LatLng latLng) {
        Log.e("veer", "entro guardar");
        Log.e("origen", origen);
        Log.e("destino", destino);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("origen", origen);
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
                                Log.e("puto", "PRECEOSA   ");

                                Log.e("puto", "respuetsa -" + response);
                                try {
                                    // Obtener estado
                                    String estado = response.getString("estado");
                                    Log.e("puto", "esatso  -" + estado);
                                    // Obtener mensaje
                                    switch (estado) {
                                        case "1":
                                            // Mostrar
                                            mensaje = response.getJSONObject("msg");
                                            List<List<HashMap<String, String>>> routes = null;
                                            JSONObject infor = null;
                                            switch (x) {
                                                case 1:
                                                    try {
                                                        marker2 = mMap.addMarker(new MarkerOptions().position(latLng));
                                                        marker2.setDraggable(true);
                                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_marker_destination));
                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor = parser.infor(mensaje);
                                                        Log.e("routes", "" + infor);

                                                        //Log.e("routes",""+infor.getString("duration"));
                                                        ArrayList<LatLng> points = null;
                                                        PolylineOptions polyLineOptions = null;

                                                        // traversing through routes
                                                        for (int i = 0; i < routes.size(); i++) {
                                                            points = new ArrayList<LatLng>();
                                                            polyLineOptions = new PolylineOptions();
                                                            List<HashMap<String, String>> path = routes.get(i);


                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);

                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);

                                                            }
                                                            polyLineOptions.addAll(points);
                                                            polyLineOptions.width(5);
                                                            polyLineOptions.color(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                                                        }

                                                        marker2.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker2.setSnippet(infor.getString("time") + "," + infor.getString("distance"));
                                                        if (polyline != null) {
                                                            polyline.remove();
                                                            polyline=null;
                                                        }
                                                        polyline = mMap.addPolyline(polyLineOptions);
                                                        // placeAutoComplete1.setText(infor.getString("end_address"));
                                                        camap(mi,marker2);
                                                        badag(infor.getString("end_address"),latLng);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;

                                                case 2:
                                                    try {
                                                        marker2.setPosition(latLng);

                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor = parser.infor(mensaje);
                                                        Log.e("paso", String.valueOf(routes));
                                                        ArrayList<LatLng> points = null;
                                                        PolylineOptions polyLineOptions = null;
                                                        // traversing through routes
                                                        Log.e("routes", String.valueOf(routes));
                                                        for (int i = 0; i < routes.size(); i++) {
                                                            points = new ArrayList<LatLng>();
                                                            polyLineOptions = new PolylineOptions();
                                                            List<HashMap<String, String>> path = routes.get(i);
                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);
                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);
                                                            }
                                                            polyLineOptions.addAll(points);
                                                            polyLineOptions.width(5);
                                                            polyLineOptions.color(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                                                        }
                                                        marker2.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker2.setSnippet(infor.getString("time") + "," + infor.getString("distance"));
                                                        if (polyline != null) {
                                                            polyline.remove();
                                                            polyline=null;
                                                        }
                                                        polyline = mMap.addPolyline(polyLineOptions);
                                                        camap(mi,marker2);
                                                        //  placeAutoComplete1.setText(infor.getString("end_address"));
                                                        badag(infor.getString("end_address"),latLng);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                case 3:
                                                    try {
                                                        LatLng latLng1 = new LatLng(Double.parseDouble(settings.getString("lato", "error")), Double.parseDouble(settings.getString("lono", "error")));
                                                        LatLng latLng2 = new LatLng(Double.parseDouble(settings.getString("latd", "error")), Double.parseDouble(settings.getString("lond", "error")));
                                                        marker1 = mMap.addMarker(new MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_pickup)));
                                                        marker2 = mMap.addMarker(new MarkerOptions().position(latLng2).icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_destination)));
                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor = parser.infor(mensaje);
                                                        Log.e("paso", String.valueOf(routes));
                                                        ArrayList<LatLng> points = null;
                                                        PolylineOptions polyLineOptions = null;
                                                        // traversing through routes
                                                        Log.e("routes", String.valueOf(routes));
                                                        for (int i = 0; i < routes.size(); i++) {
                                                            points = new ArrayList<LatLng>();
                                                            polyLineOptions = new PolylineOptions();
                                                            List<HashMap<String, String>> path = routes.get(i);
                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);
                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);
                                                            }
                                                            polyLineOptions.addAll(points);
                                                            polyLineOptions.width(5);
                                                            polyLineOptions.color(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                                                        }
                                                        //marker1.setTitle("Desde: " + infor.getString("start_address"));
                                                        //marker2.setTitle("Hasta: " + infor.getString("end_address"));
                                                        marker2.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker2.setSnippet(infor.getString("time") + "," + infor.getString("distance"));
                                                        marker1.setTitle(infor.getString("start_address") + "-" + infor.getString("end_address"));
                                                        marker1.setSnippet(infor.getString("time") + "," + infor.getString("distance"));
                                                        polyline = mMap.addPolyline(polyLineOptions);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                default:
                                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                                                    break;
                                            }

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

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (polyline != null) {
            polyline.remove();
        }
        //obtegeoco(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        if (mi!=null&marker2!=null){
            getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),
                    String.valueOf(marker2.getPosition().latitude) + "," + String.valueOf(marker2.getPosition().longitude),
                    2, new LatLng(marker2.getPosition().latitude, marker2.getPosition().longitude));
        }
    }

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius()+ circle.getRadius()/2;
            Log.e("radio", String.valueOf(radius));
            double scale = radius / 500;
            Log.e("radio", String.valueOf(scale));
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
            Log.e("radio", String.valueOf(zoomLevel));
        }
        return zoomLevel;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lliuser:
                startActivity(new Intent(getContext(), detalle_auto.class).putExtra("usto", tus));
                break;

            case R.id.llicar:
                if (auto != null) {
                    Log.e("auto", "auto no esta");
                    auto.remove();
                    refer.removeEventListener(valuelis);
                    auto = null;
                } else {
                    Log.e("auto", "auto esta");
                    verma();
                }

                break;
            case R.id.llimes:
                startActivity(new Intent(getContext(), Inicio.class).putExtra("me", "me").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.fabzo:
                if (circle != null) {
                    if (!mm) {
                        if (dis == 500) {
                            dis = 1000;
                            circle.setRadius(1000);
                            geoQuery.setRadius(1);
                        } else {
                            if (dis == 1000) {
                                dis = 1500;
                                circle.setRadius(1500);
                                geoQuery.setRadius(1.5);
                                fabzo.setImageResource(R.drawable.icon_lupamen);
                                mm = true;
                            }
                        }
                    } else {
                        if (dis == 1500) {
                            dis = 1000;
                            circle.setRadius(1000);
                            geoQuery.setRadius(1);
                        } else {
                            if (dis == 1000) {
                                dis = 500;
                                circle.setRadius(500);
                                geoQuery.setRadius(0.5);
                                fabzo.setImageResource(R.drawable.icon_lupa);
                                mm = false;
                            }
                        }
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            circle.getCenter(), getZoomLevel(circle)));
                }
                break;
            case R.id.i2:
                if (mi!=null){
                    BD base=new BD(getContext(),"baseSms",null,1);
                    final SQLiteDatabase bd=base.getWritableDatabase();
                    direccion = autoCompleteTextView.getText().toString()+", Yacuiba";

                    if(autoCompleteTextView.getText().toString().equals("")){
                        Toast.makeText(getContext(), "No hay direccion que buscar", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Buscando \""+direccion+"\"", Toast.LENGTH_SHORT).show();

                        Geocoder coder = new Geocoder(getContext());

                        try {
                            address = coder.getFromLocationName(direccion, 1);
                            if (address.size()>0){
                                Address location = address.get(0);
                                String []calle=location.getAddressLine(0).split(",");
                                if (!location.getAddressLine(0).equals("Yacuíba, Bolivia")){
                                    boolean sas=false;
                                    @SuppressLint("Recycle") Cursor fil = bd.rawQuery(
                                            "select * from bus", null);
                                    if (fil.moveToFirst()) {
                                        do {
                                            if(fil.getString(0).equals(calle[0])){
                                                sas=true;
                                               // Toast.makeText(getContext(), String.valueOf(fil.getString(0))+"__"+calle[0], Toast.LENGTH_SHORT).show();
                                            }
                                        }while (fil.moveToNext());
                                    }
                                    if (marker2==null){
                                        if (!sas){
                                            getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),
                                                    String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()),1,new LatLng(location.getLatitude(),location.getLongitude()));
                                            //marker2=mMap.addMarker(new MarkerOptions().title("destino: "+location.getAddressLine(0)).position(new LatLng(location.getLatitude(),location.getLongitude())));
                                            //marker2.setDraggable(true);
                                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),16));
                                        }else{
                                            Cursor cu=bd.rawQuery("select * from bus where calle="+"'"+calle[0]+"'",null);
                                            if (cu.moveToNext()){
                                                do {
                                                    getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),cu.getString(1)+","+cu.getString(2),1,new LatLng(Double.parseDouble(cu.getString(1)),Double.parseDouble(cu.getString(2))));
                                                }while (cu.moveToNext());
                                            }
                                            cu.close();
                                        }
                                    }
                                    else{
                                        if (!sas){
                                            getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),
                                                    String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()), 2, new LatLng(location.getLatitude(),location.getLongitude()));

                                        }else{
                                            Cursor cur=bd.rawQuery("select * from bus where calle="+"'"+calle[0]+"'",null);
                                            if (cur.moveToNext()){
                                                do {
                                                    getdirec(String.valueOf(mi.getPosition().latitude) + "," + String.valueOf(mi.getPosition().longitude),
                                                            String.valueOf(cur.getString(1)) + "," + String.valueOf(cur.getString(2)), 2, new LatLng(Double.parseDouble(cur.getString(1)),Double.parseDouble(cur.getString(2))));

                                                }while (cur.moveToNext());
                                            }
                                        }
                                    }
                                    bd.close();
                                    fil.close();

                                }else{
                                    Toast.makeText(getContext(), "No existe la calle", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(getContext(), "cero resultados", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Toast.makeText(getContext(), "No se ha encontrado la dirección : (", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getContext(), "Espere a que cargue su ubicación", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ivcle:
                if (marker2!=null&polyline!=null){
                    marker2.remove();
                    marker2=null;
                    polyline.remove();
                    polyline=null;
                }
                autoCompleteTextView.setText("");
                btnbus.setVisibility(View.VISIBLE);
                btncle.setVisibility(View.GONE);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        circle.getCenter(), getZoomLevel(circle)));
                break;
        }
    }

    public void opiviva() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.infor_vi_auto2, null);
        dialogBuilder.setView(dialogView);
        final LinearLayout llius = (LinearLayout) dialogView.findViewById(R.id.lliuser);
        final LinearLayout llica = (LinearLayout) dialogView.findViewById(R.id.llicar);
        final LinearLayout llimes = (LinearLayout) dialogView.findViewById(R.id.llimes);
        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setWindowAnimations(R.style.dialog_animation_translate);
        llius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), detalle_auto.class).putExtra("usto", tus));
            }
        });
        llica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auto != null) {
                    Log.e("auto", "auto no esta");
                    auto.remove();
                    refer.removeEventListener(valuelis);
                    auto = null;
                    b.dismiss();
                } else {
                    Log.e("auto", "auto esta");
                    verma();
                    b.dismiss();
                }
            }
        });
        llimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Inicio.class).putExtra("me", "me").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        dialogBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        b.show();
    }

    public void verma() {
        refer = FirebaseDatabase.getInstance().getReference("users").child(tus);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maraut();
                Log.e("ubicacion", "auto");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "al pedir ubicacion " + databaseError);
            }
        };
        refer.addValueEventListener(valueEventListener);
        valuelis = valueEventListener;
    }

    public void maraut() {
        DatabaseReference refe = FirebaseDatabase.getInstance().getReference("users").child(tus);
        /*GeoFire geoFire = new GeoFire(refe);
        geoFire.getLocation(tus, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                LatLng latLng = new LatLng(location.latitude, location.longitude);
                if (auto == null) {
                    auto = mMap.addMarker(new MarkerOptions().position(latLng).title("Auto").icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));
                } else {
                    auto.setPosition(latLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "al pedir ubicacion a geofire " + databaseError);
            }
        });*/

        refe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double lati = Double.parseDouble(String.valueOf(dataSnapshot.child("l").child("0").getValue()));
                Double lngi = Double.parseDouble(String.valueOf(dataSnapshot.child("l").child("1").getValue()));
                LatLng latLng = new LatLng(lati, lngi);
                if (auto == null) {
                    auto = mMap.addMarker(new MarkerOptions().position(latLng).title("Auto").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_lux)));
                } else {
                    auto.setPosition(latLng);
                    AnimationMarker.animateMarkerCar(auto,latLng,latlngInterpolator, String.valueOf(dataSnapshot.child("rota").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void vm() {
        if (!settings.getString("lato", "error").equals("error") | !settings.getString("lono", "error").equals("error") | !settings.getString("latd", "error").equals("error") | !settings.getString("lond", "error").equals("error")) {
            getdirec(settings.getString("lato", "error") + "," + settings.getString("lono", "error"),
                    settings.getString("latd", "error") + "," + settings.getString("lond", "error"), 3, null);
        cardviewbus.setVisibility(View.GONE);
        fabzo.setVisibility(View.GONE);
        }else{
            cardviewbus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //locationManager.removeUpdates(locationListener);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onPause() {
        super.onPause();
            if (isMyServiceRunning(gac_service.class)){
                getActivity().unregisterReceiver(broadCastPosition);
                getActivity().stopService(new Intent(getActivity(), gac_service.class));
                //Toast.makeText(getContext(), "pasoonpause", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onResume() {
        super.onResume();

            if (!isMyServiceRunning(gac_service.class)){
                getActivity().startService(new Intent(getActivity(), gac_service.class));
                getActivity().registerReceiver(broadCastPosition, new IntentFilter("bcNewMessage"));
                // Toast.makeText(getContext(), "pasoonresume", Toast.LENGTH_SHORT).show();
                //Log.e("servicio","pasos");
            }



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

    public static LatLng orig(){
        return new LatLng(mi.getPosition().latitude,mi.getPosition().longitude);
    }
    public static LatLng dest(){
        if (marker2!=null) {
            return new LatLng(marker2.getPosition().latitude, marker2.getPosition().longitude);
        }
        return null ;
    }

    public void initlist(){
        countryList=new ArrayList<>();
        BD base=new BD(getContext(),"baseSms",null,1);
        final SQLiteDatabase bd=base.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from bus", null);
        if (fila.moveToFirst()) {
            do {
                countryList.add(new obj_bus(fila.getString(0)));
            }while (fila.moveToNext());
        }
        bd.close();
        fila.close();

        ada = new adapterbus(getContext(), countryList);

        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(ada);
    }

    public void badag(String s,LatLng latLng) {
        BD base=new BD(getContext(),"baseSms",null,1);
        final SQLiteDatabase bd=base.getWritableDatabase();
        String []desti=s.split(",");
        autoCompleteTextView.setText(desti[0]);
        btnbus.setVisibility(View.GONE);
        btncle.setVisibility(View.VISIBLE);
        boolean sas=false;
        boolean aa=false;
        @SuppressLint("Recycle") Cursor fil = bd.rawQuery(
                "select * from bus", null);
        if (fil.moveToFirst()) {
            do {

                if (fil.getString(0).equals(desti[0])&fil.getString(1).equals(String.valueOf(latLng.latitude))&fil.getString(2).equals(String.valueOf(latLng.longitude))){
                    aa=true;
                }
                if(fil.getString(0).equals(desti[0])){
                    sas=true;
                }
            }while (fil.moveToNext());
        }
        Toast.makeText(getContext(),String.valueOf(aa),Toast.LENGTH_SHORT).show();
        if (!aa){
            ContentValues values=new ContentValues();
            values.put("calle",desti[0]);
            values.put("lat",String.valueOf(latLng.latitude));
            values.put("lng",String.valueOf(latLng.longitude));
            if (!sas){
                bd.insert("bus",null,values);
            }else{
                bd.update("bus",values,"calle="+"'"+desti[0]+"'",null);
            }
            initlist();
        }

        fil.close();
        bd.close();
    }

    public void camap(Marker m1,Marker m2){
        LatLngBounds.Builder builder = new LatLngBounds.Builder(); //the include method will calculate the min and max bound.
        builder.include(m1.getPosition());
        builder.include(m2.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        int height = getActivity().getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);
        // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width/2, height/2, padding);
        mMap.animateCamera(cu);
    }
  public static void cer(){
        autoCompleteTextView.dismissDropDown();
    }
}
