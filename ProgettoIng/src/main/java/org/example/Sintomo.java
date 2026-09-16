package org.example;

import java.time.LocalDate;

//classe che descrive un sintomo
public class Sintomo extends Segnalazione {

    private String nome;
    
    //costruttore di default della classe, usa quello di Segnalazione
    public Sintomo(){
        super();
    }

    //costruttore con parametri della classe, usa quello di Segnalazione
    public Sintomo(LocalDate dataInizio, LocalDate dataFine, String descrizione, String nome){
        super(dataInizio,dataFine,descrizione);
        this.nome = nome;
    }

    //getter del campo della classe omonimo
    public String getNome() {
        return nome;
    }

    //setter del campo della classe omonimo
    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    //override del metodo toString adattato alla classe
    @Override
    public String toString() {
        return "Sintomo" +
                "nome='" + nome + '\'' +
                ", dataInizio=" + getDataInizio() +
                ", dataFine=" + (getDataFine() != null ? getDataFine() : "In corso") +
                ", note='" + getDescrizione();
    }
}
