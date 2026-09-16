package org.example;

import java.util.Date;
//classe che descrive un diabetologo
public class Diabetologo extends Persona{

    //costruttore con parametri della classe, richiama quello di Persona
    public Diabetologo(String codiceFiscale, String nome, String cognome, Date dataNascita, String email, String username, String password) {
        super(codiceFiscale, nome, cognome, dataNascita, email, username, password);
    }

    //costruttore di default della classe
    public Diabetologo(){
        super();
    }
}
