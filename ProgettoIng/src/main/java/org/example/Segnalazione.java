package org.example;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo" // Nome del campo che comparirà nel JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Sintomo.class, name = "Sintomo"),
        @JsonSubTypes.Type(value = Patologia.class, name = "Patologia"),
        @JsonSubTypes.Type(value = TerapiaConcomitante.class, name = "TerapiaConcomitante")
})

public abstract class Segnalazione {

    private LocalDate dataInizio;
    private LocalDate dataFine; // Può essere null se la condizione/terapia è ancora in corso
    private String descrizione;

    public Segnalazione() {
    }

    public Segnalazione(LocalDate dataInizio, LocalDate dataFine, String descrizione) {
        if (dataInizio == null) {
            throw new IllegalArgumentException("La data di inizio non può essere null.");
        }
        if (dataFine != null && dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio.");
        }
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.descrizione = descrizione;
    }


    public boolean inCorso() {
        LocalDate oggi = LocalDate.now();
        return this.dataFine == null || !this.dataFine.isBefore(oggi);
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        if (dataInizio == null) {
            throw new IllegalArgumentException("La data di inizio non può essere null.");
        }
        if (this.dataFine != null && this.dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data di inizio non può essere successiva alla data di fine.");
        }
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        if (dataFine != null && this.dataInizio != null && dataFine.isBefore(this.dataInizio)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio.");
        }
        this.dataFine = dataFine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String note) {
        this.descrizione = note;
    }
}