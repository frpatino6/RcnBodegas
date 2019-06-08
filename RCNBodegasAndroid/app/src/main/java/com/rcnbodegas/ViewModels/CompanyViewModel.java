package com.rcnbodegas.ViewModels;

public class CompanyViewModel {

    private Integer id ;

    private String companyName ;

    public CompanyViewModel(int id, String companyName) {
        this.id = id;
        this.companyName = companyName;
    }

    public CompanyViewModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
