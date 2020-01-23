package com.rcnbodegas.ViewModels;

public class InventroyHeaderViewModel {

    private int companyId;
    private int id;
    private int productionId;
    private int responsibleId;
    private String warehouseTypeId;
    private int state;
    private String initDate;
    private String endDate;
    private String inventoryUser;
    private String productionName;

    public InventroyHeaderViewModel() {
    }


    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getInventoryUser() {
        return inventoryUser;
    }

    public void setInventoryUser(String inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public String getWarehouseTypeId() {
        return warehouseTypeId;
    }

    public void setWarehouseTypeId(String warehouseTypeId) {
        this.warehouseTypeId = warehouseTypeId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
