package com.example.mendez.transportdysplay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mendez on 13/06/2018.
 */

public class BD extends SQLiteOpenHelper {
    String consulta="CREATE TABLE sms (sms TEXT, fecha TEXT, hora TEXT,  nombre TEXT, placa TEXT,tipo TEXT)";
    String consulta2="CREATE TABLE bus (calle TEXT,lat TEXT,lng TEXT)";
    public BD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(consulta);
        db.execSQL(consulta2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //solo desarrollo
        db.execSQL("DROP TABLE IF EXISTS sms");
        db.execSQL("DROP TABLE IF EXISTS bus");
        //Se crea la nueva versión de la tabla
        onConfigure(db);
        //solo dearrolllo
    }
}
