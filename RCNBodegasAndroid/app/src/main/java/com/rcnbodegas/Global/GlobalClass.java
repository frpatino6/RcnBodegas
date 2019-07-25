package com.rcnbodegas.Global;

import android.app.Application;

import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalClass extends Application {

    private String userName;
    private String urlServices="http://172.20.0.154/";
    private Integer idSelectedCompany;
    private String idSelectedWareHouse;
    private String idSelectedProduction;
    private String mCurrentPhotoPath;
    private int idSelectedResponsible;
    private int idSelectedTypeElement;
    private boolean responsable=true;//Indica si la pantalla que se carga es responsable o legalizado por
    private ArrayList<MaterialViewModel>listMaterialBYProduction;
    private ArrayList<MaterialViewModel>listMaterialForAdd;

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }


    public ArrayList<MaterialViewModel> getListMaterialForAdd() {
        return listMaterialForAdd;
    }

    public void setListMaterialForAdd(ArrayList<MaterialViewModel> listMaterialForAdd) {

        if(this.listMaterialForAdd ==null) this.listMaterialForAdd=new ArrayList<>();
        this.listMaterialForAdd = listMaterialForAdd;
    }

    public ArrayList<MaterialViewModel> getListMaterialBYProduction() {
        return listMaterialBYProduction;
    }

    public void setListMaterialBYProduction(ArrayList<MaterialViewModel> listMaterialBYProduction) {
        this.listMaterialBYProduction = listMaterialBYProduction;
    }

    public String getNameSelectedWareHouse() {
        return nameSelectedWareHouse;
    }

    public void setNameSelectedWareHouse(String nameSelectedWareHouse) {
        this.nameSelectedWareHouse = nameSelectedWareHouse;
    }

    private String nameSelectedWareHouse;
    private String userRole;

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }



    public Integer getIdSelectedCompany() {
        return idSelectedCompany;
    }

    public void setIdSelectedCompany(Integer idSelectedCompany) {
        this.idSelectedCompany = idSelectedCompany;
    }

    public String getIdSelectedWareHouse() {
        return idSelectedWareHouse;
    }

    public void setIdSelectedWareHouse(String idSelectedWareHouse) {
        this.idSelectedWareHouse = idSelectedWareHouse;
    }



    public String getUrlServices() {
        return urlServices;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdSelectedProduction() {
        return idSelectedProduction;
    }

    public void setIdSelectedProduction(String idSelectedProduction) {
        this.idSelectedProduction = idSelectedProduction;
    }

    public int getIdSelectedResponsible() {
        return idSelectedResponsible;
    }

    public void setIdSelectedResponsible(int idSelectedResponsible) {
        this.idSelectedResponsible = idSelectedResponsible;
    }

    public int getIdSelectedTypeElement() {
        return idSelectedTypeElement;
    }

    public void setIdSelectedTypeElement(int idSelectedTypeElement) {
        this.idSelectedTypeElement = idSelectedTypeElement;
    }
}
