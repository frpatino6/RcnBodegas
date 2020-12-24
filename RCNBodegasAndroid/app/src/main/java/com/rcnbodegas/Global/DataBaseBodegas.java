package com.rcnbodegas.Global;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DataBaseBodegas extends SQLiteOpenHelper {

    private static final int VERSION_BASEDATOS = 2;

    // Nombre de nuestro archivo de base de datos
    private static final String NOMBRE_BASEDATOS = "bodegas_db";

    private static final String BODEGAS = "CREATE TABLE Bodegas" +
            "(wareHouseName TEXT, wareHouseId TEXT)";

    private static final String RESPONSABLE = "CREATE TABLE Responsable" +
            "(responsibleName TEXT, responsibleId int)";

    private static final String PRODUCCION = "CREATE TABLE Produccion" +
            "(productionName TEXT, productionId int)";

    private static final String MATERIAL = "CREATE TABLE Material" +
            "(DESCRIPCION TEXT, TIPO_ELEMENTO TEXT ,MARCA TEXT,CODIGO_BARRAS TEXT,VALOR_COMPRA TEXT,CODIGO Int, PRODUCCION Int)";

    public DataBaseBodegas(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MATERIAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Material");
    }
    public void doesDatabaseExist(Context context) {
        File database = context.getDatabasePath("BodegasDb.db");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "Not Found");
        } else {
            Log.i("Database", "Found");
        }
    }
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}
