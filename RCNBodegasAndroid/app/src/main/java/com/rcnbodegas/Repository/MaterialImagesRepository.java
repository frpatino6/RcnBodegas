package com.rcnbodegas.Repository;

import android.content.Context;

import androidx.room.Room;

import com.rcnbodegas.Database.BodegasDatabase;
import com.rcnbodegas.ViewModels.MaterialImagesViewModel;

import java.util.List;

public class MaterialImagesRepository {

    private static Context _context = null;
    private String DB_NAME = "bodegas_db";
    private BodegasDatabase bodegasDatabase;

    public MaterialImagesRepository(Context context) {

        bodegasDatabase = Room.databaseBuilder(context, BodegasDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        _context = context;
    }

    public List<MaterialImagesViewModel> getByMaterialDetailId(long id) {
        List<MaterialImagesViewModel> result = null;

        try {
            result = bodegasDatabase.materialImagesDao().getByMaterialDetailId(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public MaterialImagesViewModel getById(long id) {
        MaterialImagesViewModel result = null;

        try {
            result = bodegasDatabase.materialImagesDao().getById(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public long insert(MaterialImagesViewModel materialImagesViewModel) {

        return bodegasDatabase.materialImagesDao().insert(materialImagesViewModel);
    }

    public void update(MaterialImagesViewModel materialImagesViewModel) {
        try {

            bodegasDatabase.materialImagesDao().update(materialImagesViewModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void deleteALl(){
        try {

            bodegasDatabase.materialImagesDao().deleteall();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
