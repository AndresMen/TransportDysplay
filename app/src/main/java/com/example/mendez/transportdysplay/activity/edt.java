package com.example.mendez.transportdysplay.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class edt extends AppCompatActivity implements View.OnClickListener {

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

    EditText nume,codi;
    RelativeLayout rlnume,rlco;

    RelativeLayout ac,ver,ree;
    LinearLayout llbc;
    ProgressDialog progressDialog;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    TextView edt,veri,reen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edt2);
        setTitle("Editar numero");
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
        nume=(EditText) findViewById(R.id.ednu);
        nume.setText(settings.getString("num","error"));
        codi=(EditText) findViewById(R.id.edcodi);
        rlnume=(RelativeLayout) findViewById(R.id.rlnu);
        rlco=(RelativeLayout)findViewById(R.id.rlco);
        llbc=(LinearLayout)findViewById(R.id.vr);
        ac=(RelativeLayout)findViewById(R.id.layout_signup);
        ver=(RelativeLayout)findViewById(R.id.layout_ver);
        ree=(RelativeLayout)findViewById(R.id.layout_ree);
        edt=(TextView)findViewById(R.id.tv_edt);
        veri=(TextView)findViewById(R.id.tv_ver);
        reen=(TextView)findViewById(R.id.tv_ree);
        edt.setOnClickListener(this);
        veri.setOnClickListener(this);
        reen.setOnClickListener(this);
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
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final FirebaseUser  user=mAuth.getCurrentUser();
        user.updatePhoneNumber(credential).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateUI(STATE_SIGNIN_SUCCESS, user);
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        codi.setError("Invalid code.");
                        // [END_EXCLUDE]
                    }
                    updateUI(STATE_SIGNIN_FAILED);
                }
            }
        });
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
        FirebaseUser  user=mAuth.getCurrentUser();
        user.updatePhoneNumber(credential);
        //signInWithPhoneAuthCredential(credential);
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

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);

        //verificacion instantanea
    }

    private void updateUI(int uiState, final FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Estado inicializado, mostrar solo el campo de número de teléfono y el botón de inicio

                // log.setVisibility(View.VISIBLE);

                //lt.setVisibility(View.GONE);
                rlnume.setVisibility(View.VISIBLE);
                rlco.setVisibility(View.GONE);

                ac.setVisibility(View.VISIBLE);
                llbc.setVisibility(View.GONE);

                break;
            case STATE_CODE_SENT:
                // Código enviado estado, muestra el campo de verificación, el
                rlnume.setVisibility(View.GONE);
                rlco.setVisibility(View.VISIBLE);

                ac.setVisibility(View.GONE);
                llbc.setVisibility(View.VISIBLE);
                // lt.setVisibility(View.VISIBLE);

                // log.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(),"código de estado enviado",Toast.LENGTH_SHORT).show();

                break;
            case STATE_VERIFY_FAILED:
                //La verificación ha fallado, muestra todas las opciones
                // log.setVisibility(View.VISIBLE);
                //lt.setVisibility(View.GONE);
                rlnume.setVisibility(View.VISIBLE);
                rlco.setVisibility(View.GONE);
                ac.setVisibility(View.VISIBLE);
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

                rlnume.setVisibility(View.GONE);
                rlco.setVisibility(View.GONE);
                ac.setVisibility(View.GONE);
                llbc.setVisibility(View.GONE);
               // gu.setVisibility(View.VISIBLE);

                Log.e("Usuarios","email: "+user.getEmail());

                final FirebaseUser usua=mAuth.getCurrentUser();
                progressDialog = new ProgressDialog(edt.this);
                progressDialog.setMessage("Subiendo..");
                progressDialog.show();

                editar(usua.getUid(),null,nume.getText().toString());

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edt:
                if (!validatePhoneNumber()) {
                    return;
                }

                startPhoneNumberVerification("+591"+nume.getText().toString());
                break;
            case R.id.tv_ver:

                String code = codi.getText().toString();
                Log.e("codde",code);
                if (TextUtils.isEmpty(code)) {
                    codi.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.tv_ree:
                resendVerificationCode("+591"+nume.getText().toString(), mResendToken);
                break;
        }

    }
    private boolean validatePhoneNumber() {
        String phoneNumber = nume.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            nume.setError("Numero de telefono invalido");
            return false;
        }
        return true;
    }
    public void editar(String tok,String nomb,String num_cel) {
        Log.e("veer","entro guardar");


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
                    FirebaseUser user=mAuth.getCurrentUser();
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("clientes").child(user.getUid());
                    HashMap<String,String>mapa=new HashMap<>();
                    editor = settings.edit();
                    editor.putString("num",nume.getText().toString());
                    editor.apply();
                    mapa.put("nombre",settings.getString("nom","error"));
                    mapa.put("numero",nume.getText().toString());
                    reference.setValue(mapa);
                    finish();
                    //startActivity(new Intent(getBaseContext(),Inicio.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    //finish();
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
