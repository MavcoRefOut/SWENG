package org.example;

import java.time.LocalDate;
//fabbrica per generare patologie tramite factory method
public class CreatorePatologia extends CreatoreSegnalazione{
    @Override
    public Segnalazione factoryMethod(LocalDate dataInizio, LocalDate dataFine, String descrizione, Object... parametri) {
        if (parametri == null || parametri.length < 1 || !(parametri[0] instanceof String)) {
            throw new IllegalArgumentException("Parametro mancante o non valido: atteso il nome della patologia (String).");
        }
        String nomePatologia = (String) parametri[0];
        return new Patologia(dataInizio, dataFine, descrizione, nomePatologia);
    }
}
