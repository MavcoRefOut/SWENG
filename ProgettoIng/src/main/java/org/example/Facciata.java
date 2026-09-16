package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
//facade dell'interfaccia per l'utilizzo del pattern Facade per gestire l'interazione con file system
public class Facciata implements InterfacciaFacciata{

    //gestore del file
    private GestoreFile gestore;

    //costruttore della facade
    public Facciata(){
        gestore = new GestoreFile();
    }

    //serie di override dei metodi già presenti in GestoreFile
    
    @Override
    public Persona cercaPersona(String username, String password) {
        return gestore.cercaPerCredenziali(username,password);
    }

    
    @Override
    public void verificaAderenzaTerapie(Diabetologo diabetologo) throws IOException {
        List<Paziente> listaPazienti = gestore.pazientiPerMedico(diabetologo.getCodiceFiscale());
        for(Paziente p : listaPazienti)
            p.verificaAderenzaTerapie(true);
    }

   
    @Override
    public Paziente cercaPazientePerNomeCognome(String nome, String cognome) {
        return gestore.cercaPazientePerNomeCognome(nome, cognome);
    }

    @Override
    public void salvaPaziente(Paziente p) throws IOException {
        gestore.salvaPaziente(p);
    }

    @Override
    public void scriviLog(String s) {
        gestore.scriviLog(s);
    }

    @Override
    public Paziente pazientePerMail(String email) {
        return gestore.pazientePerMail(email);
    }

    @Override
    public Boolean inviaMail(String email, Diabetologo d, String messaggio, LivelloPericolo livello) throws IOException {
        Paziente p = pazientePerMail(email);
        Notifica not = new Notifica(d,p,messaggio,LivelloPericolo.MODERATO);
        Notifica.inviaNotifica(not);
        return true;
    }

    @Override
    public void eliminaNotifica(String notifica) {
        gestore.eliminaNotifica(notifica);
    }

    @Override
    public ArrayList<String> ottieniNotifiche(String s) throws FileNotFoundException {
        return gestore.ottieniNotifiche(s);
    }

    @Override
    public ArrayList<Farmaco> caricaTuttiFarmaci() throws IOException {
        return gestore.caricaTuttiFarmaci();
    }

    @Override
    public void scriviNotifica(Notifica notifica) throws IOException {
        Notifica.inviaNotifica(notifica);
    }

    @Override
    public void inserisciRilevazione(Paziente p, Rilevazione r) throws IOException {
        Notifica not = p.inserisciRilevazione(r);
        scriviNotifica(not);
    }

    @Override
    public void inserisciAssunzione(Paziente p, Assunzione a) throws IOException {
        Notifica not = p.inserisciAssunzione(a);
        if(not!=null)
            gestore.scriviNotifica(not);
    }

    @Override
    public Diabetologo ottieniDiabetologo(Paziente p) {
        return p.getDiabetologoRiferimento();
    }

    @Override
    public void verificaAderenze(Paziente p, Boolean val) throws IOException {
        Notifica not = p.verificaAderenzaTerapie(val);
        if(not!=null)
            gestore.scriviNotifica(not);
    }

    @Override
    public List<Rilevazione> ottieniRilevazioni(Paziente paziente, LocalDateTime limiteSettimana) {
        return paziente.getRilevazioni().stream()
                .filter(r -> r.getMomentoRilevazione() != null &&
                        r.getMomentoRilevazione().isAfter(limiteSettimana))
                .sorted(Comparator.comparing(Rilevazione::getMomentoRilevazione))
                .collect(Collectors.toList());
    }

    @Override
    public ArrayList<Terapia> ottieniTerapie(Paziente p) {
        return p.getTerapie();
    }

    @Override
    public String rigaLog(Persona p, String s) {
        return p.getCodiceFiscale() + s;
    }

    @Override
    public Boolean settaTerapie(ArrayList<Terapia> lista, Paziente p,List<String> modifiche) throws IOException {
        p.setTerapie(lista);
        gestore.salvaPaziente(p);
        for (String s : modifiche) {
            gestore.scriviLog(s);
        }
        return true;
    }

    @Override
    public void modificaDatiTerapia(Terapia t, int assunzioni, String indicazioni, int quantita, Farmaco farmacoScelto, Diabetologo diab) {
        t.modificaDatiTerapia(assunzioni, indicazioni, quantita, farmacoScelto, diab);

    }

    @Override
    public String ottieniCf(Persona p) {
        return p.getCodiceFiscale();
    }

    @Override
    public String ottieniBreveDesc(Paziente p) {
        return p.getBreveDescrizione();
    }

    @Override
    public void settaBreveDes(Paziente paziente, String s) throws IOException {
        paziente.setBreveDescrizione(s);
        gestore.salvaPaziente(paziente);
    }

    @Override
    public Boolean aggiungiSegnalazione(Paziente p, CreatoreSegnalazione creatore , LocalDate dataInizione, LocalDate dataFine, String descrizione, Object... parametri) {
        Segnalazione s = creatore.factoryMethod(dataInizione, dataFine, descrizione,parametri);
        return p.inserisciSegnalazione(s);
    }
}
