package com.rcnbodegas.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rcnbodegas.ViewModels.MaterialViewmodelHeader;

@Dao
public interface MaterialHeaderDAO {

    @Query("SELECT * FROM MaterialViewmodelHeader where status=1")
    MaterialViewmodelHeader  getLegalizationPendingProcess();

    @Query("SELECT * FROM MaterialViewmodelHeader where id=:id")
    MaterialViewmodelHeader  getLegalizationById(long id);


    @Insert
    Long insert(MaterialViewmodelHeader materialViewModel);

    @Update
    void update(MaterialViewmodelHeader materialViewModel);

    @Delete
    void delete(MaterialViewmodelHeader materialViewModel);
}
