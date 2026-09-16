package org.example;

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
    private Notifica notGlobale;

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
        notGlobale = null;
    }

    public ArrayList<Rilevazione> getRilevazioni() {
        return rilevazioni;
    }

    public void setRilevazioni(ArrayList<Rilevazione> rilevazioni) {
        this.rilevazioni = rilevazioni;
    }


    public Notifica inserisciRilevazione(Rilevazione rilevazione) throws IOException {
        Notifica not = null;
        rilevazioni.add(rilevazione);
        if(controllaLivello(rilevazione)){
            not = new Notifica(this,diabetologoRiferimento,rilevazione.toString(),LivelloPericolo.BASSO);
        }
        return not;
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

    //aggiunge un assunzione e verifica che segua le terapie prescritte
    public Notifica inserisciAssunzione(Assunzione assunzione) throws IOException {
        Boolean returnValue = isCoerente(assunzione);
        if(returnValue==true){
            assunzioni.add(assunzione);
            if(notGlobale!=null) {
                Notifica momentanea = notGlobale;
                notGlobale = null;
                return momentanea;
            }
        }
        return null;
    }

    private boolean isCoerente(Assunzione assunzione){
        if (assunzione == null) {
            return false;
        }

        Terapia terapia = assunzione.getTerapia();
        if (terapia == null) {
            notGlobale = new Notifica(this,this,"Attenzione: l'assunzione registrata non è associata ad alcuna terapia valida.",LivelloPericolo.ELEVATO);
            return false;
        }

        if (assunzione.getQuantita() > terapia.getQuantita()) {
            notGlobale = new Notifica(this,this,
                    "Attenzione: dosaggio eccessivo"
                            + assunzione.getQuantita() + " invece di " + terapia.getQuantita() + ".",
                    LivelloPericolo.ELEVATO);
        } else if (assunzione.getQuantita() < terapia.getQuantita()) {
            notGlobale = new Notifica(this,this,
                    "Attenzione: dosaggio insufficiente "
                            + assunzione.getQuantita() + " invece di " + terapia.getQuantita() + ".",
                    LivelloPericolo.ELEVATO);
        }
        if (assunzione.getOrarioAssunzione() != null) {
            LocalDate dataAssunzione = assunzione.getOrarioAssunzione()
                    .toLocalDateTime()
                    .toLocalDate();

            long assunzioniOggi = assunzioni.stream()
                    .filter(a -> terapia.equals(a.getTerapia()))
                    .filter(a -> a.getOrarioAssunzione() != null
                            && a.getOrarioAssunzione().toLocalDateTime().toLocalDate().equals(dataAssunzione))
                    .count();

            if (assunzioniOggi > terapia.getAssunzioniGG()) {

                notGlobale = new Notifica(this,this,
                        "Attenzione: hai superato il numero massimo di assunzioni giornaliere"
                                + assunzioniOggi + " su " + terapia.getAssunzioniGG() + ".",
                        LivelloPericolo.ELEVATO);
            }
        }
        return true;
    }

    //Controlla se non è stata seguita per tre giorni o più
    public Notifica verificaAderenzaTerapie(Boolean isMedico) throws IOException {
        Notifica ritorno = null;
        if (this.terapie == null || this.terapie.isEmpty()) {
            return ritorno;
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
                    .orElse(3L);

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
                    ritorno = notificaMedico;
                }
                else{
                    String testoPerPaziente = "Non assumi " + nomeFarmaco + " da " + giorniSenzaAssunzione + " giorni. Assumi la dose prescritta!";
                    Notifica notificaPaziente = new Notifica(
                            this,
                            this,
                            testoPerPaziente,
                            LivelloPericolo.ELEVATO
                    );
                    ritorno = notificaPaziente;
                }

            }
        }
        return ritorno;
    }

}
