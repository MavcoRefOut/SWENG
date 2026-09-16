package org.example;

import java.time.LocalDate;

public abstract class CreatoreSegnalazione {
    public abstract Segnalazione factoryMethod(
            LocalDate dataInizio,
            LocalDate dataFine,
            String descrizione,
            Object... parametri
    );
}
