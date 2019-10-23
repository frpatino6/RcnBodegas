package com.rcnbodegas.ViewModels;

public class ResponsibleViewModel {

    private Long id;
    private String name;
    private String tipoBodega;
    private String codigoProduccion;

    public ResponsibleViewModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ResponsibleViewModel() {
    }

    public String getCodigoProduccion() {
        return codigoProduccion;
    }

    public void setCodigoProduccion(String codigoProduccion) {
        this.codigoProduccion = codigoProduccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipoBodega() {
        return tipoBodega;
    }

    public void setTipoBodega(String tipoBodega) {
        tipoBodega = tipoBodega;
    }
}
