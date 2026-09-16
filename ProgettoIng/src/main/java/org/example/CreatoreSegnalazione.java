package org.example;

import java.time.LocalDate;
//abstract factory per generare fabbriche di segnalazioni
public abstract class CreatoreSegnalazione {
    public abstract Segnalazione factoryMethod(
            LocalDate dataInizio,
            LocalDate dataFine,
            String descrizione,
            Object... parametri
    );
}
