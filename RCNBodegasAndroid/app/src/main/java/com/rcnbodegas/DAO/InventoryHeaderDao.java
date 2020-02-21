package com.rcnbodegas.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;

import java.util.List;

@Dao
public interface InventoryHeaderDao {

    @Query("SELECT * FROM InventroyHeaderViewModel")
    LiveData<List<InventroyHeaderViewModel>> geHeaderByDocumentNUmber();

    @Query("SELECT * FROM InventroyHeaderViewModel where sincronized= 0")
    InventroyHeaderViewModel getInventoryHeader();

    @Insert
    Long insert(InventroyHeaderViewModel inventroyHeaderViewModel);
}
