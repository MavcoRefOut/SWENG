package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

//classe che descrive l'assunzione di un farmaco da parte di un paziente
public class Assunzione {

    private Timestamp orarioAssunzione;
    private int quantita;
    private Terapia terapia;
    
    //costruttore di default della classe
    public Assunzione() {}

    //cotruttore con parametri della classe
    public Assunzione(Timestamp orarioAssunzione, int quantita, Terapia terapia) {
        this.orarioAssunzione = orarioAssunzione;
        this.quantita = quantita;
        this.terapia = terapia;
    }

    //getter del campo di classe omonimo
    public Timestamp getOrarioAssunzione() {
        return orarioAssunzione;
    }

    //setter del campo di classe omonimo
    public void setOrarioAssunzione(Timestamp orarioAssunzione) {
        this.orarioAssunzione = orarioAssunzione;
    }

    //getter del campo di classe omonimo
    public int getQuantita() {
        return quantita;
    }

    //setter del campo di classe omonimo
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    //getter del campo di classe omonimo
    public Terapia getTerapia() {
        return terapia;
    }

    //setter del campo di classe omonimo
    public void setTerapia(Terapia terapia) {
        this.terapia = terapia;
    }

    //getter del campo di classe omonimo, se c'è una terapia allora ritorna il farmaco altrimenti null
    @JsonIgnore
    public Farmaco getFarmacoAssunto() {
        return (this.terapia != null) ? this.terapia.getFarmaco() : null;
    }

    //toString adattato alla classe
    @Override
    public String toString() {
        return "Assunzione{" +
                "orarioAssunzione=" + orarioAssunzione +
                ", quantita=" + quantita +
                ", farmacoAssunto=" + (getFarmacoAssunto() != null ? getFarmacoAssunto().getNomeFarmaco() : "N/D") +
                '}';
    }
}
