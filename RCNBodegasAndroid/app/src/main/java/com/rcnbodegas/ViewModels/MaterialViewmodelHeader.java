package com.rcnbodegas.ViewModels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MaterialViewmodelHeader {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int productionId;
    private int responsibleId;
    private String selectedProductionName;
    private int status; //1) nuevo 2) en proceso 3)finalizado
    private int userserWarehouse;
    private String warehouseId;

    public MaterialViewmodelHeader(String warehouseId, int productionId, int responsibleId, int userserWarehouse, String selectedProductionName) {

        this.status = 1; //1 por defecto
        this.warehouseId = warehouseId;
        this.productionId = productionId;
        this.responsibleId = responsibleId;
        this.userserWarehouse = userserWarehouse;
        this.selectedProductionName = selectedProductionName;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public String getSelectedProductionName() {
        return selectedProductionName;
    }

    public void setSelectedProductionName(String selectedProductionName) {
        this.selectedProductionName = selectedProductionName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserserWarehouse() {
        return userserWarehouse;
    }

    public void setUserserWarehouse(int userserWarehouse) {
        this.userserWarehouse = userserWarehouse;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}
