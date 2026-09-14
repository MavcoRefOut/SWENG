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
    private Button btnSegnalazione;

    @FXML
    private Button btnAssunzione;

    @FXML
    private Button btnInviaMail;

    //per salvare dati quando si chiude la finestra
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Rilevazione Glicemica");
        dialog.setHeaderText("Inserisci il valore numerico e lo stato:");
        ButtonType btnSalva = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, ButtonType.CANCEL);

        TextField txtNumero = new TextField();
        txtNumero.textProperty().addListener((obs, vecchio, nuovo) -> {
            if (!nuovo.matches("\\d*(\\.\\d*)?")) {
                txtNumero.setText(vecchio);
            }
        });

        CheckBox chkBooleano = new CheckBox("Pre Pasto");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Valore:"), 0, 0);
        grid.add(txtNumero, 1, 0);
        grid.add(new Label("Opzione:"), 0, 1);
        grid.add(chkBooleano, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> risultato = dialog.showAndWait();

        if (risultato.isPresent() && risultato.get() == btnSalva) {
            String testo = txtNumero.getText().trim();
            if (!testo.isEmpty()) {
                double valore = Double.parseDouble(testo);
                boolean opzioneSelezionata = chkBooleano.isSelected();
                LocalDateTime ora = LocalDateTime.now();
                paziente.inserisciRilevazione(new Rilevazione(opzioneSelezionata,ora,(int)valore));
            }
        }
    }

    @FXML
    private void faiLogout(ActionEvent evento) throws IOException{
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

    //funzione per inserire una nuova segnalazione
    @FXML
    private void inserisciDatiSegnalazione(ActionEvent evento){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova Segnalazione");
        dialog.setHeaderText("Inserisci un sintomo, una patologia o una terapia concomitante");

        ButtonType btnSalva = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnAnnulla);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Sintomo", "Patologia", "Terapia Concomitante");
        cmbTipo.setValue("Sintomo");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        Label lblNome = new Label("Nome Sintomo:");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Es. Nausea, Spossatezza...");
        GridPane.setHgrow(txtNome, Priority.ALWAYS);

        DatePicker datePickerInizio = new DatePicker(LocalDate.now());
        datePickerInizio.setMaxWidth(Double.MAX_VALUE);

        DatePicker datePickerFine = new DatePicker();
        datePickerFine.setPromptText("Lascia vuoto se ancora in corso");
        datePickerFine.setMaxWidth(Double.MAX_VALUE);

        TextArea txtDescrizione = new TextArea();
        txtDescrizione.setPromptText("Eventuali note o dettagli aggiuntivi...");
        txtDescrizione.setPrefRowCount(2);
        txtDescrizione.setWrapText(true);

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(cmbTipo, 1, 0);

        grid.add(lblNome, 0, 1);
        grid.add(txtNome, 1, 1);

        grid.add(new Label("Data Inizio:"), 0, 2);
        grid.add(datePickerInizio, 1, 2);

        grid.add(new Label("Data Fine:"), 0, 3);
        grid.add(datePickerFine, 1, 3);

        grid.add(new Label("Descrizione/Note:"), 0, 4);
        grid.add(txtDescrizione, 1, 4);

        dialog.getDialogPane().setContent(grid);

        cmbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Sintomo".equals(newVal)) {
                lblNome.setText("Nome Sintomo:");
                txtNome.setPromptText("Es. Cefalea, Nausea...");
            } else if ("Patologia".equals(newVal)) {
                lblNome.setText("Nome Patologia:");
                txtNome.setPromptText("Es. Ipertensione, Celiachia...");
            } else {
                lblNome.setText("Nome Farmaco:");
                txtNome.setPromptText("Es. Ramipril, Aspirina...");
            }
        });

        var btnSalvaNode = dialog.getDialogPane().lookupButton(btnSalva);
        btnSalvaNode.setDisable(true);

        Runnable validaInput = () -> {
            boolean nomeOk = !txtNome.getText().trim().isEmpty();
            boolean dataInizioOk = datePickerInizio.getValue() != null;

            boolean dateCoerenti = true;
            if (datePickerFine.getValue() != null && datePickerInizio.getValue() != null) {
                dateCoerenti = !datePickerFine.getValue().isBefore(datePickerInizio.getValue());
            }

            btnSalvaNode.setDisable(!nomeOk || !dataInizioOk || !dateCoerenti);
        };

        txtNome.textProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerInizio.valueProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerFine.valueProperty().addListener((o, prev, cur) -> validaInput.run());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnSalva) {
            String tipo = cmbTipo.getValue();
            String nomeVal = txtNome.getText().trim();
            String noteVal = txtDescrizione.getText().trim();
            LocalDate dataInizio = datePickerInizio.getValue();
            LocalDate dataFine = datePickerFine.getValue();

            Segnalazione nuovaSegnalazione;

            switch (tipo) {
                case "Sintomo":
                    nuovaSegnalazione = new Sintomo(dataInizio, dataFine, noteVal, nomeVal);
                    break;

                case "Patologia":
                    nuovaSegnalazione = new Patologia(dataInizio, dataFine, noteVal, nomeVal);
                    break;

                case "Terapia Concomitante":
                    nuovaSegnalazione = new TerapiaConcomitante(dataInizio, dataFine, noteVal, nomeVal);
                    break;

                default:
                    nuovaSegnalazione = null;
            }

            if (nuovaSegnalazione != null) {
                paziente.inserisciSegnalazione(nuovaSegnalazione);

                String logMsg = paziente.getCodiceFiscale() + " ha inserito una segnalazione di tipo "
                        + tipo + " [" + nomeVal + "]";
                GestoreFile.scriviLog(logMsg);
            }
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

