package com.rcnbodegas.ViewModels;

public class TypeElementViewModel {

    private Integer id;
    private String name;

    public TypeElementViewModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TypeElementViewModel() {
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
