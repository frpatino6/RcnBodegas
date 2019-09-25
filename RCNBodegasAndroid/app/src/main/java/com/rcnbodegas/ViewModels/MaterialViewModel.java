package com.rcnbodegas.ViewModels;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class MaterialViewModel {

    private String saleDate;
    private String wareHouseId;
    private String wareHouseName;
    private Integer productionId;
    private String productionName;
    private Integer responsibleId;
    private Integer terceroActual;
    private String responsibleName;
    private String typeElementId;
    private String typeElementName;
    private String legalizedBy;
    private String marca;
    private String barCode;
    private String materialName;
    private Double unitPrice;
    private Double purchaseValue;
    private ArrayList<Bitmap> listaImagenesBmp;
    private ArrayList<String> listaImagenesStr;

    private boolean isReview;


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

    public Integer getTerceroActual() {
        return terceroActual;
    }

    public void setTerceroActual(Integer terceroActual) {
        this.terceroActual = terceroActual;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getLegalizedBy() {
        return legalizedBy;
    }

    public void setLegalizedBy(String legalizedBy) {
        this.legalizedBy = legalizedBy;
    }

    public Double getPurchaseValue() {
        return purchaseValue;
    }

    public void setPurchaseValue(Double purchaseValue) {
        this.purchaseValue = purchaseValue;
    }

    public ArrayList<String> getListaImagenesStr() {

        if(listaImagenesStr ==null) listaImagenesStr = new ArrayList<>();
        return listaImagenesStr;
    }

    public void setListaImagenesStr(ArrayList<String> listaImagenesStr) {
        this.listaImagenesStr = listaImagenesStr;
    }

    public ArrayList<Bitmap> getListaImagenesBmp() {
        if (listaImagenesBmp == null)
             listaImagenesBmp = new ArrayList<Bitmap>();

        return listaImagenesBmp;
    }

    public void setListaImagenesBmp(ArrayList<Bitmap> listaImagenesBmp) {
        this.listaImagenesBmp = listaImagenesBmp;
    }

    public MaterialViewModel() {
        this.isReview = false;
        listaImagenesBmp=new ArrayList<>();
        listaImagenesStr= new ArrayList<>();
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isReview() {
        return isReview;
    }

    public void setReview(boolean review) {
        isReview = review;
    }

    @Override
    public String toString() {
        return this.barCode.toString();
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
