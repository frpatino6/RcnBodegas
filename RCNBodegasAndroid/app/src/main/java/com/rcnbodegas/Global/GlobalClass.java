package com.rcnbodegas.Global;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.rcnbodegas.Activities.LoginActivity;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GlobalClass extends Application implements LifecycleObserver {

    private int idSelectedResponsibleInventory = -1;
    private int idSelectedTypeElementInventory = -1;
    private int idSelectedResponsibleWarehouse = -1;
    private int idSelectedUserWarehouse = -1;
    private Integer idSelectedTypeElementWarehouse = -1;
    private int idSelectedTypeElementHeader = -1;
    private String userName;
    private String AdminTypeElementId;
    private String urlServices = "http://172.20.0.154:8083/";
    //private String urlServices = "http://192.168.0.6/bodegas/";
    // private String urlServices = "http://172.20.17.88/bodegas/";
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
    private ArrayList<MaterialViewModel> listMaterialBYProduction;
    private ArrayList<MaterialViewModel> listMaterialForAdd;
    private ArrayList<WareHouseViewModel> listWareHouseGlobal;
    private ArrayList<ArrayList<MaterialViewModel>> listMaterialForSync;
    private String nameSelectedWareHouseWarehouse = "";
    private String nameSelectedWareHouseInventory = "";
    private String userRole;
    private String mCurrentPhotoPath;
    private boolean responsable = true;//Indica si la pantalla que se carga es responsable o legalizado por
    private static GlobalClass instance;
    private Intent intent;


    private SharedPreferences pref;



    public static GlobalClass getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        intent = new Intent(this, KeepLiveApp.class);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        //pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        startService(intent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        stopService(intent);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //<editor-fold desc="Custom object">


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

    public String getAdminTypeElementId() {
        return AdminTypeElementId;
    }

    public void setAdminTypeElementId(String adminTypeElementId) {
        AdminTypeElementId = adminTypeElementId;
    }

    public int getIdSelectedUserWarehouse() {
        return idSelectedUserWarehouse;
    }

    public void setIdSelectedUserWarehouse(int idSelectedUserWarehouse) {
        this.idSelectedUserWarehouse = idSelectedUserWarehouse;
    }

    public ArrayList<MaterialViewModel> getDataReviewMaterial() {

        if (dataReviewMaterial == null) dataReviewMaterial = new ArrayList<>();
        return dataReviewMaterial;
    }

    public void setDataReviewMaterial(ArrayList<MaterialViewModel> dataReviewMaterial) {
        this.dataReviewMaterial = dataReviewMaterial;
    }

    public List<MaterialViewModel> getDataMaterialInventory() {
        if (dataMaterialInventory == null) dataMaterialInventory = new ArrayList<>();
        return dataMaterialInventory;
    }

    public void setDataMaterialInventory(ArrayList<MaterialViewModel> dataMaterialInventory) {
        this.dataMaterialInventory = dataMaterialInventory;
    }

    public ArrayList<MaterialViewModel> getDataMaterial() {

        if (dataMaterial == null) dataMaterial = new ArrayList<>();
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

    public Integer getIdSelectedTypeElementWarehouse() {
        return idSelectedTypeElementWarehouse;
    }

    public void setIdSelectedTypeElementWarehouse(Integer idSelectedTypeElementWarehouse) {
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

    public String getNameSelectedWareHouseInventory() {
        return nameSelectedWareHouseInventory;
    }

    public void setNameSelectedWareHouseInventory(String nameSelectedWareHouseInventory) {
        this.nameSelectedWareHouseInventory = nameSelectedWareHouseInventory;
    }

    public int getIdSelectedTypeElementHeader() {
        return idSelectedTypeElementHeader;
    }

    public void setIdSelectedTypeElementHeader(int idSelectedTypeElementHeader) {
        this.idSelectedTypeElementHeader = idSelectedTypeElementHeader;
    }
    //</editor-fold>


}
