package com.rcnbodegas.Global;

import android.app.Application;

public class GlobalClass extends Application {

    private String userName;
    private String urlServices="http://192.168.0.3/bodegas/";
    private Integer idSelectedCompany;
    private Integer idSelectedWareHouse;
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

    public Integer getIdSelectedWareHouse() {
        return idSelectedWareHouse;
    }

    public void setIdSelectedWareHouse(Integer idSelectedWareHouse) {
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
}
