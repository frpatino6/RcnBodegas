package com.rcnbodegas.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.rcnbodegas.ViewModels.MaterialImagesViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.List;

@Dao
public interface MaterialImagesDao {

    @Delete
    void delete(MaterialViewModel materialViewModel);


    @Query("DELETE FROM MaterialImagesViewModel")
    public void deleteall();


    @Query("SELECT * FROM MaterialImagesViewModel where materialViewmodelId = :id")
    List<MaterialImagesViewModel> getByMaterialDetailId(long id);


    @Query("SELECT * FROM MaterialImagesViewModel where id = :id")
    MaterialImagesViewModel getById(long id);

    @Insert
    Long insert(MaterialImagesViewModel materialViewModel);

    @Update
    void update(MaterialImagesViewModel materialViewModel);
}
