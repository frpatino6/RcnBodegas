package com.rcnbodegas.Repository;

import android.content.Context;

import androidx.room.Room;

import com.rcnbodegas.Database.BodegasDatabase;
import com.rcnbodegas.ViewModels.MaterialViewModel;

public class MaterialRepository {
    private String DB_NAME = "bodegas_db";
    private static Context _context = null;
    private BodegasDatabase bodegasDatabase;

    public MaterialRepository(Context context) {

        bodegasDatabase = Room.databaseBuilder(context, BodegasDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        _context = context;
    }

    public void insert(MaterialViewModel materialViewModel) {

        try {

            bodegasDatabase.materialDao().insertMaterial(materialViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
