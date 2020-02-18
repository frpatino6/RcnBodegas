package com.rcnbodegas.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rcnbodegas.DAO.InventoryHeaderDao;
import com.rcnbodegas.DAO.MaterialDao;
import com.rcnbodegas.ViewModels.InventoryDetailViewModel;
import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;

@Database(entities =
        {MaterialViewModel.class },
        version = 1, exportSchema = false)
public abstract class BodegasDatabase extends RoomDatabase {
    public abstract MaterialDao materialDao();


}
