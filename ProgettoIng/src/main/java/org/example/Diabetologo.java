package org.example;

import java.util.Date;

public class Diabetologo extends Persona{
    public Diabetologo(String codiceFiscale, String nome, String cognome, Date dataNascita, String email, String username, String password) {
        super(codiceFiscale, nome, cognome, dataNascita, email, username, password);
    }

    public Diabetologo(){
        super();
    }
}
