package com.softworks.moldaunia.puntocolorpagamenti;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Item implements Serializable {

    @Exclude private String id;

    private String importo;
    private String ente;
    private String modalita;
    //private String data;
    private int giorno, mese, anno;

    public Item(){

    }

    public Item(String importo, String ente, String modalita, int giorno, int mese, int anno) {
        this.importo = importo;
        this.ente = ente;
        this.modalita = modalita;
        //this.data = data;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
    }

    public String getId() {
        return id;
    }

    public String getImporto() {
        return importo;
    }

    public String getEnte() {
        return ente;
    }

    public String getModalita() {
        return modalita;
    }

    public int getGiorno() {
        return giorno;
    }

    public int getMese() {
        return mese;
    }

    public int getAnno() {
        return anno;
    }

    /*
        public String getData() {
            return data;
        }
        */
    public void setId(String id) {
        this.id = id;
    }

    public void setImporto(String importo) {
        this.importo = importo;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public void setModPagamento(String modPagamento) {
        this.modalita = modalita;
    }
    /*
    public void setData(String data) {
        this.data = data;
    }
    */

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public void setMese(int mese) {
        this.mese = mese;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }
}
