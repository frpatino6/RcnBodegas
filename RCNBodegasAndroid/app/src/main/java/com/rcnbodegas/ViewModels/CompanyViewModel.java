package com.rcnbodegas.ViewModels;

public class CompanyViewModel {

    private String id ;

    private String companyName ;

    public CompanyViewModel(String id, String companyName) {
        this.id = id;
        this.companyName = companyName;
    }

    public CompanyViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
