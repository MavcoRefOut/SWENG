package org.example;

import java.time.LocalDate;

//classe che descrive una patologia
public class Patologia extends Segnalazione{
    private String nome;

    //costruttore di default della classe, usa quello di Segnalazione
    public Patologia() {
        super();
    }

    //costruttore con parametri della classe, usa quello di Segnalazione e controlla il nome della patologia inserito
    public Patologia(LocalDate dataInizio, LocalDate dataFine, String note, String nome) {
        super(dataInizio, dataFine, note);
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della patologia non può essere vuoto.");
        }
        this.nome = nome.trim();
    }

    //getter del campo della classe omonimo
    public String getDescrizione() {
        return this.nome;
    }

    //getter del campo della classe omonimo
    public String getNome() {
        return nome;
    }

    //setter del campo della classe omonimo
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della patologia non può essere vuoto.");
        }
        this.nome = nome.trim();
    }

    //override del metodo toString adattato alla classe
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
