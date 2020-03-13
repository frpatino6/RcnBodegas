package com.rcnbodegas.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rcnbodegas.DAO.InventoryHeaderDao;
import com.rcnbodegas.DAO.MaterialDao;
import com.rcnbodegas.DAO.MaterialHeaderDAO;
import com.rcnbodegas.DAO.MaterialImagesDao;
import com.rcnbodegas.ViewModels.InventoryDetailViewModel;
import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;
import com.rcnbodegas.ViewModels.MaterialImagesViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.MaterialViewmodelHeader;

@Database(entities =
        {MaterialViewModel.class, InventroyHeaderViewModel.class , MaterialViewmodelHeader.class, MaterialImagesViewModel.class},
        version = 2, exportSchema = false)
public abstract class BodegasDatabase extends RoomDatabase {
    public abstract MaterialDao materialDao();
    public abstract InventoryHeaderDao inventoryHeaderDao();
    public abstract MaterialHeaderDAO materialHeaderDAO();
    public abstract MaterialImagesDao materialImagesDao();



}
