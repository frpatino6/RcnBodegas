package com.rcnbodegas.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.List;

@Dao
public interface MaterialDao {

    @Delete
    void delete(MaterialViewModel materialViewModel);

    @Query("DELETE FROM MaterialViewModel where idHeader = :headerId")
    void deleteByHeder(int headerId);

    @Query("SELECT * FROM MaterialViewModel where idHeader = :headerId and isReview=0")
    LiveData<List<MaterialViewModel>> geDetailByDocumentNumber(int headerId);

    @Query("SELECT * FROM MaterialViewModel where idHeader = :headerId and isReview=1")
    List<MaterialViewModel> getReviewDetail(int headerId);


    @Query("SELECT * FROM MaterialViewModel where barCode = :barcode")
    MaterialViewModel getMaterialByBarcode(String barcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllOElements(List<MaterialViewModel> materialViewModels);

    @Insert
    Long insert(MaterialViewModel materialViewModel);

    @Update
    void update(MaterialViewModel materialViewModel);
}
