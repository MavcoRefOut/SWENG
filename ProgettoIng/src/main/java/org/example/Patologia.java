package org.example;

import java.time.LocalDate;

public class Patologia extends Segnalazione{
    private String nome;


    public Patologia() {
        super();
    }


    public Patologia(LocalDate dataInizio, LocalDate dataFine, String note, String nome) {
        super(dataInizio, dataFine, note);
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della patologia non può essere vuoto.");
        }
        this.nome = nome.trim();
    }

    public String getDescrizione() {
        return this.nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della patologia non può essere vuoto.");
        }
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return "Patologia" +
                "nome='" + nome + '\'' +
                ", dataInizio=" + getDataInizio() +
                ", dataFine=" + (getDataFine() != null ? getDataFine() : "In corso/Cronica") +
                ", inCorso=" + inCorso() +
                ", note='" + getDescrizione();
    }
}
