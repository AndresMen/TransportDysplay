package com.example.mendez.transportdysplay.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.transportdysplay.database.BD;
import com.example.mendez.transportdysplay.config.Constantes;
import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.dialog.diafragsms;
import com.example.mendez.transportdysplay.dialog.dialcal;
import com.example.mendez.transportdysplay.fragment.Viajes_fragment;
import com.example.mendez.transportdysplay.fragment.iniciocon;
import com.example.mendez.transportdysplay.fragment.map;
import com.example.mendez.transportdysplay.fragment.message;
import com.example.mendez.transportdysplay.networking.gac_service;
import com.example.mendez.transportdysplay.networking.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class Inicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,iniciocon.OnFragmentInteractionListener,map.OnFragmentInteractionListener,
Viajes_fragment.OnFragmentInteractionListener,message.OnFragmentInteractionListener, View.OnClickListener {

    private final int MIS_PERMISOS = 100;
    static boolean not = false;

    Bundle avbun;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String nomh, numh;
    TextView nomhe, numhe;

    ProgressDialog progressDialog;
    String sms, ames;
    NotificationManager nm;
    NavigationView navigationView;
    static String ns = Context.NOTIFICATION_SERVICE;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        validaPermisos();
        nm = (NotificationManager) getSystemService(ns);
        mAuth = FirebaseAuth.getInstance();
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        nomhe = (TextView) hView.findViewById(R.id.tv_nom);
        numhe = (TextView) hView.findViewById(R.id.textViewema);
        nomhe.setOnClickListener(this);
        numhe.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.ini);
            displaySelectedScreen(R.id.ini);
        }
        String rf = getIntent().getStringExtra("rf");
        if (rf != null) {
            Log.e("rf", rf);
            if (rf.equals("1")) {
                navigationView.setCheckedItem(R.id.map);
                displaySelectedScreen(R.id.map);
            }
        }
        avbun = getIntent().getBundleExtra("bun");
        if (avbun != null) {
            for (String key : avbun.keySet()) {
                Log.e("datos_" + key, String.valueOf(avbun.get(key)));
            }
            if (String.valueOf(avbun.get("av")).equals("n")) {
                editor = settings.edit();
                editor.putString("bo", "bo");
                editor.apply();
                navigationView.setCheckedItem(R.id.map);
                displaySelectedScreen(R.id.map);
            } else {
                if (String.valueOf(avbun.get("av")).equals("av")) {
                    map.iviva.setVisibility(View.GONE);
                    avbun.clear();
                    Constantes.termi(Inicio.this);
                    /*editor = settings.edit();
                    editor.putString("lato", "error");
                    editor.putString("lono", "error");
                    editor.putString("latd", "error");
                    editor.putString("lond", "error");
                    editor.putString("tus", "error");
                    editor.putString("nombus", "error");
                    editor.putString("pla", "error");
                    editor.putString("tkdes", "error");
                    editor.putString("idvia", "error");
                    editor.putString("bo", "error");
                    editor.putString("verecy", "error");*/
                    if (map.marker1 != null) {
                        map.marker1.remove();
                        map.marker1 = null;
                    }
                    if (map.marker2 != null) {
                        map.marker2.remove();
                        map.marker2 = null;
                    }
                    if (map.polyline != null) {
                        map.polyline.remove();
                        map.polyline = null;
                    }
                    if (map.auto != null) {
                        map.refer.removeEventListener(map.valuelis);
                        map.auto.remove();
                        map.auto = null;
                    }

                   // editor.apply();
                    avbun = null;
                    sms = null;
                    map.tus = "error";
                    dialcal dica=new dialcal();
                    dica.show(getSupportFragmentManager(),"Dialogo calificacion");
                    //navigationView.setCheckedItem(R.id.via);
                    //displaySelectedScreen(R.id.via);
                } else {
                    if (String.valueOf(avbun.get("av")).equals("m")) {
                        editor = settings.edit();
                        editor.putString("notm", "1");
                        editor.apply();
                        smsg(1);
                    }
                }
            }
        }
        ames = getIntent().getStringExtra("me");
        if (ames != null) {
            if (ames.equals("me")) {
                displaySelectedScreen(R.id.mens);
                navigationView.setCheckedItem(R.id.mens);
                DatabaseReference sm = FirebaseDatabase.getInstance().getReference("autos/" + settings.getString("tus", "error") + "/viaje/smsau");
                sm.removeValue();
            }
            if (ames.equals(("tv"))){
                navigationView.setCheckedItem(R.id.via);
                displaySelectedScreen(R.id.via);
            }
        }

        //Log.e("token", FirebaseInstanceId.getInstance().getToken() + "");
       //dialcal dialcal= new dialcal();
       //dialcal.show(getSupportFragmentManager(), "Dialogo Calificacion");

    }

    public void loadSharedPrefs(String... prefs) {
        // Logging messages left in to view Shared Preferences. I filter out all logs except for ERROR; hence why I am printing error messages.
        Log.i("Loading Shared Prefs", "-----------------------------------");
        Log.i("----------------", "---------------------------------------");
        for (String pref_name : prefs) {
            SharedPreferences preference = getSharedPreferences(pref_name, MODE_PRIVATE);
            for (String key : preference.getAll().keySet()) {
                Log.i(String.format("Shared Preference : %s - %s", pref_name, key), preference.getString(key, "error!"));
            }
            Log.i("----------------", "---------------------------------------");
        }
        Log.i("Finished Shared Prefs", "----------------------------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        nomh = settings.getString("nom", "error");
        numh = settings.getString("num", "error");
        Log.e("nomnum",nomh+","+numh);
        if (nomh.equals("error")&numh.equals("error")){
            FirebaseAuth.getInstance().signOut();
            editor = settings.edit();
            editor.putString("nom","error");
            editor.putString("num","error");
            editor.apply();
            Toast.makeText(getBaseContext(), "passs", Toast.LENGTH_SHORT).show();
            Intent lo = new Intent(getBaseContext(), Crear_cuenta.class);
            lo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(lo);
            finish();
        }
    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.conten);
            if (!(f instanceof iniciocon)) {
                navigationView.setCheckedItem(R.id.ini);
                displaySelectedScreen(R.id.ini);
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        nomh = settings.getString("nom", "error");
        numh = settings.getString("num", "error");
        pres();
        nomhe.setText(nomh);
        numhe.setText(numh);
        if (!settings.getString("tus", "error").equals("error")) {
            Log.e("entro", "hay tus");

            if (!settings.getString("bo", "error").equals("bo")) {
                Log.e("entro", "hay bo");
                veres();
            } else {
                if (settings.getString("notm", "error").equals("0") | settings.getString("notm", "error").equals("error")) {
                    smsg(2);
                }
            }
            verv();
        }
        loadSharedPrefs(Constantes.PREFS_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(broadCastGPS);
        Log.e("paso", "on pause");
        user=mAuth.getCurrentUser();
        if (user!=null){
            pres();
        }
    }

    public void veres() {
        DatabaseReference datos = FirebaseDatabase.getInstance().getReference("autos/" + settings.getString("tus", "error"));
        datos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("datasnap", dataSnapshot.getValue().toString());
                nm.cancelAll();

                if (dataSnapshot.child("estado").child("estado").getValue().equals("Ocupado")) {
                    editor = settings.edit();
                    editor.putString("bo", "bo");
                    editor.apply();
                    displaySelectedScreen(R.id.map);
                    navigationView.setCheckedItem(R.id.map);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error", databaseError.toString());
            }
        });
    }

    public void verv() {
        DatabaseReference datos = FirebaseDatabase.getInstance().getReference("autos/" + settings.getString("tus", "error") + "/viaje");
        datos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    nm.cancelAll();
                    Constantes.termi(Inicio.this);
                   /* editor = settings.edit();
                    editor.putString("lato", "error");
                    editor.putString("lono", "error");
                    editor.putString("latd", "error");
                    editor.putString("lond", "error");
                    editor.putString("tus", "error");
                    editor.putString("nombus", "error");
                    editor.putString("pla", "error");
                    editor.putString("tkdes", "error");
                    editor.putString("idvia", "error");
                    editor.putString("bo", "error");
                    editor.putString("verecy", "error");*/
                    if (map.marker1 != null) {
                        map.marker1.remove();
                        map.marker1 = null;
                    }
                    if (map.marker2 != null) {
                        map.marker2.remove();
                        map.marker2 = null;
                    }
                    if (map.polyline != null) {
                        map.polyline.remove();
                        map.polyline = null;
                    }
                    if (map.auto != null) {
                        map.refer.removeEventListener(map.valuelis);
                        map.auto.remove();
                        map.auto = null;
                    }

                   // editor.apply();
                    avbun = null;
                    sms = null;
                    map.tus = "error";
                    dialcal dica=new dialcal();
                    dica.show(getSupportFragmentManager(),"Dialogo calificacion");
                    //navigationView.setCheckedItem(R.id.via);
                    //displaySelectedScreen(R.id.via);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errorverver", databaseError.toString());
            }
        });
    }

    public void smsg(final int t) {
        final String[] x = new String[1];
        x[0] = "";
        final DatabaseReference sm = FirebaseDatabase.getInstance().getReference("autos/" + settings.getString("tus", "error") + "/viaje/smsau");
        sm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataChildren : dataSnapshot.getChildren()) {
                        setMsg(dataChildren);
                       // Log.e("data", String.valueOf(dataChildren.getValue()));
                        x[0] = x[0] + dataChildren.child("sms").getValue() + "\n";
                    }
                    switch (t) {
                        case 1:
                            navigationView.setCheckedItem(R.id.mens);
                            displaySelectedScreen(R.id.mens);
                            editor = settings.edit();
                            editor.putString("notm", "0");
                            editor.apply();
                            //sm.removeValue();
                            break;
                        case 2:
                            Fragment f = getSupportFragmentManager().findFragmentById(R.id.conten);
                            if (!(f instanceof message)) {
                                //Toast.makeText(getBaseContext(),"fragment no visible",Toast.LENGTH_SHORT).show();
                                Bundle b = new Bundle();
                                b.putString("nsm", x[0]);

                                final diafragsms diafragsms = new diafragsms();
                                diafragsms.setArguments(b);
                                diafragsms.show(getSupportFragmentManager(), "Dialogo SMS");
                                //sm.removeValue();
                            } else { // do something with f ((CustomFragmentClass) f).doSomething();
                                // Toast.makeText(getBaseContext(),"fragment visible",Toast.LENGTH_SHORT).show();
                                try {
                                    navigationView.setCheckedItem(R.id.mens);
                                    displaySelectedScreen(R.id.mens);
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                                    Log.e("error", e.toString());
                                }
                                //sm.removeValue();
                            }
                            // displaySelectedScreen(R.id.mens);

                            break;
                    }
                    sm.removeValue();
                    x[0] = "";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("smserror", databaseError.toString());
            }
        });
    }

    public void setMsg(DataSnapshot dataSnapshot) {
        nm.cancelAll();
        BD base = new BD(getBaseContext(), "baseSms", null, 1);
        final SQLiteDatabase bd = base.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("sms", String.valueOf(dataSnapshot.child("sms").getValue()));
        registro.put("fecha", String.valueOf(dataSnapshot.child("fecha").getValue()));
        registro.put("hora", String.valueOf(dataSnapshot.child("hora").getValue()));
        registro.put("nombre", String.valueOf(dataSnapshot.child("nombre").getValue()));
        registro.put("placa", String.valueOf(dataSnapshot.child("placa").getValue()));
        registro.put("tipo", "1");
        bd.insert("sms", null, registro);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.conten);
            if (!(f instanceof map)) {
                navigationView.setCheckedItem(R.id.map);
                displaySelectedScreen(R.id.map);
            }else{
                //finish();
                super.onBackPressed();
            }
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemid) {
        Fragment fragment = null;

        switch (itemid) {
            case R.id.ini:
                fragment = new iniciocon();
                setTitle("Inicio");
                break;
            case R.id.map:
                registerReceiver(broadCastGPS, new IntentFilter("avgps"));
                fragment = new map();
                setTitle("Mapa");
                break;
            case R.id.via:
                fragment = new Viajes_fragment();
                setTitle("Viajes");
                break;
            case R.id.mens:
                fragment = new message();
                setTitle("Mensajes");
                break;
            case R.id.logt:
                signOut();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.replace(R.id.conten, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void sendEmailVerification() {
        // Disable button
        user = mAuth.getCurrentUser();
        // Send verification email
        // [START send_email_verification]

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        if (task.isSuccessful()) {
                            Toast.makeText(Inicio.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            verifi(user.getEmail());

                        } else {
                            Log.e("error", "sendEmailVerification", task.getException());
                            Toast.makeText(Inicio.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public void verifi(String ema) {
        final AlertDialog.Builder alertOpcion = new AlertDialog.Builder(Inicio.this);
        alertOpcion.setTitle("Aviso");
        alertOpcion.setMessage("Verifique su email: " + ema);
        alertOpcion.setCancelable(false);
        alertOpcion.setPositiveButton("Volver a enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmailVerification();
            }
        });
        alertOpcion.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.e("fireuser", user.getEmail() + "-" + user.getUid());
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //PhoneAuthCredential credential = PhoneAuthProvider.getCredential();

                        //user.reauthenticate();
                        FirebaseUser userre = FirebaseAuth.getInstance().getCurrentUser();
                        if (userre.isEmailVerified()) {
                            Toast.makeText(getBaseContext(), "Email verificado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Verifique su email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        alertOpcion.show();
    }

    private void signOut() {
        BD base = new BD(getBaseContext(), "baseSms", null, 1);
        final SQLiteDatabase bd = base.getWritableDatabase();

        final AlertDialog.Builder alertOpcion = new AlertDialog.Builder(Inicio.this);
        alertOpcion.setTitle("Aviso");
        alertOpcion.setMessage("Saldra de su cuenta, se borraran todos sus datos");
        alertOpcion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertOpcion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               bd.delete("sms",null,null);
               bd.delete("bus",null,null);
               bd.close();

                FirebaseAuth.getInstance().signOut();
                editor = settings.edit();
                editor.putString("nom","error");
                editor.putString("num","error");
                editor.apply();
                //pres();
                Intent lo = new Intent(getBaseContext(), Crear_cuenta.class);
                lo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lo);
                finish();
            }
        });

        alertOpcion.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MIS_PERMISOS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {//el dos representa los 2 permisos
                Toast.makeText(getBaseContext(), "Permisos aceptados", Toast.LENGTH_SHORT).show();
                //btnf.setEnabled(true);
            }
        } else {
            solicitarPermisosManual();
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getBaseContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Inicio.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, SYSTEM_ALERT_WINDOW}, 100);
            }
        });
        dialogo.show();
    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) & (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) & (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) ) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, SYSTEM_ALERT_WINDOW}, 100);
        }

        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_nom:
                startActivity(new Intent(getBaseContext(), edtda.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.textViewema:
                startActivity(new Intent(getBaseContext(), edt.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
        }
    }

    public void dele(String tok) {
        Log.e("veer", "entro guardar");
        Log.e("token_cli", tok);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("token_cli", tok);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.DEL_US,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto", "PRECEOSA   ");
                                procesarRespuesta(response);

                                Log.e("puto", "respuetsa -" + response);
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
        Log.e("puto", "ENTRA PROESA   ");
        try {
            // Obtener estado
            String estado = response.getString("estado");
            Log.e("puto", "esatso  -" + estado);
            // Obtener mensaje
            String mensaje = response.getString("msg");
            Log.e("puto", "mensaje -" + mensaje);

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                    DatabaseReference refe = FirebaseDatabase.getInstance().getReference("clientes").child(user.getUid());
                    refe.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseAuth.getInstance().signOut();
                            Intent lo = new Intent(getBaseContext(), Crear_cuenta.class);
                            lo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(lo);
                            finish();
                        }
                    });
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


    BroadcastReceiver broadCastGPS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String gp = intent.getStringExtra("gps");
            if (gp != null) {
                try {
                    gac_service.status.startResolutionForResult(Inicio.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("aviso", "El usuario permitió el cambio de ajustes de ubicación.");
                        //processLastLocation();
                        //startLocationUpdates();
                        if (data.getExtras() != null) {
                            Log.e("datos", "resul " + requestCode + " __ " + resultCode + " __ " + data.getExtras().toString());
                        }
                        //stopService(new Intent(Inicio.this, gac_service.class));
                        //unregisterReceiver(broadCastGPS);
                        break;
                    case Activity.RESULT_CANCELED:

                        navigationView.setCheckedItem(R.id.ini);
                        Log.d("aviso", "El usuario no permitió el cambio de ajustes de ubicación");
                        stopService(new Intent(Inicio.this, gac_service.class));
                        unregisterReceiver(broadCastGPS);
                        displaySelectedScreen(R.id.ini);
                        break;
                }
                break;
        }
    }


    public void pres() {
        final DatabaseReference presenceRefCon = FirebaseDatabase.getInstance().getReference().child("presencia").child("conexion").child("clientes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conectado");
        final DatabaseReference presenceRefTim = FirebaseDatabase.getInstance().getReference().child("presencia").child("tiempo").child("clientes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

// ya que puedo conectarme desde varios dispositivos, almacenamos cada instancia de conexión por separado
// en cualquier momento que el valor de connectionsRef sea nulo (es decir, no tiene hijos) Estoy desconectado
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference("users/joe/connections");

//almacena la marca de tiempo de mi última desconexión (la última vez que fui visto en línea)
        final DatabaseReference lastOnlineRef = database.getReference("/users/joe/lastOnline");

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //DatabaseReference con = presenceRefCon.push();

                    presenceRefCon.setValue(Boolean.TRUE);

// cuando este dispositivo se desconecte, quítalo
                    //con.onDisconnect().removeValue();
                    presenceRefCon.onDisconnect().setValue(Boolean.FALSE);

// cuando me desconecte, actualizar la última vez que me vieron en línea
                    presenceRefTim.child("start").setValue(ServerValue.TIMESTAMP);
                    presenceRefTim.child("end").onDisconnect().setValue(ServerValue.TIMESTAMP);


// agregar este dispositivo a mi lista de conexiones
                    // este valor podría contener información sobre el dispositivo o una marca de tiempo también
                    //con.setValue(Boolean.TRUE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }
}
