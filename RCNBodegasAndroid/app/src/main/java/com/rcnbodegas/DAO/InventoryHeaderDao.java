package com.rcnbodegas.DAO;

import android.arch.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;

import java.util.List;

@Dao
public interface InventoryHeaderDao {

    @Insert
    Long insertCustomer(InventroyHeaderViewModel inventroyHeaderViewModel);

    @Query("SELECT * FROM InventroyHeaderViewModel")
    LiveData<List<InventroyHeaderViewModel>> geHeaderByDocumentNUmber();
}
