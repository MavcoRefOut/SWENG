package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//interfaccia per l'implementazione del pattern Facade
public interface InterfacciaFacciata {

    //firme dei metodi di cui viene fatto l'override in Facciata

    //ricerca persona per credenziali di login
    public Persona cercaPersona(String username, String password);

    //verifica se un paziente affidato al diabetologo sta aderendo alla terapia o meno
    public void verificaAderenzaTerapie(Diabetologo diabetologo) throws IOException;

    //ricerca di un paziente per nome e cognome
    public Paziente cercaPazientePerNomeCognome(String nome,String cognome);

    //salva un paziente nel file apposito
    public void salvaPaziente(Paziente p) throws IOException;

    //salva una modifica nel file di log delle operazioni dei diabetologi
    public void scriviLog(String s);

    //ritorna il paziente cercato per email
    public Paziente pazientePerMail(String email);

    //invio email al diabetologo
    public Boolean inviaMail(String email, Diabetologo d, String messaggio, LivelloPericolo livello) throws IOException;

    //eliminazione della notifica selezionata
    public void eliminaNotifica(String notifica);

    //ritorna notifiche di una persona
    public ArrayList<String> ottieniNotifiche(String s) throws FileNotFoundException;

    //carica farmaci presenti nel sistema
    public ArrayList<Farmaco> caricaTuttiFarmaci() throws IOException;

    //scrive una notifica da inviare
    public void scriviNotifica(Notifica notifica) throws IOException;

    //inserimento di una rilevazione del livello di glicemia di un paziente
    public void inserisciRilevazione(Paziente p, Rilevazione r) throws IOException;

    //inserimento di un'assunzione di un farmaco da parte di un paziente
    public void inserisciAssunzione(Paziente p, Assunzione a) throws IOException;

    //ritorna il diabetologo di riferimento di un paziente specifico
    public Diabetologo ottieniDiabetologo(Paziente p);

    //verifica l'aderenza o meno alla terapia di un paziente
    public void verificaAderenze(Paziente p, Boolean val) throws IOException;

    //ottiene le rilevazioni effettuate da un paziente in un arco temporale
    public List<Rilevazione> ottieniRilevazioni(Paziente p, LocalDateTime limite);

    //ottiene le terapie di un paaziente
    public ArrayList<Terapia> ottieniTerapie(Paziente p);

    //ritorna una singola riga del log delle operazioni dei diabetologi
    public String rigaLog(Persona p, String s);

    //aggiorna e terapie di un paziente
    public Boolean settaTerapie(ArrayList<Terapia> lista, Paziente p,List<String> modifiche) throws IOException;

    //modifica una terapia di un paziente
    public void modificaDatiTerapia(Terapia t, int assunzioni, String indicazioni, int quantita, Farmaco farmacoScelto, Diabetologo diab);

    //ritorna in codice fiscale di una persona
    public String ottieniCf(Persona p);

    //ritorna una breve descrizione del paziente specificato
    public String ottieniBreveDesc(Paziente p);

    //salva le modifiche alla breve descrizione di un paziente
    public void settaBreveDes(Paziente paziente,String s) throws IOException;

    //aggiunge una segnalazione da parte di un paziente
    public Boolean aggiungiSegnalazione(Paziente p, CreatoreSegnalazione creatore, LocalDate dataInizione, LocalDate dataFine, String descrizione, Object... parametri);
}
