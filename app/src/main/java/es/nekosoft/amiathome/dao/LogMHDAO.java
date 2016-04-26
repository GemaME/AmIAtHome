package es.nekosoft.amiathome.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.nekosoft.amiathome.model.LogMH;
import es.nekosoft.amiathome.utils.Constants;


public class LogMHDAO {

    //---- Atributes ---//

    public final static String TABLE_NAME = "log";
    public final static String COL_ID = "_id";
    public final static String COL_ICON = "type";
    public final static String COL_TITLE = "title";
    public final static String COL_DATE = "date";
    public final static String ORDER_READ = "DESC";
    protected MyDB mydb;


    //---- Dependency Inyector ----//

    protected MyDB getMydb(Context c){

        return new MyDB(c);
    }

    //---- Constructor ---//

    public LogMHDAO(Context c){

        mydb = getMydb(c); //+
    }


    //---- Methods ----//

    public List<LogMH> readAll(){

        List<LogMH> list = null;

        SQLiteDatabase db = mydb.getReadableDatabase();

        //Columns
        String[] fields = {
                COL_ID,
                COL_ICON,
                COL_TITLE,
                COL_DATE
        };

        //Do the query
        Cursor c = db.query(
                TABLE_NAME,                 // Nombre de la tabla
                fields,                     // Columna a devolver
                null,                       // Where clause
                null,                       // PlaceHolder where
                null,                       // Group by clause
                null,                       // Having clause
                COL_ID + " " + ORDER_READ,  // Order by clause
                Constants.LOG_MAX_ENTRIES   // Limit
        );

        //Get results
        LogMH obj = null;
        if(c.moveToFirst()){

            list = new ArrayList<LogMH>();

            do {
                obj = new LogMH();
                obj.setId(c.getInt(c.getColumnIndex(COL_ID)));
                obj.setType(c.getInt(c.getColumnIndex(COL_ICON)));
                obj.setTitle(c.getString(c.getColumnIndex(COL_TITLE)));

                Date date = null;
                try {
                    String dateStr = c.getString(c.getColumnIndex(COL_DATE));
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    date = format.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = new Date();
                }
                obj.setDate(date);

                list.add(obj);

            }while(c.moveToNext());
        }

        return list;
    }

    public long insert(LogMH obj){

        long idInsert = 0;
        SQLiteDatabase db = mydb.getWritableDatabase();

        //Insert obj
        ContentValues values = new ContentValues();
        values.put(COL_ICON, obj.getType());
        values.put(COL_TITLE, obj.getTitle());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = format.format(obj.getDate());
        values.put(COL_DATE, strDate);

        idInsert = db.insert(
                TABLE_NAME,
                null,
                values
        );

        return idInsert;
    }

    public long deleteAll(){

        long deleted = 0;
        SQLiteDatabase db = mydb.getWritableDatabase();
        int count = db.delete(TABLE_NAME, null, null);

        return deleted;
    }
}
