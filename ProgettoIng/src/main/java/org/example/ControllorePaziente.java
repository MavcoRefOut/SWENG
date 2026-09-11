package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.classiEffettive.*;

import javax.swing.plaf.nimbus.NimbusStyle;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


public class ControllorePaziente {

    private Paziente paziente;
    public void settaDatiPaziente(Paziente paziente) throws IOException {
        this.paziente = paziente;
        paziente.verificaAderenzaTerapie(false);
        caricaNotifiche();
    }

    @FXML
    private Button btnRilevazione;

    @FXML
    private Button btnLogout;

    @FXML
    private VBox vBoxNotifiche;

    @FXML
    private ListView<String> listViewNotifiche;

    @FXML
    private Button btnEliminaNotifica;

    @FXML
    private Button btnCondizione;

    @FXML
    private Button btnAssunzione;

    @FXML
    private Button btnInviaMail;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnRilevazione.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    GestoreFile.salvaPaziente(paziente);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    //metodo per inserire una rilevazione
    @FXML
    private void inserisciRilevazione(ActionEvent evento) throws IOException {
        // 1. Creazione della finestra di dialogo modale
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Rilevazione Glicemica");
        dialog.setHeaderText("Inserisci il valore numerico e lo stato:");

        ButtonType btnSalva = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, ButtonType.CANCEL);

        // 2. Componenti di input
        TextField txtNumero = new TextField();

        // Limita l'input ai soli numeri (e opzionale virgola/punto)
        txtNumero.textProperty().addListener((obs, vecchio, nuovo) -> {
            if (!nuovo.matches("\\d*(\\.\\d*)?")) {
                txtNumero.setText(vecchio);
            }
        });

        CheckBox chkBooleano = new CheckBox("Pre Pasto");

        // 3. Layout semplice con GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Valore:"), 0, 0);
        grid.add(txtNumero, 1, 0);
        grid.add(new Label("Opzione:"), 0, 1);
        grid.add(chkBooleano, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // 4. Mostra e gestisci il risultato
        Optional<ButtonType> risultato = dialog.showAndWait();

        if (risultato.isPresent() && risultato.get() == btnSalva) {
            String testo = txtNumero.getText().trim();
            if (!testo.isEmpty()) {
                double valore = Double.parseDouble(testo);
                boolean opzioneSelezionata = chkBooleano.isSelected();
                LocalDateTime ora = LocalDateTime.now();

                //quelli sopra sono i due valori, salvare nella rilevazione
                paziente.inserisciRilevazione(new Rilevazione(opzioneSelezionata,ora,(int)valore));
            }
        }
    }

    @FXML
    private void faiLoguot(ActionEvent evento) throws IOException{
        GestoreFile.salvaPaziente(paziente);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/formLogin.fxml"));
        Parent root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(root));
        loginStage.show();

        Stage currentStage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
        currentStage.close();
    }

    private void caricaNotifiche(){
        try {
            if (paziente == null) {
                return;
            }

            String cf = paziente.getCodiceFiscale();
            var notifiche = GestoreFile.ottieniNotifiche(cf);
            listViewNotifiche.getItems().clear();
            if (notifiche != null && !notifiche.isEmpty()) {
                listViewNotifiche.getItems().addAll(notifiche);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminaNotifica(ActionEvent evento){
        String notificaSelezionata = listViewNotifiche.getSelectionModel().getSelectedItem();
        if (notificaSelezionata == null) {
            mostraAlert(Alert.AlertType.WARNING,"Nessuna selezione","Nessuna notifica è stata selezionata");
            return;
        }
        listViewNotifiche.getItems().remove(notificaSelezionata);
        GestoreFile.eliminaNotifica(notificaSelezionata);
    }

    @FXML
    private void inserisciDatiCondizione(ActionEvent evento){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Inserimento Dati");
        dialog.setHeaderText("Inserisci la descrizione e l'intervallo temporale");

        ButtonType btnSalva = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnAnnulla);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // 1. Campo Descrizione
        TextArea txtDescrizione = new TextArea();
        txtDescrizione.setPromptText("Inserisci qui la descrizione...");
        txtDescrizione.setPrefRowCount(3);
        txtDescrizione.setWrapText(true);
        GridPane.setHgrow(txtDescrizione, Priority.ALWAYS);

        // 2. Data Inizio (default: oggi)
        DatePicker datePickerInizio = new DatePicker(LocalDate.now());
        datePickerInizio.setMaxWidth(Double.MAX_VALUE);

        // 3. Data Fine
        DatePicker datePickerFine = new DatePicker();
        datePickerFine.setPromptText("Seleziona data di fine");
        datePickerFine.setMaxWidth(Double.MAX_VALUE);

        // Posizionamento nella griglia (Colonna, Riga)
        grid.add(new Label("Descrizione:"), 0, 0);
        grid.add(txtDescrizione, 1, 0);

        grid.add(new Label("Data Inizio:"), 0, 1);
        grid.add(datePickerInizio, 1, 1);

        grid.add(new Label("Data Fine:"), 0, 2);
        grid.add(datePickerFine, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Abilita il tasto Salva solo se la descrizione non è vuota e entrambe le date sono scelte
        var btnSalvaNode = dialog.getDialogPane().lookupButton(btnSalva);
        Runnable validaInput = () -> {
            boolean descOk = !txtDescrizione.getText().trim().isEmpty();
            boolean datePresenti = datePickerInizio.getValue() != null && datePickerFine.getValue() != null;
            boolean dateCoerenti = datePresenti && !datePickerFine.getValue().isBefore(datePickerInizio.getValue());

            btnSalvaNode.setDisable(!descOk || !dateCoerenti);
        };

        // Validazione dinamica alla modifica dei campi
        btnSalvaNode.setDisable(true);
        txtDescrizione.textProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerInizio.valueProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerFine.valueProperty().addListener((o, prev, cur) -> validaInput.run());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnSalva) {
            String descrizione = txtDescrizione.getText().trim();

            // Conversione da LocalDate a java.util.Date
            Date dataInizio = Date.from(datePickerInizio.getValue()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            Date dataFine = Date.from(datePickerFine.getValue()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            // Logica di salvataggio o aggiunta (es. paziente.getTerapie().add(...) o GestoreFile)
            paziente.inserisciCondizione(new Condizione(descrizione,dataInizio,dataFine));
        }
    }

    @FXML
    private void inserisciDatiAssunzione(ActionEvent evento) throws IOException {
        if (paziente == null) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Nessun paziente selezionato.");
            return;
        }

        List<Terapia> terapieDisponibili = paziente.getTerapie();

        if (terapieDisponibili == null || terapieDisponibili.isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione", "Il paziente non ha alcuna terapia prescritta attiva.");
            return;
        }

        Dialog<Assunzione> dialog = new Dialog<>();
        dialog.setTitle("Nuova Assunzione");
        dialog.setHeaderText("Registra una nuova assunzione per la terapia");

        ButtonType btnRegistra = new ButtonType("Registra", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRegistra, ButtonType.CANCEL);

        ComboBox<Terapia> comboTerapie = new ComboBox<>();
        comboTerapie.getItems().addAll(terapieDisponibili);
        comboTerapie.getSelectionModel().selectFirst();

        comboTerapie.setConverter(new StringConverter<Terapia>() {
            @Override
            public String toString(Terapia t) {
                if (t == null) return "";
                String nomeFarmaco = (t.getFarmaco() != null) ? t.getFarmaco().getNomeFarmaco() : "Farmaco sconosciuto";
                String restante = " "+t.getIndicazioni()+" "+t.getAssunzioniGG();
                return nomeFarmaco+restante;
            }

            @Override
            public Terapia fromString(String string) {
                return null;
            }
        });

        Spinner<Integer> spinnerQuantita = new Spinner<>(1, 100, 1);
        spinnerQuantita.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        grid.add(new Label("Terapia / Farmaco:"), 0, 0);
        grid.add(comboTerapie, 1, 0);
        grid.add(new Label("Quantità:"), 0, 1);
        grid.add(spinnerQuantita, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnRegistra) {
                Terapia terapiaScelta = comboTerapie.getValue();
                int quantita = spinnerQuantita.getValue();
                Timestamp adesso = new Timestamp(System.currentTimeMillis());

                return new Assunzione(adesso, quantita, terapiaScelta);
            }
            return null;
        });

        Optional<Assunzione> risultato = dialog.showAndWait();

        risultato.ifPresent(nuovaAssunzione -> {
            boolean inserita = paziente.inserisciAssunzione(nuovaAssunzione);
            if (inserita) {
                mostraAlert(Alert.AlertType.INFORMATION, "Esito", "Assunzione registrata con successo!");
            } else {
                mostraAlert(Alert.AlertType.ERROR, "Non Conforme",
                        "Assunzione non conforme alle prescrizioni della terapia.");
            }
        });
    }


    @FXML
    private void inviaMail(ActionEvent evento) {
        if (paziente == null) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Nessun paziente selezionato.");
            return;
        }

        Diabetologo medico = paziente.getDiabetologoRiferimento();
        if (medico == null || medico.getEmail() == null || medico.getEmail().trim().isEmpty()) {
            mostraAlert(Alert.AlertType.ERROR, "Destinatario non valido", "Nessun medico di riferimento o email mancante.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Contatta Medico Curante");
        dialog.setHeaderText("Invia un messaggio al Dr. " + medico.getNome() + " " + medico.getCognome());

        ButtonType btnInvia = new ButtonType("Invia Mail", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnInvia, ButtonType.CANCEL);

        TextArea txtMessaggio = new TextArea();
        txtMessaggio.setPromptText("Scrivi qui il messaggio per il tuo medico...");
        txtMessaggio.setWrapText(true);
        txtMessaggio.setPrefRowCount(7);
        txtMessaggio.setPrefColumnCount(30);

        VBox contenuto = new VBox(10, new Label("Testo della mail:"), txtMessaggio);
        contenuto.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(contenuto);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnInvia) {
                return txtMessaggio.getText().trim();
            }
            return null;
        });

        Optional<String> testoMessaggio = dialog.showAndWait();

        testoMessaggio.ifPresent(messaggio -> {
            if (messaggio.isEmpty()) {
                mostraAlert(Alert.AlertType.WARNING, "Messaggio vuoto", "Il testo della mail non può essere vuoto.");
                return;
            }

            String oggetto = "Comunicazione da paziente: " + paziente.getNome() + " " + paziente.getCognome()+"'"
                    +txtMessaggio.getText()+"'";

            Notifica notifica = new Notifica(paziente, paziente.getDiabetologoRiferimento(),oggetto,LivelloPericolo.BASSO);
            try {
                GestoreFile.scriviNotifica(notifica);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mostraAlert(Alert.AlertType.INFORMATION, "Invio Riuscito",
                    "Email inviata al Dr. " + medico.getCognome() + " (" + medico.getEmail() + ")");
        });
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}

