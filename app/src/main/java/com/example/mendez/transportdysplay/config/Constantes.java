package com.example.mendez.transportdysplay.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Constantes {

    private static final String IP ="http://taxi.xorxio.com/phptracker/";
    public static final String IN_CLI=IP+"insertar_cliente.php";
    public static final String IN_VI=IP+"insertar_viaje.php";
    public static final String VI_GET=IP+"obtener_viajes_cli.php";
    public static final String UP_CLI=IP+"actualizar_cliente.php";

    public static final String OBT_US=IP+"obtener_usuario.php";

    public static final String DEL_US=IP+"del_clifi";


    public static  final String IMG_US=IP+"users";
    public static  final String IMG_AU=IP+"autos";


    public static final String DIR=IP+"get_direc.php";

    public static final String ENV_NOT=IP+"enviarnot.php";

    public static final String LOG=IP+"login_cli.php";

    //public static final String VIV=IP+"obtener_estvia.php";

    public static final String PREFS_NAME="display";


    public static  final String UP_VI_ST=IP+"actualizar_viaje_star.php";

    public static void termi(Context context){

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor;
        editor = settings.edit();
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
        editor.putString("verecy", "error");
        editor.putString("ped","error");
        editor.apply();
    }
}
