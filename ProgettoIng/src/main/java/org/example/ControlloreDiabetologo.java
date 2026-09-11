package org.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.classiEffettive.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ControlloreDiabetologo {
    private Diabetologo diabetologo;

    public void SettaDatiMedico(Diabetologo diabetologo) throws IOException {
        this.diabetologo = diabetologo;
        List<Paziente> listaPazienti = GestoreFile.pazientiPerMedico(diabetologo.getCodiceFiscale());
        for(Paziente p : listaPazienti)
            p.verificaAderenzaTerapie(true);
        caricaNotifiche();
    }

    @FXML
    private Button btnLogout;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCognome;

    @FXML
    private Button btnRicercaPaziente;


    @FXML
    private ListView<String> listViewListaNotifiche;

    @FXML
    private VBox vBoxNotifiche;

    @FXML
    private Button btnEliminaNotifica;

    @FXML
    private Button btnInviaMail;

    //cerca il paziente usando il codice fiscale nella textBox
    @FXML
    private void cercaPaziente(ActionEvent evento) throws IOException {
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        Paziente paziente = GestoreFile.cercaPazientePerNomeCognome(nome,cognome);

        if (paziente != null) {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Dettagli Paziente");
            alert.setHeaderText("Paziente trovato");

            TextArea textArea = new TextArea(paziente.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(450);
            textArea.setPrefHeight(200);
            alert.getDialogPane().setContent(textArea);

            ButtonType btnModifica = new ButtonType("Modifica Dati");
            ButtonType btnTerapie = new ButtonType("Mostra Terapie");
            ButtonType btnChiudi = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(btnModifica, btnTerapie, btnChiudi);

            Optional<ButtonType> risultato = alert.showAndWait();
            if (risultato.isPresent()) {
                if (risultato.get() == btnTerapie) {
                    mostraTerapieConAzioni(paziente, diabetologo);
                } else if (risultato.get() == btnModifica) {
                    modificaDatiPaziente(paziente);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ricerca Paziente");
            alert.setHeaderText(null);
            alert.setContentText("Nessun paziente trovato con il codice fiscale specificato.");
            alert.showAndWait();
        }
    }

    @FXML
    private void faiLogout(ActionEvent evento) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/formLogin.fxml"));
        Parent root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(root));
        loginStage.show();

        Stage currentStage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
        currentStage.close();
    }

    //mostra le terapie
    public void mostraTerapieConAzioni(Paziente paziente, Diabetologo medicoLoggato) {
        List<String> logMomentaneo = new ArrayList<>();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Terapie di " + paziente.getNome() + " " + paziente.getCognome());

        ArrayList<Terapia> copiaTerapie = new ArrayList<>();
        if (paziente.getTerapie() != null) {
            copiaTerapie.addAll(paziente.getTerapie());
        }

        ObservableList<Terapia> terapieOsservabili = FXCollections.observableArrayList(copiaTerapie);
        ListView<Terapia> listView = new ListView<>(terapieOsservabili);

        listView.setCellFactory(param -> new ListCell<Terapia>() {
            private final Label lblDettagli = new Label();
            private final Button btnModifica = new Button("Modifica");
            private final HBox riga = new HBox(10);

            {
                HBox.setHgrow(lblDettagli, Priority.ALWAYS);
                lblDettagli.setMaxWidth(Double.MAX_VALUE);
                lblDettagli.setWrapText(true);

                riga.getChildren().addAll(lblDettagli, btnModifica);
                riga.setAlignment(Pos.CENTER_LEFT);
                riga.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(Terapia terapia, boolean empty) {
                super.updateItem(terapia, empty);

                if (empty || terapia == null) {
                    setGraphic(null);
                } else {
                    lblDettagli.setText(terapia.toString());

                    btnModifica.setOnAction(event -> {
                        Optional<Terapia> modificata = apriFormTerapia(terapia, medicoLoggato);
                        if (modificata.isPresent()) {
                            Terapia t = modificata.get();
                            listView.refresh();
                            String logModifica = medicoLoggato.getCodiceFiscale() + " modifica terapia del paziente " + paziente.getCodiceFiscale()
                                    + " con farmaco: " + t.getFarmaco().getNomeFarmaco()
                                    + " settandoli a quantita: " + t.getQuantita()
                                    + ", assunzioniGG: " + t.getAssunzioniGG()
                                    + ", indicazioni: " + t.getIndicazioni();
                            logMomentaneo.add(logModifica);
                        }
                    });

                    setGraphic(riga);
                }
            }
        });

        Button btnAggiungi = new Button("Aggiungi Nuova Terapia");
        btnAggiungi.setOnAction(event -> {
            Optional<Terapia> nuova = apriFormTerapia(null, medicoLoggato);
            if (nuova.isPresent()) {
                Terapia t = nuova.get();
                terapieOsservabili.add(nuova.get());
                String logAggiunta = medicoLoggato.getCodiceFiscale() + " aggiunge terapia al paziente " + paziente.getCodiceFiscale()
                        + " con farmaco: " + t.getFarmaco().getNomeFarmaco()
                        + ", quantita: " + t.getQuantita()
                        + ", assunzioniGG: " + t.getAssunzioniGG()
                        + ", indicazioni: " + t.getIndicazioni();
                logMomentaneo.add(logAggiunta);
            }
        });

        Button btnConfermaCambiamenti = new Button("Conferma Cambiamenti");
        btnConfermaCambiamenti.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        btnConfermaCambiamenti.setOnAction(event -> {
            paziente.setTerapie(new ArrayList<>(terapieOsservabili));

            try {
                GestoreFile.salvaPaziente(paziente);
                for (String riga : logMomentaneo) {
                    GestoreFile.scriviLog(riga);
                }
                Alert alertSuccesso = new Alert(Alert.AlertType.INFORMATION);
                alertSuccesso.setTitle("Salvataggio");
                alertSuccesso.setHeaderText(null);
                alertSuccesso.setContentText("Modifiche alle terapie salvate con successo!");
                alertSuccesso.showAndWait();

                dialogStage.close();
            } catch (IOException e) {
                Alert alertErrore = new Alert(Alert.AlertType.ERROR);
                alertErrore.setTitle("Errore");
                alertErrore.setHeaderText("Errore durante il salvataggio");
                alertErrore.setContentText(e.getMessage());
                alertErrore.showAndWait();
            }
        });

        Button btnAnnulla = new Button("Annulla");
        btnAnnulla.setOnAction(event -> dialogStage.close());

        HBox boxBottoni = new HBox(10, btnAggiungi, btnAnnulla, btnConfermaCambiamenti);
        boxBottoni.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(12, new Label("Elenco Terapie:"), listView, boxBottoni);
        root.setPadding(new Insets(15));
        root.setPrefSize(680, 440);

        dialogStage.setScene(new Scene(root));
        dialogStage.showAndWait();
    }

    //mostra form per modifcare/aggiungere terapia
    private Optional<Terapia> apriFormTerapia(Terapia terapiaEsistente, Diabetologo medicoLoggato) {
        Dialog<Terapia> dialog = new Dialog<>();
        boolean isModifica = (terapiaEsistente != null);

        dialog.setTitle(isModifica ? "Modifica Terapia" : "Nuova Terapia");
        dialog.setHeaderText(isModifica ? "Aggiorna i parametri della terapia" : "Compila i dati per la nuova terapia");

        ButtonType btnSalvaForm = new ButtonType(isModifica ? "Applica" : "Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvaForm, ButtonType.CANCEL);

        ComboBox<Farmaco> cmbFarmaci = new ComboBox<>();
        try {
            ArrayList<Farmaco> listaFarmaci = GestoreFile.caricaTuttiFarmaci();
            cmbFarmaci.setItems(FXCollections.observableArrayList(listaFarmaci));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cmbFarmaci.setConverter(new StringConverter<Farmaco>() {
            @Override
            public String toString(Farmaco f) {
                return f == null ? "" : f.getNomeFarmaco() + " (" + f.getPrincipioAttivo() + ")";
            }
            @Override
            public Farmaco fromString(String string) { return null; }
        });

        TextField txtQuantita = new TextField();
        TextField txtAssunzioniGG = new TextField();
        TextArea txtIndicazioni = new TextArea();
        txtIndicazioni.setPrefRowCount(3);

        txtQuantita.textProperty().addListener((obs, v, n) -> {
            if (!n.matches("\\d*")) txtQuantita.setText(v);
        });
        txtAssunzioniGG.textProperty().addListener((obs, v, n) -> {
            if (!n.matches("\\d*")) txtAssunzioniGG.setText(v);
        });

        if (isModifica) {
            txtQuantita.setText(String.valueOf(terapiaEsistente.getQuantita()));
            txtAssunzioniGG.setText(String.valueOf(terapiaEsistente.getAssunzioniGG()));
            txtIndicazioni.setText(terapiaEsistente.getIndicazioni() != null ? terapiaEsistente.getIndicazioni() : "");

            if (terapiaEsistente.getFarmaco() != null) {
                for (Farmaco f : cmbFarmaci.getItems()) {
                    if (f.getNomeFarmaco().equalsIgnoreCase(terapiaEsistente.getFarmaco().getNomeFarmaco())) {
                        cmbFarmaci.setValue(f);
                        break;
                    }
                }
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Farmaco:"), 0, 0);
        grid.add(cmbFarmaci, 1, 0);
        grid.add(new Label("Quantità:"), 0, 1);
        grid.add(txtQuantita, 1, 1);
        grid.add(new Label("Assunzioni al giorno:"), 0, 2);
        grid.add(txtAssunzioniGG, 1, 2);
        grid.add(new Label("Indicazioni:"), 0, 3);
        grid.add(txtIndicazioni, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvaForm) {
                Farmaco farmacoScelto = cmbFarmaci.getValue();
                int quantita = txtQuantita.getText().isEmpty() ? 0 : Integer.parseInt(txtQuantita.getText());
                int assunzioni = txtAssunzioniGG.getText().isEmpty() ? 0 : Integer.parseInt(txtAssunzioniGG.getText());
                String indicazioni = txtIndicazioni.getText().trim();

                if (isModifica) {
                    terapiaEsistente.modificaDatiTerapia(assunzioni, indicazioni, quantita, farmacoScelto, medicoLoggato);
                    return terapiaEsistente;
                } else {
                    return new Terapia(assunzioni, indicazioni, quantita, farmacoScelto, medicoLoggato);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    //cariche le notifiche del diabetologo loggato
    private void caricaNotifiche() {
        try {
            if (diabetologo == null) {
                return;
            }

            String cf = diabetologo.getCodiceFiscale();
            var notifiche = GestoreFile.ottieniNotifiche(cf);
            listViewListaNotifiche.getItems().clear();
            if (notifiche != null && !notifiche.isEmpty()) {
                listViewListaNotifiche.getItems().addAll(notifiche);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //modifica i dati e alva su log
    private void modificaDatiPaziente(Paziente paziente) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Dati Paziente");
        dialog.setHeaderText("Aggiorna i dati anagrafici e le credenziali di accesso");

        ButtonType btnSalva = new ButtonType("Salva Modifiche", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnAnnulla);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        TextField txtCF = new TextField(paziente.getCodiceFiscale());
        txtCF.setEditable(false);
        txtCF.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #495057;");

        TextField txtNome = new TextField(paziente.getNome());
        TextField txtCognome = new TextField(paziente.getCognome());

        DatePicker datePickerNascita = new DatePicker();
        if (paziente.getDataNascita() != null) {
            LocalDate dataLocale = paziente.getDataNascita().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            datePickerNascita.setValue(dataLocale);
        }
        datePickerNascita.setMaxWidth(Double.MAX_VALUE);

        TextField txtEmail = new TextField(paziente.getEmail() != null ? paziente.getEmail() : "");
        TextField txtUsername = new TextField(paziente.getUsername());
        TextField txtPassword = new TextField(paziente.getPassword()); // Oppure PasswordField se preferisci mascherarla

        grid.add(new Label("Codice Fiscale:"), 0, 0);
        grid.add(txtCF, 1, 0);

        grid.add(new Label("Nome:"), 0, 1);
        grid.add(txtNome, 1, 1);

        grid.add(new Label("Cognome:"), 0, 2);
        grid.add(txtCognome, 1, 2);

        grid.add(new Label("Data di Nascita:"), 0, 3);
        grid.add(datePickerNascita, 1, 3);

        grid.add(new Label("Email:"), 0, 4);
        grid.add(txtEmail, 1, 4);

        grid.add(new Label("Username:"), 0, 5);
        grid.add(txtUsername, 1, 5);

        grid.add(new Label("Password:"), 0, 6);
        grid.add(txtPassword, 1, 6);

        dialog.getDialogPane().setContent(grid);

        var btnSalvaNode = dialog.getDialogPane().lookupButton(btnSalva);
        Runnable validaCampi = () -> {
            boolean disabilitato = txtNome.getText().trim().isEmpty() ||
                    txtCognome.getText().trim().isEmpty() ||
                    txtUsername.getText().trim().isEmpty() ||
                    txtPassword.getText().trim().isEmpty() ||
                    datePickerNascita.getValue() == null;
            btnSalvaNode.setDisable(disabilitato);
        };

        txtNome.textProperty().addListener((o, prev, cur) -> validaCampi.run());
        txtCognome.textProperty().addListener((o, prev, cur) -> validaCampi.run());
        txtUsername.textProperty().addListener((o, prev, cur) -> validaCampi.run());
        txtPassword.textProperty().addListener((o, prev, cur) -> validaCampi.run());
        datePickerNascita.valueProperty().addListener((o, prev, cur) -> validaCampi.run());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnSalva) {

            Date nuovaDataNascita = Date.from(datePickerNascita.getValue()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            paziente.setNome(txtNome.getText().trim());
            paziente.setCognome(txtCognome.getText().trim());
            paziente.setDataNascita(nuovaDataNascita);
            paziente.setEmail(txtEmail.getText().trim());
            paziente.setUsername(txtUsername.getText().trim());
            paziente.setPassword(txtPassword.getText().trim());

            GestoreFile.salvaPaziente(paziente);
            String modifica = diabetologo.getCodiceFiscale() + " modifica dati paziente " + paziente.getCodiceFiscale()
                    + " settandoli a nome: " + txtNome.getText().trim()
                    + ", cognome: " + txtCognome.getText().trim()
                    + ", dataNascita: " + nuovaDataNascita
                    + ", email: " + txtEmail.getText().trim()
                    + ", username: " + txtUsername.getText().trim()
                    + ", password: " + txtPassword.getText().trim();

            GestoreFile.scriviLog(modifica);
        }
    }

    @FXML
    private void eliminaNotifica(ActionEvent evento){
        String notificaSelezionata = listViewListaNotifiche.getSelectionModel().getSelectedItem();
        if (notificaSelezionata == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna selezione");
            alert.setHeaderText(null);
            alert.setContentText("Seleziona prima una notifica dalla lista prima di eliminarla.");
            alert.showAndWait();
            return;
        }
        listViewListaNotifiche.getItems().remove(notificaSelezionata);
        GestoreFile.eliminaNotifica(notificaSelezionata);
    }

    @FXML
    private void inviaMail(ActionEvent evento) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Invio Email");
        dialog.setHeaderText("Inserisci l'indirizzo email e il messaggio");

        ButtonType btnInvia = new ButtonType("Invia", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnInvia, ButtonType.CANCEL);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("destinatario@esempio.it");

        TextArea txtMessaggio = new TextArea();
        txtMessaggio.setPromptText("Scrivi qui il messaggio...");
        txtMessaggio.setWrapText(true);
        txtMessaggio.setPrefRowCount(6);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        grid.add(new Label("Email:"), 0, 0);
        grid.add(txtEmail, 1, 0);
        grid.add(new Label("Messaggio:"), 0, 1);
        grid.add(txtMessaggio, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnInvia) {
                Map<String, String> risultato = new HashMap<>();
                risultato.put("email", txtEmail.getText().trim());
                risultato.put("messaggio", txtMessaggio.getText().trim());
                return risultato;
            }
            return null;
        });

        Optional<Map<String, String>> input = dialog.showAndWait();

        input.ifPresent(dati -> {
            String email = dati.get("email");
            String messaggio = dati.get("messaggio");

            if (email.isEmpty() || messaggio.isEmpty()) {
                mostraAlert(Alert.AlertType.WARNING, "Dati mancanti", "Compila entrambi i campi prima di procedere.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                mostraAlert(Alert.AlertType.ERROR, "Email non valida", "Inserisci un indirizzo email corretto.");
                return;
            }

            Paziente p = GestoreFile.pazientePerMail(email);
            if(p!=null){
                Notifica notifica = new Notifica(diabetologo,p,messaggio,LivelloPericolo.MODERATO);
                try {
                    GestoreFile.scriviNotifica(notifica);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Email inviata correttamente a " + email);
            }
            else{
                mostraAlert(Alert.AlertType.ERROR, "Errore", "Indirizzo non trovato");
            }


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
