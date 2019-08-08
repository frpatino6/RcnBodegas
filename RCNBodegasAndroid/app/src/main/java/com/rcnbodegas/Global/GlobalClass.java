package com.rcnbodegas.Global;

import android.app.Application;

import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.ArrayList;

public class GlobalClass extends Application {

    private int idSelectedResponsibleInventory;
    private int idSelectedTypeElementInventory;
    private int idSelectedResponsibleWarehouse;
    private int idSelectedTypeElementWarehouse;
    private String userName;
    //private String urlServices = "http://172.20.0.154:8083/";
    private String urlServices = "http://192.168.0.10/bodegas/";
    private Integer idSelectedCompanyInventory;
    private String idSelectedWareHouseInventory;
    private String idSelectedProductionInventory;
    private Boolean queryByInventory = false;
    private Boolean isCurrentInventoryActiveProcess = false;
    private Boolean isCurrentAddElementActiveProcess = false;
    private Integer idSelectedCompanyWarehouse;
    private String idSelectedWareHouseWarehouse;
    private String idSelectedProductionWarehouse;
    private ArrayList<MaterialViewModel> dataMaterial;
    private ArrayList<MaterialViewModel> dataMaterialInventory;
    private ArrayList<MaterialViewModel> dataReviewMaterial;
    private String nameSelectedWareHouseWarehouse = "";
    private String nameSelectedWareHouseInventory = "";
    private String userRole;
    private String mCurrentPhotoPath;
    private boolean responsable = true;//Indica si la pantalla que se carga es responsable o legalizado por
    private ArrayList<MaterialViewModel> listMaterialBYProduction;
    private ArrayList<MaterialViewModel> listMaterialForAdd;

    public ArrayList<MaterialViewModel> getDataReviewMaterial() {

        if (dataReviewMaterial == null) dataReviewMaterial = new ArrayList<>();
        return dataReviewMaterial;
    }

    public void setDataReviewMaterial(ArrayList<MaterialViewModel> dataReviewMaterial) {
        this.dataReviewMaterial = dataReviewMaterial;
    }

    public ArrayList<MaterialViewModel> getDataMaterialInventory() {
        if (dataMaterialInventory == null) dataMaterialInventory = new ArrayList<>();
        return dataMaterialInventory;
    }

    public void setDataMaterialInventory(ArrayList<MaterialViewModel> dataMaterialInventory) {
        this.dataMaterialInventory = dataMaterialInventory;
    }

    public ArrayList<MaterialViewModel> getDataMaterial() {
        return dataMaterial;
    }

    public void setDataMaterial(ArrayList<MaterialViewModel> dataMaterial) {
        this.dataMaterial = dataMaterial;
    }

    public Boolean getCurrentAddElementActiveProcess() {
        return isCurrentAddElementActiveProcess;
    }

    public void setCurrentAddElementActiveProcess(Boolean currentAddElementActiveProcess) {
        isCurrentAddElementActiveProcess = currentAddElementActiveProcess;
    }

    public Boolean getCurrentInventoryActiveProcess() {
        return isCurrentInventoryActiveProcess;
    }

    public void setCurrentInventoryActiveProcess(Boolean currentActiveProcess) {
        isCurrentInventoryActiveProcess = currentActiveProcess;
    }

    public String getNameSelectedWareHouseWarehouse() {
        return nameSelectedWareHouseWarehouse;
    }

    public void setNameSelectedWareHouseWarehouse(String nameSelectedWareHouseWarehouse) {
        this.nameSelectedWareHouseWarehouse = nameSelectedWareHouseWarehouse;
    }

    public Boolean getQueryByInventory() {
        return queryByInventory;
    }

    public void setQueryByInventory(Boolean queryByInventory) {
        this.queryByInventory = queryByInventory;
    }


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlServices() {
        return urlServices;
    }

    public void setUrlServices(String urlServices) {
        this.urlServices = urlServices;
    }

    public Integer getIdSelectedCompanyInventory() {
        return idSelectedCompanyInventory;
    }

    public void setIdSelectedCompanyInventory(Integer idSelectedCompanyInventory) {
        this.idSelectedCompanyInventory = idSelectedCompanyInventory;
    }

    public String getIdSelectedWareHouseInventory() {
        return idSelectedWareHouseInventory;
    }

    public void setIdSelectedWareHouseInventory(String idSelectedWareHouseInventory) {
        this.idSelectedWareHouseInventory = idSelectedWareHouseInventory;
    }

    public String getIdSelectedProductionInventory() {
        return idSelectedProductionInventory;
    }

    public void setIdSelectedProductionInventory(String idSelectedProductionInventory) {
        this.idSelectedProductionInventory = idSelectedProductionInventory;
    }

    public int getIdSelectedResponsibleInventory() {
        return idSelectedResponsibleInventory;
    }

    public void setIdSelectedResponsibleInventory(int idSelectedResponsibleInventory) {
        this.idSelectedResponsibleInventory = idSelectedResponsibleInventory;
    }

    public int getIdSelectedTypeElementInventory() {
        return idSelectedTypeElementInventory;
    }

    public void setIdSelectedTypeElementInventory(int idSelectedTypeElementInventory) {
        this.idSelectedTypeElementInventory = idSelectedTypeElementInventory;
    }

    public Integer getIdSelectedCompanyWarehouse() {
        return idSelectedCompanyWarehouse;
    }

    public void setIdSelectedCompanyWarehouse(Integer idSelectedCompanyWarehouse) {
        this.idSelectedCompanyWarehouse = idSelectedCompanyWarehouse;
    }

    public String getIdSelectedWareHouseWarehouse() {
        return idSelectedWareHouseWarehouse;
    }

    public void setIdSelectedWareHouseWarehouse(String idSelectedWareHouseWarehouse) {
        this.idSelectedWareHouseWarehouse = idSelectedWareHouseWarehouse;
    }

    public String getIdSelectedProductionWarehouse() {
        return idSelectedProductionWarehouse;
    }

    public void setIdSelectedProductionWarehouse(String idSelectedProductionWarehouse) {
        this.idSelectedProductionWarehouse = idSelectedProductionWarehouse;
    }

    public int getIdSelectedResponsibleWarehouse() {
        return idSelectedResponsibleWarehouse;
    }

    public void setIdSelectedResponsibleWarehouse(int idSelectedResponsibleWarehouse) {
        this.idSelectedResponsibleWarehouse = idSelectedResponsibleWarehouse;
    }

    public int getIdSelectedTypeElementWarehouse() {
        return idSelectedTypeElementWarehouse;
    }

    public void setIdSelectedTypeElementWarehouse(int idSelectedTypeElementWarehouse) {
        this.idSelectedTypeElementWarehouse = idSelectedTypeElementWarehouse;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    public ArrayList<MaterialViewModel> getListMaterialBYProduction() {
        return listMaterialBYProduction;
    }

    public void setListMaterialBYProduction(ArrayList<MaterialViewModel> listMaterialBYProduction) {
        this.listMaterialBYProduction = listMaterialBYProduction;
    }

    public ArrayList<MaterialViewModel> getListMaterialForAdd() {
        if (listMaterialForAdd == null) listMaterialForAdd = new ArrayList<>();
        return listMaterialForAdd;
    }

    public void setListMaterialForAdd(ArrayList<MaterialViewModel> listMaterialForAdd) {
        this.listMaterialForAdd = listMaterialForAdd;
    }

    public String getNameSelectedWareHouseInventory() {
        return nameSelectedWareHouseInventory;
    }

    public void setNameSelectedWareHouseInventory(String nameSelectedWareHouseInventory) {
        this.nameSelectedWareHouseInventory = nameSelectedWareHouseInventory;
    }
}
