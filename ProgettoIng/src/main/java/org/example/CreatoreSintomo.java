package org.example;

import java.time.LocalDate;
//fabbrica per generare sintomi tramite factory method
public class CreatoreSintomo extends CreatoreSegnalazione{

    @Override
    public Segnalazione factoryMethod(LocalDate dataInizio, LocalDate dataFine, String descrizione,Object... parametri) {
        if (parametri == null || parametri.length < 1 || !(parametri[0] instanceof String)) {
            throw new IllegalArgumentException("Parametro mancante o non valido: atteso il nome del sintomo (String).");
        }
        String nome = (String) parametri[0];
        return new Sintomo(dataInizio, dataFine, descrizione, nome);
    }
}
