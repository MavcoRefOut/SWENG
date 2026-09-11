package org.example.classiEffettive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Timestamp;

public class Assunzione {

    private Timestamp orarioAssunzione;
    private int quantita;
    private Terapia terapia;

    public Assunzione() {}

    public Assunzione(Timestamp orarioAssunzione, int quantita, Terapia terapia) {
        this.orarioAssunzione = orarioAssunzione;
        this.quantita = quantita;
        this.terapia = terapia;
    }

    public Timestamp getOrarioAssunzione() {
        return orarioAssunzione;
    }

    public void setOrarioAssunzione(Timestamp orarioAssunzione) {
        this.orarioAssunzione = orarioAssunzione;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public Terapia getTerapia() {
        return terapia;
    }

    public void setTerapia(Terapia terapia) {
        this.terapia = terapia;
    }

    @JsonIgnore
    public Farmaco getFarmacoAssunto() {
        return (this.terapia != null) ? this.terapia.getFarmaco() : null;
    }

    @Override
    public String toString() {
        return "Assunzione{" +
                "orarioAssunzione=" + orarioAssunzione +
                ", quantita=" + quantita +
                ", farmacoAssunto=" + (getFarmacoAssunto() != null ? getFarmacoAssunto().getNomeFarmaco() : "N/D") +
                '}';
    }
}