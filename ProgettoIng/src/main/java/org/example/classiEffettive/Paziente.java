package org.example.classiEffettive;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class Paziente extends Persona {

    private Diabetologo diabetologoRiferimento;
    private ArrayList<Rilevazione> rilevazioni;
    private ArrayList<Segnalazione> segnalazioni;
    private ArrayList<Terapia> terapie;
    private ArrayList<Assunzione> assunzioni;
    private String breveDescrizione;

    public Paziente(){
        super();
    }

    public Paziente(Diabetologo diab, String codiceFiscale, String nome, String cognome, Date dataNascita, String email, String username, String password) {
        super(codiceFiscale, nome, cognome, dataNascita, email, username, password);
        rilevazioni = new ArrayList<Rilevazione>();
        segnalazioni = new ArrayList<Segnalazione>();
        terapie = new ArrayList<Terapia>();
        assunzioni = new ArrayList<Assunzione>();
        this.diabetologoRiferimento = diab;
        breveDescrizione = "";
    }

    public ArrayList<Rilevazione> getRilevazioni() {
        return rilevazioni;
    }

    public void setRilevazioni(ArrayList<Rilevazione> rilevazioni) {
        this.rilevazioni = rilevazioni;
    }

    public Boolean inserisciRilevazione(Rilevazione rilevazione) throws IOException {
        boolean ritorno = true;
        rilevazioni.add(rilevazione);
        if(controllaLivello(rilevazione)){
            Notifica not = new Notifica(this,diabetologoRiferimento,rilevazione.toString(),LivelloPericolo.BASSO);
            ritorno = Notifica.inviaNotifica(not);
        }
        return ritorno;
    }

    private Boolean controllaLivello(Rilevazione rilevazione){
        int valore = rilevazione.getLivelloGlicemia();
        if (rilevazione.getPrePasto()) {
            return valore < 80 || valore > 130;
        }
        return valore > 180;
    }

    public ArrayList<Segnalazione> getSegnalazioni() {
        return segnalazioni;
    }

    public void setSegnalazioni(ArrayList<Segnalazione> condizioni) {
        this.segnalazioni = condizioni;
    }

    public Boolean inserisciSegnalazione(Segnalazione condizione){
        return segnalazioni.add(condizione);
    }

    public ArrayList<Terapia> getTerapie() {
        return terapie;
    }

    public void setTerapie(ArrayList<Terapia> terapie) {
        this.terapie = terapie;
    }

    public ArrayList<Assunzione> getAssunzioni() {
        return assunzioni;
    }

    public void setAssunzioni(ArrayList<Assunzione> assunzioni) {
        this.assunzioni = assunzioni;
    }

    public Boolean aggiungiTerapia(Terapia terapia){
        return terapie.add(terapia);
    }

    public Diabetologo getDiabetologoRiferimento(){return diabetologoRiferimento;}

    public void setDiabetologoRiferimento(Diabetologo diabetologoRiferimento){this.diabetologoRiferimento = diabetologoRiferimento;}

    public String getBreveDescrizione(){return breveDescrizione;}
    public void setBreveDescrizione(String breveDescrizione){this.breveDescrizione = breveDescrizione;}

    @Override
    public String toString() {
        return "Nome='" + getNome() + '\'' +
                ", cognome='" + getCognome() + '\'' +
                ", dataNascita=" + getDataNascita() +
                ", email='" + getEmail() + '\'' +
                ", medicoRiferimento=" + (diabetologoRiferimento != null ? diabetologoRiferimento.getNome() + " " + diabetologoRiferimento.getCognome() : "Nessuno") +
                ", rilevazioniCount=" + (rilevazioni != null ? rilevazioni.size() : 0) +
                ", condizioniCount=" + (segnalazioni != null ? segnalazioni.size() : 0) +
                ", terapieCount=" + (terapie != null ? terapie.size() : 0) +
                ", assunzioniCount=" + (assunzioni != null ? assunzioni.size() : 0);
    }

    public boolean inserisciAssunzione(Assunzione assunzione) {
        if (assunzione == null || assunzione.getFarmacoAssunto() == null) {
            return false;
        }

        Terapia terapiaPrescritta = null;
        for (Terapia t : terapie) {
            if (t.getFarmaco() != null &&
                    t.getFarmaco().equals(assunzione.getFarmacoAssunto())) {
                terapiaPrescritta = t;
                break;
            }
        }

        if (terapiaPrescritta == null) {
            return false;
        }

        if (assunzione.getQuantita() > terapiaPrescritta.getQuantita()) {
            return false;
        }

        LocalDate dataAssunzione = assunzione.getOrarioAssunzione()
                .toLocalDateTime()
                .toLocalDate();

        long assunzioniOggi = assunzioni.stream()
                .filter(a -> a.getFarmacoAssunto() != null
                        && assunzione.getFarmacoAssunto() != null
                        && a.getFarmacoAssunto().equals(assunzione.getFarmacoAssunto()))
                .filter(a -> a.getOrarioAssunzione() != null
                        && a.getOrarioAssunzione().toLocalDateTime().toLocalDate().equals(dataAssunzione))
                .count();

        if (assunzioniOggi >= terapiaPrescritta.getAssunzioniGG()) {
            return false;
        }

        assunzioni.add(assunzione);
        return true;
    }

    public void verificaAderenzaTerapie(Boolean isMedico) throws IOException {
        if (this.terapie == null || this.terapie.isEmpty()) {
            return;
        }

        LocalDate oggi = LocalDate.now();

        for (Terapia terapia : this.terapie) {
            Farmaco farmaco = terapia.getFarmaco();
            if (farmaco == null) continue;

            Optional<LocalDate> ultimaData = (this.assunzioni == null) ? Optional.empty() :
                    this.assunzioni.stream()
                            .filter(a -> a.getFarmacoAssunto() != null && a.getFarmacoAssunto().equals(farmaco))
                            .map(a -> a.getOrarioAssunzione().toLocalDateTime().toLocalDate())
                            .max(LocalDate::compareTo);

            long giorniSenzaAssunzione = ultimaData
                    .map(data -> ChronoUnit.DAYS.between(data, oggi))
                    .orElse(3L); // 3 giorni di default se non ci sono mai state assunzioni

            if (giorniSenzaAssunzione >= 3) {
                Diabetologo medico = terapia.getMedicoPrescrittore();
                String nomeFarmaco = farmaco.getNomeFarmaco();

                if(isMedico){
                    String testoPerMedico = "Il paziente non assume " + nomeFarmaco + " da " + giorniSenzaAssunzione + " giorni.";
                    Notifica notificaMedico = new Notifica(
                            this,
                            medico,
                            testoPerMedico,
                            LivelloPericolo.ELEVATO
                    );
                    GestoreFile.scriviNotifica(notificaMedico);
                }
                else{
                    String testoPerPaziente = "Non assumi " + nomeFarmaco + " da " + giorniSenzaAssunzione + " giorni. Assumi la dose prescritta!";
                    Notifica notificaPaziente = new Notifica(
                            this,
                            this,
                            testoPerPaziente,
                            LivelloPericolo.ELEVATO
                    );
                    GestoreFile.scriviNotifica(notificaPaziente);
                }

            }
        }
    }

    public void aggiornaDatiPaziente(Paziente p){
        this.codiceFiscale = p.getCodiceFiscale();
        this.nome = p.getNome();
        this.cognome = p.getCognome();
        this.dataNascita = p.getDataNascita();
        this.email = p.getEmail();
        this.username = p.getUsername();
        this.password = p.getPassword();
        this.breveDescrizione = p.getBreveDescrizione();
    }
}
