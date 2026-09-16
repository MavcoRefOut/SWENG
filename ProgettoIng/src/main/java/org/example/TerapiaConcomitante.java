package org.example;

import java.time.LocalDate;

public class TerapiaConcomitante extends Segnalazione{
    private String farmaco;

    public TerapiaConcomitante() {
        super();
    }

    public TerapiaConcomitante(LocalDate dataInizio, LocalDate dataFine,
                               String note, String farmaco) {
        super(dataInizio, dataFine, note);
        this.farmaco = farmaco.trim();
    }


    public String getFarmaco() {
        return farmaco;
    }

    public void setFarmaco(String farmaco) {
        this.farmaco = farmaco.trim();
    }

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
