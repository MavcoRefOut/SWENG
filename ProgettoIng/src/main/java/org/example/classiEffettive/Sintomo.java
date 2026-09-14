package org.example.classiEffettive;

import java.time.LocalDate;

public class Sintomo extends Segnalazione {

    public Sintomo(){
        super();
    }

    public Sintomo(LocalDate dataInizio, LocalDate dataFine, String descrizione, String nome){
        super(dataInizio,dataFine,descrizione);
        this.nome = nome;
    }

    private String nome;
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return "Sintomo" +
                "nome='" + nome + '\'' +
                ", dataInizio=" + getDataInizio() +
                ", dataFine=" + (getDataFine() != null ? getDataFine() : "In corso") +
                ", note='" + getDescrizione();
    }
}
