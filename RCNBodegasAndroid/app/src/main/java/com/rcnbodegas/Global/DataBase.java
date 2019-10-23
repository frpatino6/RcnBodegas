package com.rcnbodegas.Global;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DataBase extends SQLiteOpenHelper {

    private static final int VERSION_BASEDATOS = 2;

    // Nombre de nuestro archivo de base de datos
    private static final String NOMBRE_BASEDATOS = "BodegasDb.db";

    private static final String BODEGAS = "CREATE TABLE Bodegas" +
            "(wareHouseName TEXT, wareHouseId TEXT)";

    private static final String RESPONSABLE = "CREATE TABLE Responsable" +
            "(responsibleName TEXT, responsibleId int)";

    private static final String PRODUCCION = "CREATE TABLE Produccion" +
            "(productionName TEXT, productionId int)";

    public DataBase(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
}
