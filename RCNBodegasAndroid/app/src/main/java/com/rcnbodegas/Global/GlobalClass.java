package com.rcnbodegas.Global;

import android.app.Application;

import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.List;

public class GlobalClass extends Application {

    private String userName;
    private String urlServices="http://192.168.0.5/bodegas/";
    private Integer idSelectedCompany;
    private String idSelectedWareHouse;
    private String idSelectedProduction;
    private int idSelectedResponsible;
    private int idSelectedTypeElement;
    private List<MaterialViewModel>listMaterialBYProduction;

    public List<MaterialViewModel> getListMaterialBYProduction() {
        return listMaterialBYProduction;
    }

    public void setListMaterialBYProduction(List<MaterialViewModel> listMaterialBYProduction) {
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
