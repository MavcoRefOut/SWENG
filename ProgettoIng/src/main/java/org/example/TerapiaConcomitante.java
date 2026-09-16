package org.example;

import java.time.LocalDate;

//classe che descrive una terapia condomitante
public class TerapiaConcomitante extends Segnalazione{
    private String farmaco;

    //costruttore di default della classe, usa quello di Segnalazione
    public TerapiaConcomitante() {
        super();
    }
    
    //costruttore con parametri della classe, usa quello di Segnalazione
    public TerapiaConcomitante(LocalDate dataInizio, LocalDate dataFine, String note, String farmaco) {
        super(dataInizio, dataFine, note);
        this.farmaco = farmaco.trim();
    }

    //getter del campo della classe omonimo
    public String getFarmaco() {
        return farmaco;
    }
    
    //setter del campo della classe omonimo
    public void setFarmaco(String farmaco) {
        this.farmaco = farmaco.trim();
    }

    //override del metodo toString adattato alla classe
    @Override
    public String toString() {
        return "TerapiaConcomitante" +
                "farmaco='" + farmaco + '\'' +
                ", dataInizio=" + getDataInizio() +
                ", dataFine=" + (getDataFine() != null ? getDataFine() : "In corso") +
                ", inCorso=" + inCorso() +
                ", note='" + getDescrizione();
    }
}
