package com.rcnbodegas.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.rcnbodegas.Database.BodegasDatabase;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.List;

public class MaterialRepository {
    private static Context _context = null;
    private String DB_NAME = "bodegas_db";
    private BodegasDatabase bodegasDatabase;

    public MaterialRepository(Context context) {

        bodegasDatabase = Room.databaseBuilder(context, BodegasDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        _context = context;
    }

    public void delete(MaterialViewModel materialViewModel) {

        try {

            bodegasDatabase.materialDao().delete(materialViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void deletebyHeaderId(int docNumber) {

        try {

            bodegasDatabase.materialDao().deleteByHeder(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public LiveData<List<MaterialViewModel>> geDetailByDocumentNumber(int docNumber) {
        LiveData<List<MaterialViewModel>> result = null;

        try {
            result = bodegasDatabase.materialDao().geDetailByDocumentNumber(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public MaterialViewModel getMaterialByBarcode(String barcode) {
        MaterialViewModel result = null;

        try {
            result = bodegasDatabase.materialDao().getMaterialByBarcode(barcode);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public List<MaterialViewModel> getMaterialLegalizationDetail(long docNumber) {
        List<MaterialViewModel> result = null;

        try {
            result = bodegasDatabase.materialDao().getMaterialLegalizationDetail(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public List<MaterialViewModel> getReviewDetail(int docNumber) {
        List<MaterialViewModel> result = null;

        try {
            result = bodegasDatabase.materialDao().getReviewDetail(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public int geAllDetailByDocumentNumber(int docNumber) {
       int result = 0;

        try {
            result = bodegasDatabase.materialDao().geAllDetailByDocumentNumber(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public int getCountReviewDetail(int docNumber) {
        int result = 0;

        try {
            result = bodegasDatabase.materialDao().getCountReviewDetail(docNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }


    public long insert(MaterialViewModel materialViewModel) {

        long id = 0;
        try {

            id = bodegasDatabase.materialDao().insert(materialViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return id;
    }

    public void insertAllOElements(List<MaterialViewModel> materialViewModels) {
        try {

            bodegasDatabase.materialDao().insertAllOElements(materialViewModels);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void update(MaterialViewModel materialViewModel) {

        try {

            bodegasDatabase.materialDao().update(materialViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
