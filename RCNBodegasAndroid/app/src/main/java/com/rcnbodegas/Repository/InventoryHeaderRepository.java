package com.rcnbodegas.Repository;

import android.content.Context;

import androidx.room.Room;

import com.rcnbodegas.Database.BodegasDatabase;
import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;

public class InventoryHeaderRepository {
    private static Context _context = null;
    private String DB_NAME = "bodegas_db";
    private BodegasDatabase bodegasDatabase;

    public InventoryHeaderRepository(Context context) {

        bodegasDatabase = Room.databaseBuilder(context, BodegasDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        _context = context;

    }

    public InventroyHeaderViewModel getInventoryHeader() {
        InventroyHeaderViewModel result;

        result = bodegasDatabase.inventoryHeaderDao().getInventoryHeader();

        return result;
    }

    public void insert(InventroyHeaderViewModel inventroyHeaderViewModel) {

        try {

            bodegasDatabase.inventoryHeaderDao().insert(inventroyHeaderViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
