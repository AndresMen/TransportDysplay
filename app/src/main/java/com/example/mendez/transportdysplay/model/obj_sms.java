package com.example.mendez.transportdysplay.model;

public class obj_sms {
    private   String mmessage;
    private   String mfecha;
    private   String mhora;
    private   String mnombreUs;
    private   String mplacaUs;
    private  String tipo;
    private boolean checked;

    public obj_sms(String mmessage, String mfecha,String mhora, String mnombreUs, String mplacaUs, String tipo) {
        this.mmessage = mmessage;
        this.mfecha = mfecha;
        this.mhora = mhora;
        this.mnombreUs = mnombreUs;
        this.mplacaUs = mplacaUs;
        this.tipo = tipo;
    }

    public String getMmessage() {
        return mmessage;
    }

    public void setMmessage(String mmessage) {
        this.mmessage = mmessage;
    }

    public String getMhora() {
        return mhora;
    }

    public void setMhora(String mhora) {
        this.mhora = mhora;
    }

    public String getMfecha() {
        return mfecha;
    }

    public void setMfecha(String mfecha) {
        this.mfecha = mfecha;
    }

    public String getMnombreUs() {
        return mnombreUs;
    }

    public void setMnombreUs(String mnombreUs) {
        this.mnombreUs = mnombreUs;
    }

    public String getMplacaUs() {
        return mplacaUs;
    }

    public void setMplacaUs(String mplacaUs) {
        this.mplacaUs = mplacaUs;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
