package org.example.classiEffettive;

import java.io.IOException;

public class Notifica {
    private Persona mandante;
    private Persona destinatario;
    private String messaggio;
    private LivelloPericolo livelloPericolo;

    public Notifica(Persona mandante, Persona destinatario, String messaggio, LivelloPericolo livelloPericolo) {
        this.mandante = mandante;
        this.destinatario = destinatario;
        this.messaggio = messaggio;
        this.livelloPericolo = livelloPericolo;
    }

    public Persona getMandante() {
        return mandante;
    }

    public void setMandante(Persona mandante) {
        this.mandante = mandante;
    }

    public Persona getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Persona destinatario) {
        this.destinatario = destinatario;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public LivelloPericolo getLivelloPericolo() {
        return livelloPericolo;
    }

    public void setLivelloPericolo(LivelloPericolo livelloPericolo) {
        this.livelloPericolo = livelloPericolo;
    }

    public static Boolean inviaNotifica(Notifica notifica) throws IOException {
        GestoreFile.scriviNotifica(notifica);
        return true;
    }

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
