package org.example;

import java.time.LocalDateTime;

public class Rilevazione {
    private Boolean prePasto;
    private LocalDateTime momentoRilevazione;
    private int livelloGlicemia;

    public Rilevazione(){}

    public Rilevazione(Boolean prePasto, LocalDateTime momentoRilevazione, int livelloGlicemia) {
        this.prePasto = prePasto;
        this.momentoRilevazione = momentoRilevazione;
        this.livelloGlicemia = livelloGlicemia;
    }

    public Boolean getPrePasto() {
        return prePasto;
    }

    public void setPrePasto(Boolean prePasto) {
        this.prePasto = prePasto;
    }

    public LocalDateTime getMomentoRilevazione() {
        return momentoRilevazione;
    }

    public void setMomentoRilevazione(LocalDateTime momentoRilevazione) {
        this.momentoRilevazione = momentoRilevazione;
    }

    public int getLivelloGlicemia() {
        return livelloGlicemia;
    }

    public void setLivelloGlicemia(int livelloGlicemia) {
        this.livelloGlicemia = livelloGlicemia;
    }

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
