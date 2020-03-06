package com.rcnbodegas.ViewModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MaterialImagesViewModel {

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image;
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long materialViewmodelId;
    private String parsePhoto;

    public String getParsePhoto() {
        return parsePhoto;
    }

    public void setParsePhoto(String parsePhoto) {
        this.parsePhoto = parsePhoto;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMaterialViewmodelId() {
        return materialViewmodelId;
    }

    public void setMaterialViewmodelId(int materialViewmodelId) {
        this.materialViewmodelId = materialViewmodelId;
    }

    public MaterialImagesViewModel(byte[] image, long materialViewmodelId, String parsePhoto) {
        this.image = image;
        this.materialViewmodelId = materialViewmodelId;
        this.parsePhoto= parsePhoto;
    }
}
