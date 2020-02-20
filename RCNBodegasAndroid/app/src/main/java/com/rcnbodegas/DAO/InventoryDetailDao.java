package com.rcnbodegas.DAO;

import androidx.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rcnbodegas.ViewModels.InventoryDetailViewModel;

import java.util.List;

@Dao
public interface InventoryDetailDao {

    @Insert
    Long insertCustomer(InventoryDetailViewModel inventroyDetailViewModel);

    @Query("SELECT * FROM InventoryDetailViewModel")
    LiveData<List<InventoryDetailViewModel>> geDetailByDocumentNUmber();
}
