package org.example;

import java.time.LocalDate;
//factory per generare terapie concomitanti tramite factory method
public class CreatoreTerapiaConcomitante extends CreatoreSegnalazione{
    @Override
    public Segnalazione factoryMethod(LocalDate dataInizio, LocalDate dataFine, String descrizione, Object... parametri) {
        if (parametri == null || parametri.length < 1 || !(parametri[0] instanceof String)) {
            throw new IllegalArgumentException("Parametro mancante o non valido: atteso il nome del farmaco (String).");
        }
        String farmaco = (String) parametri[0];
        return new TerapiaConcomitante(dataInizio, dataFine, descrizione, farmaco);
    }
}

