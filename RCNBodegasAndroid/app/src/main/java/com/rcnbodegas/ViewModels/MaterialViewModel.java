package com.rcnbodegas.ViewModels;

public class MaterialViewModel {

    private String wareHouseId;
    private String wareHouseName;
    private Integer productionId;
    private String productionName;
    private Integer responsibleId;
    private String responsibleName;
    private String typeElementId;
    private String typeElementName;
    private String marca;
    private String barCode;
    private String materialName;

    public MaterialViewModel(String wareHouseId, String wareHouseName, Integer productionId, String productionName, Integer responsibleId, String responsibleName, String typeElementId, String typeElementName, String marca, String barCode, String materialName) {
        this.wareHouseId = wareHouseId;
        this.wareHouseName = wareHouseName;
        this.productionId = productionId;
        this.productionName = productionName;
        this.responsibleId = responsibleId;
        this.responsibleName = responsibleName;
        this.typeElementId = typeElementId;
        this.typeElementName = typeElementName;
        this.marca = marca;
        this.barCode = barCode;
        this.materialName = materialName;
    }

    public MaterialViewModel() {
    }

    public String getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public Integer getProductionId() {
        return productionId;
    }

    public void setProductionId(Integer productionId) {
        this.productionId = productionId;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public Integer getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(Integer responsibleId) {
        this.responsibleId = responsibleId;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public String getTypeElementId() {
        return typeElementId;
    }

    public void setTypeElementId(String typeElementId) {
        this.typeElementId = typeElementId;
    }

    public String getTypeElementName() {
        return typeElementName;
    }

    public void setTypeElementName(String typeElementName) {
        this.typeElementName = typeElementName;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
