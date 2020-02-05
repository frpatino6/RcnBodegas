package com.rcnbodegas.ViewModels;

public class InventroyHeaderViewModel {

    private int companyId;
    private String endDate;
    private int id;
    private String initDate;
    private String inventoryUser;
    private int productionId;
    private String productionName;
    private int responsibleId;
    private int state;
    private int typeELement;
    private String fechaMovimiento;
    private String warehouseTypeId;

    public InventroyHeaderViewModel() {
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    public String getInventoryUser() {
        return inventoryUser;
    }

    public void setInventoryUser(String inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTypeELement() {
        return typeELement;
    }

    public void setTypeELement(int typeELement) {
        this.typeELement = typeELement;
    }

    public String getWarehouseTypeId() {
        return warehouseTypeId;
    }

    public void setWarehouseTypeId(String warehouseTypeId) {
        this.warehouseTypeId = warehouseTypeId;
    }
}
