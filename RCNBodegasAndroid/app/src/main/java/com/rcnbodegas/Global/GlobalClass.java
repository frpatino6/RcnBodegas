package com.rcnbodegas.Global;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalClass extends Application implements LifecycleObserver {

    private static GlobalClass instance;
    private String AdminTypeElementId;
    private ArrayList<MaterialViewModel> dataMaterial;
    private ArrayList<MaterialViewModel> dataMaterialInventory;
    private ArrayList<MaterialViewModel> dataReviewMaterial;
    //private String urlServices = "http://192.168.0.7/bodegas/";
    private String urlServices = "http://190.24.154.2:8083/";
    private Integer idSelectedCompanyInventory;
    private Integer idSelectedCompanyWarehouse;
    private String idSelectedProductionInventory;
    private String idSelectedProductionWarehouse;
    private int idSelectedResponsibleInventory = -1;
    private int idSelectedResponsibleWarehouse = -1;
    private int idSelectedTypeElementHeader = -1;
    private int idSelectedTypeElementInventory = -1;
    private Integer idSelectedTypeElementWarehouse = -1;
    private int idSelectedUserWarehouse = -1;
    private String idSelectedWareHouseInventory;
    private String idSelectedWareHouseWarehouse;
    private Intent intent;
    private Boolean isCurrentAddElementActiveProcess = false;
    private Boolean isCurrentInventoryActiveProcess = false;
    private ArrayList<Integer> lastDocuments;
    private ArrayList<MaterialViewModel> listMaterialBYProduction;
    private ArrayList<MaterialViewModel> listMaterialForAdd;
    private ArrayList<ArrayList<MaterialViewModel>> listMaterialForSync;
    private ArrayList<WareHouseViewModel> listWareHouseGlobal;
    private String mCurrentPhotoPath;
    private String nameSelectedWareHouseInventory = "";
    private String nameSelectedWareHouseWarehouse = "";
    private SharedPreferences pref;
    private Boolean queryByInventory = false;
    private boolean responsable = true;//Indica si la pantalla que se carga es responsable o legalizado por

    private String userName;
    private String userRole;

    public String getAdminTypeElementId() {
        return AdminTypeElementId;
    }

    public void setAdminTypeElementId(String adminTypeElementId) {
        AdminTypeElementId = adminTypeElementId;
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

    //<editor-fold desc="Custom object">

    public ArrayList<MaterialViewModel> getDataMaterial() {

        if (dataMaterial == null) dataMaterial = new ArrayList<>();
        return dataMaterial;
    }

    public void setDataMaterial(ArrayList<MaterialViewModel> dataMaterial) {
        this.dataMaterial = dataMaterial;
    }

    public List<MaterialViewModel> getDataMaterialInventory() {
        if (dataMaterialInventory == null) dataMaterialInventory = new ArrayList<>();
        return dataMaterialInventory;
    }

    public void setDataMaterialInventory(ArrayList<MaterialViewModel> dataMaterialInventory) {
        this.dataMaterialInventory = dataMaterialInventory;
    }

    public ArrayList<MaterialViewModel> getDataReviewMaterial() {

        if (dataReviewMaterial == null) dataReviewMaterial = new ArrayList<>();
        return dataReviewMaterial;
    }

    public void setDataReviewMaterial(ArrayList<MaterialViewModel> dataReviewMaterial) {
        this.dataReviewMaterial = dataReviewMaterial;
    }

    public Integer getIdSelectedCompanyInventory() {
        return idSelectedCompanyInventory;
    }

    public void setIdSelectedCompanyInventory(Integer idSelectedCompanyInventory) {
        this.idSelectedCompanyInventory = idSelectedCompanyInventory;
    }

    public Integer getIdSelectedCompanyWarehouse() {
        return idSelectedCompanyWarehouse;
    }

    public void setIdSelectedCompanyWarehouse(Integer idSelectedCompanyWarehouse) {
        this.idSelectedCompanyWarehouse = idSelectedCompanyWarehouse;
    }

    public String getIdSelectedProductionInventory() {
        return idSelectedProductionInventory;
    }

    public void setIdSelectedProductionInventory(String idSelectedProductionInventory) {
        this.idSelectedProductionInventory = idSelectedProductionInventory;
    }

    public String getIdSelectedProductionWarehouse() {
        return idSelectedProductionWarehouse;
    }

    public void setIdSelectedProductionWarehouse(String idSelectedProductionWarehouse) {
        this.idSelectedProductionWarehouse = idSelectedProductionWarehouse;
    }

    public int getIdSelectedResponsibleInventory() {
        return idSelectedResponsibleInventory;
    }

    public void setIdSelectedResponsibleInventory(int idSelectedResponsibleInventory) {
        this.idSelectedResponsibleInventory = idSelectedResponsibleInventory;
    }

    public int getIdSelectedResponsibleWarehouse() {
        return idSelectedResponsibleWarehouse;
    }

    public void setIdSelectedResponsibleWarehouse(int idSelectedResponsibleWarehouse) {
        this.idSelectedResponsibleWarehouse = idSelectedResponsibleWarehouse;
    }

    public int getIdSelectedTypeElementHeader() {
        return idSelectedTypeElementHeader;
    }

    public void setIdSelectedTypeElementHeader(int idSelectedTypeElementHeader) {
        this.idSelectedTypeElementHeader = idSelectedTypeElementHeader;
    }

    public int getIdSelectedTypeElementInventory() {
        return idSelectedTypeElementInventory;
    }

    public void setIdSelectedTypeElementInventory(int idSelectedTypeElementInventory) {
        this.idSelectedTypeElementInventory = idSelectedTypeElementInventory;
    }

    public Integer getIdSelectedTypeElementWarehouse() {
        return idSelectedTypeElementWarehouse;
    }

    public void setIdSelectedTypeElementWarehouse(Integer idSelectedTypeElementWarehouse) {
        this.idSelectedTypeElementWarehouse = idSelectedTypeElementWarehouse;
    }

    public int getIdSelectedUserWarehouse() {
        return idSelectedUserWarehouse;
    }

    public void setIdSelectedUserWarehouse(int idSelectedUserWarehouse) {
        this.idSelectedUserWarehouse = idSelectedUserWarehouse;
    }

    public String getIdSelectedWareHouseInventory() {
        return idSelectedWareHouseInventory;
    }

    public void setIdSelectedWareHouseInventory(String idSelectedWareHouseInventory) {
        this.idSelectedWareHouseInventory = idSelectedWareHouseInventory;
    }

    public String getIdSelectedWareHouseWarehouse() {
        return idSelectedWareHouseWarehouse;
    }

    public void setIdSelectedWareHouseWarehouse(String idSelectedWareHouseWarehouse) {
        this.idSelectedWareHouseWarehouse = idSelectedWareHouseWarehouse;
    }

    public static GlobalClass getInstance() {
        return instance;
    }

    public ArrayList<Integer> getLastDocuments() {
        if (lastDocuments == null) lastDocuments = new ArrayList<>();
        return lastDocuments;
    }

    public void setLastDocuments(ArrayList<Integer> lastDocuments) {

        if (this.lastDocuments == null) this.lastDocuments = new ArrayList<>();
        this.lastDocuments = lastDocuments;
    }

    public List<MaterialViewModel> getListMaterialBYProduction() {
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

    public ArrayList<ArrayList<MaterialViewModel>> getListMaterialForSync() {
        if (listMaterialForSync == null) listMaterialForSync = new ArrayList<>();
        return listMaterialForSync;
    }

    public void setListMaterialForSync(ArrayList<ArrayList<MaterialViewModel>> listMaterialForSync) {
        this.listMaterialForSync = listMaterialForSync;
    }

    public ArrayList<WareHouseViewModel> getListWareHouseGlobal() {
        return listWareHouseGlobal;
    }

    public void setListWareHouseGlobal(ArrayList<WareHouseViewModel> listWareHouseGlobal) {
        this.listWareHouseGlobal = listWareHouseGlobal;
    }

    public String getNameSelectedWareHouseInventory() {
        return nameSelectedWareHouseInventory;
    }

    public void setNameSelectedWareHouseInventory(String nameSelectedWareHouseInventory) {
        this.nameSelectedWareHouseInventory = nameSelectedWareHouseInventory;
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

    public String getUrlServices() {
        return urlServices;
    }

    public void setUrlServices(String urlServices) {
        this.urlServices = urlServices;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        startService(intent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        intent = new Intent(this, KeepLiveApp.class);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        /*if (BuildConfig.DEBUG) {
            urlServices = "http://190.24.154.2:8083/";
        } else {
            urlServices = "http://192.168.0.7/bodegas/";
        }*/

        //pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
    //</editor-fold>


}
