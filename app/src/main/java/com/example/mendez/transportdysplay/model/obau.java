package com.example.mendez.transportdysplay.model;

public class obau {

    private String id;
    private String nomb;
    private String pla;
    private String estado;
    private String fcm;

    public obau(String id,String nomb, String pla, String estado,String fcm) {
        this.id = id;
        this.nomb = nomb;
        this.pla = pla;
        this.estado = estado;
        this.fcm = fcm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomb() {
        return nomb;
    }

    public void setNomb(String nomb) {
        this.nomb = nomb;
    }

    public String getPla() {
        return pla;
    }

    public void setPla(String pla) {
        this.pla = pla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }

}
