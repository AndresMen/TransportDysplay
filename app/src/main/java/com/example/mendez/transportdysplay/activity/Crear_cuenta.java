package com.example.mendez.transportdysplay.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Crear_cuenta extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PhoneAuthActivity";

    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    EditText nume,codi,nom;
    RelativeLayout rlnume,rlcodi,rlnom;

    RelativeLayout ac,ver,ree,gu;
    LinearLayout llbc;
    ProgressDialog loading = null;
    SharedPreferences  settings;
    SharedPreferences.Editor editor;

    TextView txsig,txver,txree,txgua;

    Animation mAnimationenter,mAnimationexit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta3);

        mAuth = FirebaseAuth.getInstance();
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
        nume=(EditText) findViewById(R.id.ednu);
        codi=(EditText) findViewById(R.id.edcodi);
        nom=(EditText) findViewById(R.id.edit_nom);

        rlnume=(RelativeLayout) findViewById(R.id.rlnu);
        rlcodi=(RelativeLayout)findViewById(R.id.rlco);
        rlnom=(RelativeLayout) findViewById(R.id.rlnm);

        llbc=(LinearLayout)findViewById(R.id.vr);
        ac=(RelativeLayout)findViewById(R.id.layout_signup);
        ver=(RelativeLayout)findViewById(R.id.layout_ver);
        ree=(RelativeLayout)findViewById(R.id.layout_ree);
        gu=(RelativeLayout)findViewById(R.id.layout_gua);
        txsig=(TextView)findViewById(R.id.txt_signup);
        txver=(TextView)findViewById(R.id.tv_ver) ;
        txree=(TextView)findViewById(R.id.tv_ree);
        txgua=(TextView)findViewById(R.id.txt_gu);
        txsig.setOnClickListener(this);
        txver.setOnClickListener(this);
        txree.setOnClickListener(this);
        txgua.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Esta devolución de llamada se invocará en dos situaciones:
                // 1 - Verificación instantánea. En algunos casos, el número de teléfono puede ser instantáneamente
                // verificado sin necesidad de enviar o ingresar un código de verificación.
                // 2 - Recuperación automática. En algunos dispositivos, los servicios de Google Play pueden automáticamente
                // detectar el SMS de verificación entrante y realizar la verificación sin acción del usuario.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Actualice la interfaz de usuario e intente iniciar sesión con la credencial del teléfono
                updateUI(STATE_VERIFY_SUCCESS, credential);

                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Esta devolución de llamada se invoca en una solicitud no válida para la verificación,
                // por ejemplo, si el formato del número de teléfono no es válido.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Solicitud no válida
                    // [START_EXCLUDE]
                    nume.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    //La cuota de SMS para el proyecto ha sido excedida
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Mostrar un mensaje y actualizar la interfaz de usuario
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // El código de verificación de SMS se ha enviado al número de teléfono proporcionado,
                // ahora necesitamos pedirle al usuario que ingrese el código y luego construir una credencial
                // combinando el código con una identificación de verificación.
                Log.d(TAG, "onCodeSent:" + verificationId);




                // Guardar ID de verificación y token de reenvío para que podamos usarlos más tarde
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            //datos();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                codi.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user,null);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);

        //verificacion instantanea
    }
    public void anima(View view,boolean x){
        if (x){
            mAnimationenter = AnimationUtils.loadAnimation(this, R.anim.scale_enter);
            view.setVisibility(View.VISIBLE);
        }else{
            mAnimationexit = AnimationUtils.loadAnimation(this, R.anim.scale_exit);
            view.setVisibility(View.GONE);
        }

    }
    private void updateUI(int uiState, final FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Estado inicializado, mostrar solo el campo de número de teléfono y el botón de inicio

                // log.setVisibility(View.VISIBLE);

                //lt.setVisibility(View.GONE);
                anima(rlnume,true);
                rlnume.setVisibility(View.VISIBLE);
                anima(rlcodi,false);
                rlcodi.setVisibility(View.GONE);
                anima(rlnom,false);
                rlnom.setVisibility(View.GONE);

                anima(ac,true);
                ac.setVisibility(View.VISIBLE);
                anima(llbc,false);
                llbc.setVisibility(View.GONE);

                break;
            case STATE_CODE_SENT:
                // Código enviado estado, muestra el campo de verificación, el
                anima(rlnume,false);
                rlnume.setVisibility(View.GONE);
                anima(rlcodi,true);
                rlcodi.setVisibility(View.VISIBLE);
                anima(rlnom,false);
                rlnom.setVisibility(View.GONE);

                anima(ac,false);
                ac.setVisibility(View.GONE);
                anima(llbc,true);
                llbc.setVisibility(View.VISIBLE);
                // lt.setVisibility(View.VISIBLE);

                // log.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(),"código de estado enviado",Toast.LENGTH_SHORT).show();

                break;
            case STATE_VERIFY_FAILED:
                //La verificación ha fallado, muestra todas las opciones
                // log.setVisibility(View.VISIBLE);
                //lt.setVisibility(View.GONE);

                anima(rlnume,true);
                rlnume.setVisibility(View.VISIBLE);
                anima(rlcodi,false);
                rlcodi.setVisibility(View.GONE);
                anima(rlnom,false);
                rlnom.setVisibility(View.GONE);
                anima(ac,true);
                ac.setVisibility(View.VISIBLE);
                anima(llbc,false);
                llbc.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(),"verificacion de estado fallido",Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in

                Toast.makeText(getBaseContext(),"verificación de estado exitosa",Toast.LENGTH_SHORT).show();
                //Establezca el texto de verificación basado en la credencial
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        codi.setText(cred.getSmsCode());
                    } else {
                        codi.setText("validación instantánea");
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                //No-operativo, manejado por cheque de inicio de sesión
                Toast.makeText(getBaseContext(),"estado de inicio de sesión fallido",Toast.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                //No-operativo,manejado por cheque de inicio de sesión
                dato(user);
                break;
        }
    }

    public void dato(final FirebaseUser user){
        final AlertDialog loa= ProgressDialog.show(Crear_cuenta.this,"Cargando...","Espere por favor...",false,false);

        DatabaseReference  reference=FirebaseDatabase.getInstance().getReference().child("clientes").child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.e("datosss",dataSnapshot.getValue().toString());
                    editor=settings.edit();
                    editor.putString("nom",dataSnapshot.child("nombre").getValue().toString());
                    editor.putString("num",dataSnapshot.child("numero").getValue().toString());
                    editor.apply();
                    loa.dismiss();
                    //control(user);
                    startActivity(new Intent(getBaseContext(),Inicio.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else{
                    loa.dismiss();
                    anima(rlnume,false);
                    rlnume.setVisibility(View.GONE);
                    anima(rlcodi,false);
                    rlcodi.setVisibility(View.GONE);
                    anima(rlnom,true);
                    rlnom.setVisibility(View.VISIBLE);
                    anima(ac,false);
                    ac.setVisibility(View.GONE);
                    anima(llbc,false);
                    llbc.setVisibility(View.GONE);
                    anima(gu,true);
                    gu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errorrr",databaseError.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_signup:
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();

                if (!validatePhoneNumber()) {
                    return;
                }

                startPhoneNumberVerification("+591"+nume.getText().toString());
                break;
            case R.id.tv_ver:
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
                String code = codi.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    codi.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.tv_ree:
                resendVerificationCode("+591"+nume.getText().toString(), mResendToken);
                break;

            case R.id.txt_gu:

                if (!validateForm()){
                    return;
                }

                final FirebaseUser usua=mAuth.getCurrentUser();

                loading= ProgressDialog.show(Crear_cuenta.this,"Subiendo...","Espere por favor...",false,false);

                guardar(usua.getUid(),nom.getText().toString(),nume.getText().toString(),FirebaseInstanceId.getInstance().getToken());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification("+591"+nume.getText().toString());
        }
        // [END_EXCLUDE]
    }
    private boolean validatePhoneNumber() {
        String phoneNumber = nume.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            nume.setError("Numero de telefono invalido");
            return false;
        }
        return true;
    }
    private boolean validateForm(){
        String nomb=nom.getText().toString();
        if (TextUtils.isEmpty(nomb)){
            nom.setError("Nombre invalido");
            return false;
        }

    return true;
    }

    public void guardar(String tok,String nomb,String num_cel,String tfcm) {
        Log.e("veer","entro guardar");
        Log.e("token",tok);
        Log.e("nombre", nomb);
        Log.e("num_cel", num_cel);
        Log.e("tfcm",tfcm);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("token",tok);
        map.put("nombre", nomb);
        map.put("num_cel", num_cel);
        map.put("tok_fcm",tfcm);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.IN_CLI,
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
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
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

                    loading.dismiss();
                    control(user);
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("clientes").child(user.getUid());
                    HashMap<String,String>mapa=new HashMap<>();
                    mapa.put("nombre",nom.getText().toString());
                    mapa.put("numero", nume.getText().toString());
                    reference.setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                editor = settings.edit();
                                editor.putString("nom",nom.getText().toString());
                                editor.putString("num",nume.getText().toString());
                                editor.apply();
                                //control(user);
                                startActivity(new Intent(getBaseContext(),Inicio.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        }
                    });

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    loading.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    editor = settings.edit();
                    editor.putString("nom","error");
                    editor.putString("num","error");
                    editor.apply();
                    Intent lo = new Intent(getBaseContext(), Crear_cuenta.class);
                    lo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lo);
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void control(final FirebaseUser user){

        String path = getString(R.string.firebase_path) + "/" + user.getUid();
        DatabaseReference refgetcon=FirebaseDatabase.getInstance().getReference().child(path+"/control");
        refgetcon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataChildren:dataSnapshot.getChildren()){
                        setc(dataChildren,user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",databaseError.toString());
            }
        });

    }
    public void setc(DataSnapshot dataSnapshot,FirebaseUser us){
        String path = getString(R.string.firebase_path) + "/" + us.getUid();
        DatabaseReference refcon=FirebaseDatabase.getInstance().getReference().child(path+"/control").child(dataSnapshot.getKey());
        HashMap<String,Boolean> mapcon = new HashMap<String, Boolean>();// Mapeo previo
        if (dataSnapshot.getKey().equals(FirebaseInstanceId.getInstance().getToken())){
            mapcon.put("estado",true);
            refcon.setValue(mapcon);
            startActivity(new Intent(getBaseContext(),Inicio.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }else{
            mapcon.put("estado",false);
            refcon.setValue(mapcon);
        }
    }

    public void editar(String tku, String tkfcm, final String nom , final String num) {
        Log.e("veer","entro guardar");
        Log.e("token_usu",tku);
        Log.e("correo",tkfcm);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("token_usu",tku);
        map.put("tokenfcm", tkfcm);


        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.LOG,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuestaed(response,nom,num);

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
    private void procesarRespuestaed(JSONObject response,String nom,String num) {
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
                    editor=settings.edit();
                    editor.putString("nom",nom);
                    editor.putString("num",num);
                    editor.apply();
                        startActivity(new Intent(getBaseContext(),Inicio.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    loading.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    editor = settings.edit();
                    editor.putString("nom","error");
                    editor.putString("num","error");
                    editor.apply();
                    Intent lo = new Intent(getBaseContext(), Crear_cuenta.class);
                    lo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lo);
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
