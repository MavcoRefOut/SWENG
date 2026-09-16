package org.example;

import java.util.Date;

public class Persona {
    protected String codiceFiscale;
    protected String nome;
    protected String cognome;
    protected Date dataNascita;
    protected String email;
    protected String username;
    protected String password;

    public Persona(){};

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

    public void setCodiceFiscale(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.trim().length() != 16) {
            throw new IllegalArgumentException("Il codice fiscale deve essere lungo esattamente 16 caratteri");
        }
        this.codiceFiscale = codiceFiscale.trim().toUpperCase();
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
