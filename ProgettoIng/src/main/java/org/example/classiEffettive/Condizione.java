package org.example.classiEffettive;

import java.util.Date;

public class Condizione {
    private String descrizione;
    private Date dataInizio;
    private Date dataFine;

    public Condizione(){}

    public Condizione(String descrizione, Date dataInizio, Date dataFine){
        this.dataFine = dataFine;
        this.dataInizio = dataInizio;
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }
}
