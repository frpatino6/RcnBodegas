package com.rcnbodegas.ViewModels;

public class ProductionViewModel {
    private String id;
    private String  nameWareHouseType;
    private Integer productionCode;
    private String productionName;
    private String internalOrder;

    public ProductionViewModel(String id, String nameWareHouseType, Integer productionCode, String productionName, String internalOrder) {
        this.id = id;
        this.nameWareHouseType = nameWareHouseType;
        this.productionCode = productionCode;
        this.productionName = productionName;
        this.internalOrder = internalOrder;
    }

    public ProductionViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameWareHouseType() {
        return nameWareHouseType;
    }

    public void setNameWareHouseType(String nameWareHouseType) {
        this.nameWareHouseType = nameWareHouseType;
    }

    public Integer getProductionCode() {
        return productionCode;
    }

    public void setProductionCode(Integer productionCode) {
        this.productionCode = productionCode;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getInternalOrder() {
        return internalOrder;
    }

    public void setInternalOrder(String internalOrder) {
        this.internalOrder = internalOrder;
    }
}
