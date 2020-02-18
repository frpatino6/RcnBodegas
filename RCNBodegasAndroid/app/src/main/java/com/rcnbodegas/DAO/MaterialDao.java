package com.rcnbodegas.DAO;

import android.arch.lifecycle.LiveData;

import androidx.room.Insert;
import androidx.room.Query;

import com.rcnbodegas.ViewModels.InventoryDetailViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.List;

public interface MaterialDao {

    @Insert
    Long insertMaterial(MaterialViewModel materialViewModel);

    @Query("SELECT * FROM MaterialViewModel where idDetail = :headerId")
    LiveData<List<MaterialViewModel>> geDetailByDocumentNumber(int headerId);
}
