package com.example.excelreader.DAO;

public class RecordCabinet {

    private String ora;
    private String corrente;
    private String tensione;
    private String energia;

    public String getPotenza() {
        return potenza;
    }

    public void setPotenza(String potenza) {
        this.potenza = potenza;
    }

    private String potenza;
    public RecordCabinet(String ora, String corrente, String tensione, String energia, String potenza) {
        this.ora = ora;
        this.corrente = corrente;
        this.tensione = tensione;
        this.energia = energia;
        this.potenza = potenza;
    }

    public RecordCabinet(){

    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getCorrente() {
        return corrente;
    }

    public void setCorrente(String corrente) {
        this.corrente = corrente;
    }

    public String getTensione() {
        return tensione;
    }

    public void setTensione(String tensione) {
        this.tensione = tensione;
    }

    public String getEnergia() {
        return energia;
    }

    public void setEnergia(String energia) {
        this.energia = energia;
    }

    @Override
    public String toString() {
        return "RecordCabinet{" +
                "ora='" + ora + '\'' +
                ", corrente=" + corrente +
                ", tensione=" + tensione +
                ", energia=" + energia +
                '}';
    }

}
