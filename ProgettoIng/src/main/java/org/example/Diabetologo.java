package org.example;

import java.util.Date;
//classe che descrive un diabetologo
public class Diabetologo extends Persona{
    public Diabetologo(String codiceFiscale, String nome, String cognome, Date dataNascita, String email, String username, String password) {
        super(codiceFiscale, nome, cognome, dataNascita, email, username, password);
    }

    public Diabetologo(){
        super();
    }
}
