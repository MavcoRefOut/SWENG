package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface InterfacciaFacciata {

    public Persona cercaPersona(String username, String password);

    public void verificaAderenzaTerapie(Diabetologo diabetologo) throws IOException;

    public Paziente cercaPazientePerNomeCognome(String nome,String cognome);

    public void salvaPaziente(Paziente p) throws IOException;

    public void scriviLog(String s);

    public Paziente pazientePerMail(String email);

    public Boolean inviaMail(String email, Diabetologo d, String messaggio, LivelloPericolo livello) throws IOException;

    public void eliminaNotifica(String notifica);

    public ArrayList<String> ottieniNotifiche(String s) throws FileNotFoundException;

    public ArrayList<Farmaco> caricaTuttiFarmaci() throws IOException;

    public void scriviNotifica(Notifica notifica) throws IOException;

    public void inserisciRilevazione(Paziente p, Rilevazione r) throws IOException;

    public void inserisciAssunzione(Paziente p, Assunzione a) throws IOException;

    public Diabetologo ottieniDiabetologo(Paziente p);

    public void verificaAderenze(Paziente p, Boolean val) throws IOException;

    public List<Rilevazione> ottieniRilevazioni(Paziente p, LocalDateTime limite);

    public ArrayList<Terapia> ottieniTerapie(Paziente p);

    public String rigaLog(Persona p, String s);

    public Boolean settaTerapie(ArrayList<Terapia> lista, Paziente p,List<String> modifiche) throws IOException;

    public void modificaDatiTerapia(Terapia t, int assunzioni, String indicazioni, int quantita, Farmaco farmacoScelto, Diabetologo diab);

    public String ottieniCf(Persona p);

    public String ottieniBreveDesc(Paziente p);

    public void settaBreveDes(Paziente paziente,String s) throws IOException;

    public Boolean aggiungiSegnalazione(Paziente p, CreatoreSegnalazione creatore, LocalDate dataInizione, LocalDate dataFine, String descrizione, Object... parametri);
}
