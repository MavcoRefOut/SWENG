package org.example;

import java.util.Date;

//classe che descrive una persona
public class Persona {
    protected String codiceFiscale;
    protected String nome;
    protected String cognome;
    protected Date dataNascita;
    protected String email;
    protected String username;
    protected String password;

    //costruttore di default della classe
    public Persona(){};

    //costruttore con parametri della classe
    public Persona(String codiceFiscale, String nome, String cognome, Date dataNascita,
                   String email, String username, String password) {
        this.setCodiceFiscale(codiceFiscale);
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    //setter del campo della classe omonimo, con controllo di validità
    public void setCodiceFiscale(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.trim().length() != 16) {
            throw new IllegalArgumentException("Il codice fiscale deve essere lungo esattamente 16 caratteri");
        }
        this.codiceFiscale = codiceFiscale.trim().toUpperCase();
    }

     //setter del campo della classe omonimo
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

     //getter del campo della classe omonimo
    public String getNome() {
        return nome;
    }

     //setter del campo della classe omonimo
    public void setNome(String nome) {
        this.nome = nome;
    }

     //getter del campo della classe omonimo
    public String getCognome() {
        return cognome;
    }

     //setter del campo della classe omonimo
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

     //getter del campo della classe omonimo
    public Date getDataNascita() {
        return dataNascita;
    }

     //setter del campo della classe omonimo
    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

     //getter del campo della classe omonimo
    public String getEmail() {
        return email;
    }

     //setter del campo della classe omonimo
    public void setEmail(String email) {
        this.email = email;
    }

     //getter del campo della classe omonimo
    public String getUsername() {
        return username;
    }

     //setter del campo della classe omonimo
    public void setUsername(String username) {
        this.username = username;
    }

     //getter del campo della classe omonimo
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
