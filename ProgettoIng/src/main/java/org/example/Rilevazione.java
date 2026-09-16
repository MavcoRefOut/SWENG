package org.example;

import java.time.LocalDateTime;

//classe che descrive una rilevazione del livello di glicemia di un paziente
public class Rilevazione {
    private Boolean prePasto;
    private LocalDateTime momentoRilevazione;
    private int livelloGlicemia;

    //costruttore di default della classe
    public Rilevazione(){}

    //costruttore con parametri della classe
    public Rilevazione(Boolean prePasto, LocalDateTime momentoRilevazione, int livelloGlicemia) {
        this.prePasto = prePasto;
        this.momentoRilevazione = momentoRilevazione;
        this.livelloGlicemia = livelloGlicemia;
    }

    //getter del campo della classe omonimo
    public Boolean getPrePasto() {
        return prePasto;
    }

    //setter del campo della classe omonimo
    public void setPrePasto(Boolean prePasto) {
        this.prePasto = prePasto;
    }

    //getter del campo della classe omonimo
    public LocalDateTime getMomentoRilevazione() {
        return momentoRilevazione;
    }

    //setter del campo della classe omonimo
    public void setMomentoRilevazione(LocalDateTime momentoRilevazione) {
        this.momentoRilevazione = momentoRilevazione;
    }

    //getter del campo della classe omonimo
    public int getLivelloGlicemia() {
        return livelloGlicemia;
    }

    //setter del campo della classe omonimo
    public void setLivelloGlicemia(int livelloGlicemia) {
        this.livelloGlicemia = livelloGlicemia;
    }

    //override del metodo toString adattato alla classe
    @Override
    public String toString() {
        String momentoStr = (momentoRilevazione != null)
                ? momentoRilevazione.toLocalTime().toString() // se vuoi formattare: momentoRilevazione.format(DateTimeFormatter.ofPattern("HH:mm"))
                : "N/D";

        String statoPasto = (prePasto != null)
                ? (prePasto ? "Pre-pasto" : "Post-pasto")
                : "N/D";

        return "Rilevazione [" +
                "Livello: " + livelloGlicemia + " mg/dL" +
                ", Ora: " + momentoStr +
                ", Momento: " + statoPasto +
                "]";
    }
}
