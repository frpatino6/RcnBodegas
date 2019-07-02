package com.rcnbodegas.ViewModels;

public class ResponsibleViewModel {

    private Integer id;
    private String name;

    public ResponsibleViewModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ResponsibleViewModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
