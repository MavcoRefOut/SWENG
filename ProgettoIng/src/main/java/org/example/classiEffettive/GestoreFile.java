package org.example.classiEffettive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestoreFile {

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

    //cerca il utenti per credenzili
    public static Persona cercaPerCredenziali(String us, String pass) {
        if (us == null || pass == null || us.trim().isEmpty() || pass.trim().isEmpty()) {
            return null;
        }

        String username = us.trim();
        String password = pass.trim();

        try {
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

        return null;
    }

    //cerca il paziente per nome e cognome
    public static Paziente cercaPazientePerNomeCognome(String nome, String cognome) {
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
    public static void scriviNotifica(Notifica notifica) throws IOException {
        String riga = notifica.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathNotifiche, true))) {
            writer.write(riga);
            writer.newLine();
        }
    }

    //trova le notifiche in notifiche.txt partendo dal codice fiscale del destinatario, usarlo quando si fa il login
    public static ArrayList<String> ottieniNotifiche(String codiceFiscale) throws FileNotFoundException {
        ArrayList<String> notificheTrovate = new ArrayList<>();

        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            return notificheTrovate;
        }

        File file = new File(pathNotifiche);
        if (!file.exists()) {
            return notificheTrovate;
        }

        String targetCf = codiceFiscale.trim().toUpperCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                if (riga.trim().isEmpty()) {
                    continue;
                }

                String[] campi = riga.split(SEPARATORE, -1);

                if (campi.length >= 4) {
                    String cfDestinatario = campi[1].trim();

                    if (targetCf.equalsIgnoreCase(cfDestinatario)) {
                        String cfMandante = campi[0].trim();
                        String livello = campi[2].trim();
                        String testo = campi[3].trim();

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
    public static boolean eliminaNotifica(String rigaNotificaDaEliminare) {
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
    public static void salvaPaziente(Paziente paziente) throws IOException {
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
    public static Paziente caricaPaziente(String username) throws IOException {
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
    public static void salvaDiabetologo(Diabetologo diabetologo) throws IOException {
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
    public static Diabetologo caricaDiabetologo(String username) throws IOException {
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
    public static Paziente cercaPazienteCf(String codiceFiscale) throws IOException {
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            return null;
        }

        File file = new File(pathPaziente);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        ArrayList<Paziente> listaPazienti = jsonMapper.readValue(file, new TypeReference<ArrayList<Paziente>>() {});

        String target = codiceFiscale.trim();
        for (Paziente p : listaPazienti) {
            if (p.getCodiceFiscale() != null && target.equalsIgnoreCase(p.getCodiceFiscale().trim())) {
                return p;
            }
        }

        return null;
    }

    public static void salvaFarmaco(Farmaco farmaco) throws IOException {
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

    public static ArrayList<Farmaco> caricaTuttiFarmaci() throws IOException {
        File file = new File(pathFarmaco);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        return jsonMapper.readValue(file, new TypeReference<ArrayList<Farmaco>>() {});
    }

    public static Farmaco cercaFarmacoPerNome(String nomeFarmaco) throws IOException {
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

    public static synchronized void scriviLog(String rigaLog) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathLog, true))) {
            writer.write(rigaLog);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del log: " + e.getMessage());
        }
    }

    public static List<Paziente> pazientiPerMedico(String cfMedico) {
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

    public static Paziente pazientePerMail(String email) {
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