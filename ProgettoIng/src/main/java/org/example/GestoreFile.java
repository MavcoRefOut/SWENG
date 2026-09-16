package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//classe che gestisce l'interazione con il file system, ovvero i file dove sono presenti le varie informazioni di servizio per il funzionamento del sistema
public class GestoreFile {

    public GestoreFile(){}
    
    //il log delle operazioni dei diabetologi e le notifiche sono in file txt separando con ';', mentre tutto il resto è in json per facilitare la serializzazione degli oggetti
    private static final String SEPARATORE = ";";
    private static String pathNotifiche = "src/main/resources/notifiche.txt";
    private static String pathPaziente = "src/main/resources/pazienti.json";
    private static String pathDiabetologo = "src/main/resources/diabetologi.json";
    private static String pathFarmaco = "src/main/resources/farmaci.json";
    private static String pathLog = "src/main/resources/log.txt";
    private static final ObjectMapper jsonMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    //cerca il utenti per credenzili, ritorna la persona che sta provando ad accedere
    public Persona cercaPerCredenziali(String us, String pass) {
        if (us == null || pass == null || us.trim().isEmpty() || pass.trim().isEmpty()) {
            return null;
        }

        String username = us.trim();
        String password = pass.trim();

        //prova ad aprire il file e vedere la corrispondenza tra credenziali inserite e credenziali presenti nel file
        try {
            //controlla se sta provando ad accedere un diabetologo
            File fileMedici = new File(pathDiabetologo);
            if (fileMedici.exists() && fileMedici.length() > 0) {
                List<Diabetologo> medici = jsonMapper.readValue(fileMedici, new TypeReference<List<Diabetologo>>() {});
                if (medici != null) {
                    Diabetologo medicoTrovato = medici.stream()
                            .filter(m -> username.equals(m.getUsername()) && password.equals(m.getPassword()))
                            .findFirst()
                            .orElse(null);

                    if (medicoTrovato != null) {
                        return medicoTrovato;
                    }
                }
            }
             //controlla se sta provando ad accedere un paziente
            File filePazienti = new File(pathPaziente);
            if (filePazienti.exists() && filePazienti.length() > 0) {
                List<Paziente> pazienti = jsonMapper.readValue(filePazienti, new TypeReference<List<Paziente>>() {});
                if (pazienti != null) {
                    Paziente pazienteTrovato = pazienti.stream()
                            .filter(p -> username.equals(p.getUsername()) && password.equals(p.getPassword()))
                            .findFirst()
                            .orElse(null);

                    if (pazienteTrovato != null) {
                        return pazienteTrovato;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Errore durante la lettura dei file JSON: " + e.getMessage());
        }
        //se non c'è ritorna null => messaggio d'errore credenziali sbagliate / assenti
        return null;
    }

    //cerca il paziente per nome e cognome
    public Paziente cercaPazientePerNomeCognome(String nome, String cognome) {
        if (nome == null || cognome == null || nome.trim().isEmpty() || cognome.trim().isEmpty()) {
            return null;
        }

        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try {
            List<Paziente> tuttiPazienti = jsonMapper.readValue(file, new TypeReference<List<Paziente>>() {});
            if (tuttiPazienti == null) {
                return null;
            }
            //ritorna il paziente trovato o meno filtrando sulla stream dei pazienti ottenuta dal file
            return tuttiPazienti.stream()
                    .filter(p -> p.getNome() != null && p.getCognome() != null
                            && p.getNome().trim().equalsIgnoreCase(nome.trim())
                            && p.getCognome().trim().equalsIgnoreCase(cognome.trim()))
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            System.err.println("Errore durante la ricerca per nome e cognome: " + e.getMessage());
            return null;
        }
    }

    //scrive in formato csv le notifiche su notifiche.txt
    public void scriviNotifica(Notifica notifica) throws IOException {
        String riga = notifica.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathNotifiche, true))) {
            writer.write(riga);
            writer.newLine();
        }
    }

    //trova le notifiche in notifiche.txt partendo dal codice fiscale del destinatario, usarlo quando si fa il login
    public ArrayList<String> ottieniNotifiche(String codiceFiscale) throws FileNotFoundException {
        ArrayList<String> notificheTrovate = new ArrayList<>();

        //controlla che il codice fiscale sia stato inserito
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            return notificheTrovate;
        }

        //controlla che il file esista
        File file = new File(pathNotifiche);
        if (!file.exists()) {
            return notificheTrovate;
        }

        String targetCf = codiceFiscale.trim().toUpperCase();
        //legge una riga finchè non trova testo
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                if (riga.trim().isEmpty()) {
                    continue;
                }

                String[] campi = riga.split(SEPARATORE, -1);
                //salva le infomazioni della notifica
                if (campi.length >= 4) {
                    String cfDestinatario = campi[1].trim();

                    if (targetCf.equalsIgnoreCase(cfDestinatario)) {
                        String cfMandante = campi[0].trim();
                        String livello = campi[2].trim();
                        String testo = campi[3].trim();
                        //aggiunge le info nella lista di notifiche che verranno visualizzate
                        notificheTrovate.add(riga);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura delle notifiche: " + e.getMessage());
        }

        return notificheTrovate;
    }

    //Elimina una notifica passata come stringa tutta
    public boolean eliminaNotifica(String rigaNotificaDaEliminare) {
        try {
            Path path = Paths.get(pathNotifiche);
            List<String> tutteLeRighe = Files.readAllLines(path);
            List<String> righeAggiornate = new ArrayList<>();
            boolean eliminata = false;

            for (String riga : tutteLeRighe) {
                if (!eliminata && riga.trim().equals(rigaNotificaDaEliminare.trim())) {
                    eliminata = true;
                    continue; // Salta la riga, non aggiungendola alla nuova lista
                }
                righeAggiornate.add(riga);
            }

            if (eliminata) {
                Files.write(path, righeAggiornate,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            }

            return false; // Riga non trovata nel file

        } catch (IOException e) {
            System.err.println("Errore durante l'aggiornamento del file notifiche:");
            return false;
        }
    }

    //metodo per salvare in json i pazienti
    public void salvaPaziente(Paziente paziente) throws IOException {
        if (paziente == null) {
            throw new IllegalArgumentException("Il paziente non può essere nullo");
        }

        File file = new File(pathPaziente);

        ArrayList<Paziente> lista = new ArrayList<>();
        if (file.exists() && file.length() > 0) {
            lista = jsonMapper.readValue(file, new TypeReference<ArrayList<Paziente>>() {});
        }

        lista.removeIf(p ->
                (p.getUsername() != null && p.getUsername().equalsIgnoreCase(paziente.getUsername())) ||
                        (p.getCodiceFiscale() != null && p.getCodiceFiscale().equalsIgnoreCase(paziente.getCodiceFiscale()))
        );

        lista.add(paziente);
        jsonMapper.writeValue(file, lista);
    }

    //metodo per carica il paziente da json
    public Paziente caricaPaziente(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        ArrayList<Paziente> listaPazienti = jsonMapper.readValue(file, new TypeReference<ArrayList<Paziente>>() {});

        String target = username.trim();
        for (Paziente p : listaPazienti) {
            if (target.equalsIgnoreCase(p.getUsername())) {
                return p;
            }
        }

        return null;
    }

    //metodo per salvare i diabetologi in json
    public void salvaDiabetologo(Diabetologo diabetologo) throws IOException {
        if (diabetologo == null) {
            throw new IllegalArgumentException("Il diabetologo non può essere nullo");
        }

        File file = new File(pathDiabetologo);

        ArrayList<Diabetologo> lista = new ArrayList<>();
        if (file.exists() && file.length() > 0) {
            lista = jsonMapper.readValue(file, new TypeReference<ArrayList<Diabetologo>>() {});
        }

        lista.removeIf(d ->
                (d.getUsername() != null && d.getUsername().equalsIgnoreCase(diabetologo.getUsername())) ||
                        (d.getCodiceFiscale() != null && d.getCodiceFiscale().equalsIgnoreCase(diabetologo.getCodiceFiscale()))
        );

        lista.add(diabetologo);
        jsonMapper.writeValue(file, lista);
    }

    //metodo per caricare i diabetologi
    public Diabetologo caricaDiabetologo(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        File file = new File(pathDiabetologo);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        ArrayList<Diabetologo> listaDiabetologi = jsonMapper.readValue(file, new TypeReference<ArrayList<Diabetologo>>() {});

        String target = username.trim();
        for (Diabetologo d : listaDiabetologi) {
            if (target.equalsIgnoreCase(d.getUsername())) {
                return d;
            }
        }

        return null;
    }

    //cerca il paziente per codice fiscale
    public Paziente cercaPazienteCf(String codiceFiscale) throws IOException {
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            return null;
        }
        //apre il file con i dati dei pazienti
        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        ArrayList<Paziente> listaPazienti = jsonMapper.readValue(file, new TypeReference<ArrayList<Paziente>>() {});

        //scorre tutto il file finchè non trova il CF cercato, ritornando il paziente o null se non lo trova
        String target = codiceFiscale.trim();
        for (Paziente p : listaPazienti) {
            if (p.getCodiceFiscale() != null && target.equalsIgnoreCase(p.getCodiceFiscale().trim())) {
                return p;
            }
        }

        return null;
    }
    //metodo per scrivere un farmaco nel file contenente le lista dei farmaci
    public void salvaFarmaco(Farmaco farmaco) throws IOException {
        if (farmaco == null) {
            throw new IllegalArgumentException("Il farmaco non può essere nullo");
        }

        File file = new File(pathFarmaco);

        ArrayList<Farmaco> lista = new ArrayList<>();
        if (file.exists() && file.length() > 0) {
            lista = jsonMapper.readValue(file, new TypeReference<ArrayList<Farmaco>>() {});
        }

        // Rimuove eventuali duplicati per nome commerciale
        lista.removeIf(f -> f.getNomeFarmaco() != null &&
                f.getNomeFarmaco().equalsIgnoreCase(farmaco.getNomeFarmaco().trim()));

        lista.add(farmaco);
        jsonMapper.writeValue(file, lista);
    }

    public ArrayList<Farmaco> caricaTuttiFarmaci() throws IOException {
        File file = new File(pathFarmaco);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        return jsonMapper.readValue(file, new TypeReference<ArrayList<Farmaco>>() {});
    }

    //metodo che cerca un farmaco nel file per nome commerciale
    public Farmaco cercaFarmacoPerNome(String nomeFarmaco) throws IOException {
        if (nomeFarmaco == null || nomeFarmaco.trim().isEmpty()) {
            return null;
        }

        ArrayList<Farmaco> farmaci = caricaTuttiFarmaci();
        String target = nomeFarmaco.trim();

        for (Farmaco f : farmaci) {
            if (f.getNomeFarmaco() != null && target.equalsIgnoreCase(f.getNomeFarmaco().trim())) {
                return f;
            }
        }

        return null;
    }

    //metodoche salva nel log una modifica effettuata su un paziente da parte di un diabetologo, gestendo la sezione critica
    public synchronized void scriviLog(String rigaLog) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathLog, true))) {
            writer.write(rigaLog);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del log: " + e.getMessage());
        }
    }

    //metodo che ritorna la lista dei pazienti affidati a un diabetologo specifico
    public List<Paziente> pazientiPerMedico(String cfMedico) {
        if (cfMedico == null || cfMedico.trim().isEmpty()) {
            return new ArrayList<>();
        }

        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            List<Paziente> tuttiPazienti = jsonMapper.readValue(file, new TypeReference<List<Paziente>>() {});

            if (tuttiPazienti == null) {
                return new ArrayList<>();
            }

            //ritorna i pazienti filtrati dalla stream completa
            return tuttiPazienti.stream()
                    .filter(p -> p.getTerapie() != null && p.getTerapie().stream()
                            .anyMatch(t -> t.getMedicoPrescrittore() != null
                                    && cfMedico.equalsIgnoreCase(t.getMedicoPrescrittore().getCodiceFiscale())))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Errore durante la lettura dei pazienti: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //metodo che ritorna un paziente cercato per email
    public Paziente pazientePerMail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try {
            List<Paziente> tuttiPazienti = jsonMapper.readValue(file, new TypeReference<List<Paziente>>() {
            });
            if (tuttiPazienti == null) {
                return null;
            }

            return tuttiPazienti.stream()
                    .filter(p -> p.getEmail() != null && p.getEmail().trim().equalsIgnoreCase(email.trim()))
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
            return null;
        }
    }
}
