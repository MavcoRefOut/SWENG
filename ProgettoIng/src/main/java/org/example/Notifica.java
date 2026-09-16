package org.example;

import java.io.IOException;

//classe che descrive una notifica
public class Notifica {
    private Persona mandante;
    private Persona destinatario;
    private String messaggio;
    private LivelloPericolo livelloPericolo;

    //costruttore con parametri della classe
    public Notifica(Persona mandante, Persona destinatario, String messaggio, LivelloPericolo livelloPericolo) {
        this.mandante = mandante;
        this.destinatario = destinatario;
        this.messaggio = messaggio;
        this.livelloPericolo = livelloPericolo;
    }

    //getter del campo della classe omonimo
    public Persona getMandante() {
        return mandante;
    }

    //setter del campo della classe omonimo
    public void setMandante(Persona mandante) {
        this.mandante = mandante;
    }

    //getter del campo della classe omonimo
    public Persona getDestinatario() {
        return destinatario;
    }

    //setter del campo della classe omonimo
    public void setDestinatario(Persona destinatario) {
        this.destinatario = destinatario;
    }

    //getter del campo della classe omonimo
    public String getMessaggio() {
        return messaggio;
    }

    //setter del campo della classe omonimo
    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    //getter del campo della classe omonimo
    public LivelloPericolo getLivelloPericolo() {
        return livelloPericolo;
    }

    //setter del campo della classe omonimo
    public void setLivelloPericolo(LivelloPericolo livelloPericolo) {
        this.livelloPericolo = livelloPericolo;
    }

    //metodo che scrive una notifica sul file 'inviandola'
    public static Boolean inviaNotifica(Notifica notifica) throws IOException {
        GestoreFile gestoreFile = new GestoreFile();
        gestoreFile.scriviNotifica(notifica);
        return true;
    }

    //override del metodo toString adattato alla classe
    @Override
    public String toString() {
        String cfMandante = (this.getMandante() != null) ? this.getMandante().getCodiceFiscale() : "N/D";
        String cfDestinatario = (this.getDestinatario() != null) ? this.getDestinatario().getCodiceFiscale() : "N/D";

        String messaggio = (this.getMessaggio() != null)
                ? this.getMessaggio().replace("\n", " ").replace(";", ",")
                : "";

        String livello = (this.getLivelloPericolo() != null)
                ? this.getLivelloPericolo().name()
                : "N/D";

        String riga = String.join(";", cfMandante, cfDestinatario, livello, messaggio);
        return riga;
    }
}
