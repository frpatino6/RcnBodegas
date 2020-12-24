package com.rcnbodegas.Repository;

import android.content.Context;

import androidx.room.Room;

import com.rcnbodegas.Database.BodegasDatabase;
import com.rcnbodegas.ViewModels.MaterialViewmodelHeader;

public class MaterialHeaderRepository {

    private static Context _context = null;
    private String DB_NAME = "bodegas_db";
    private BodegasDatabase bodegasDatabase;

    public MaterialHeaderRepository(Context context) {

        bodegasDatabase = Room.databaseBuilder(context, BodegasDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        _context = context;
    }

    public MaterialViewmodelHeader getLegalizationPendingProcess() {
        MaterialViewmodelHeader result = null;

        try {
            result = bodegasDatabase.materialHeaderDAO().getLegalizationPendingProcess();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public MaterialViewmodelHeader getLegalizationById(long id) {
        MaterialViewmodelHeader result = null;

        try {
            result = bodegasDatabase.materialHeaderDAO().getLegalizationById(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public long insert(MaterialViewmodelHeader materialViewmodelHeader) {

        return bodegasDatabase.materialHeaderDAO().insert(materialViewmodelHeader);
    }

    public long delete(MaterialViewmodelHeader materialViewmodelHeader) {

        return bodegasDatabase.materialHeaderDAO().insert(materialViewmodelHeader);
    }

    public void update(MaterialViewmodelHeader materialViewmodelHeader) {
        try {

            bodegasDatabase.materialHeaderDAO().update(materialViewmodelHeader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
