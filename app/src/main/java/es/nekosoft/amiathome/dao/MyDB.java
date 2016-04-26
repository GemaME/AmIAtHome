package es.nekosoft.amiathome.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.nekosoft.amiathome.R;


public class MyDB extends SQLiteOpenHelper {

    //---- Atributes ----//
    public static Context context;
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "organizer.db";


    //---- Methods ----//
    public MyDB(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        MyDB.context = context;
    }

    public MyDB(Context context, String dataBaseName) { //+

        super(context, dataBaseName, null, DATABASE_VERSION);
        MyDB.context = context;
    }


    //Solo se ejecuta cuando la BBDD se crea por primera vez
    public void onCreate(SQLiteDatabase db) {

        //Obtener sentencias para el esquema y los inserts
        List<String> schema = readRawDB(R.raw.db_scheme, context);
        List<String> inserts = readRawDB(R.raw.db_insert, context);

        //Unir listas
        schema.addAll(inserts);

        //Ejecutar las sentencias
        for(int i=0 ; i<schema.size() ; i++)
            db.execSQL(schema.get(i));

    }

    //Para actualizar o desactualizar la BBDD. Esto es util cuando se trabaja con persistencia externa
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        List<String> drop = readRawDB(R.raw.db_drop_tables, context);
        for(int i=0 ; i<drop.size() ; i++)
            db.execSQL(drop.get(i));

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);
    }

    protected List<String> readRawDB(int idRaw, Context context){

        //Obtener canales
        InputStream is = context.getResources().openRawResource(idRaw);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Scanner sc = new Scanner(isr);
        sc.useDelimiter(";");

        //Leer lineas
        String line;
        List<String> list = new ArrayList<String>();

        while (sc.hasNext()){

            list.add(sc.next().trim());
        }

        sc.close();

        //Devolver resultado
        return list;
    }

}
