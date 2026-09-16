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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ControlloreDiabetologo {

    private Diabetologo diabetologo;
    InterfacciaFacciata f = new Facciata();

    //CONTROLLATA
    public void settaDatiDiabetologo(Diabetologo diabetologo, InterfacciaFacciata f) throws IOException {
        this.diabetologo = diabetologo;
        this.f = f;
        f.verificaAderenzaTerapie(this.diabetologo);
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

    //cerca il paziente usando il nome e il cognome nella textBox CONTROLLATA
    @FXML
    private void cercaPaziente(ActionEvent evento) throws IOException {
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        Paziente paziente = f.cercaPazientePerNomeCognome(nome,cognome);

        if (paziente != null) {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Dettagli Paziente - Andamento Glicemico");
            alert.setHeaderText("Paziente: " + paziente.getNome() + " " + paziente.getCognome());

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Data e Ora");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Glicemia (mg/dL)");
            yAxis.setAutoRanging(true);

            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle("Andamento Glicemico (Ultimi 7 Giorni)");
            chart.setPrefSize(600, 350);
            chart.setAnimated(false);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Glicemia");

            List<Rilevazione> rilevazioniRecenti = f.ottieniRilevazioni(paziente, LocalDateTime.now().minusDays(7));

            for (Rilevazione r : rilevazioniRecenti) {
                String dataFormattata = r.getMomentoRilevazione().format(formatter);
                serie.getData().add(new XYChart.Data<>(dataFormattata, r.getLivelloGlicemia()));
            }

            chart.getData().add(serie);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));

            if (rilevazioniRecenti.isEmpty()) {
                layout.getChildren().add(new Label("Nessuna rilevazione registrata negli ultimi 7 giorni."));
            } else {
                layout.getChildren().add(chart);
            }

            alert.getDialogPane().setContent(layout);

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
            alert.setContentText("Nessun paziente trovato con il nome e cognome specificati.");
            alert.showAndWait();
        }
    }

    //bottone per fare il logout CONTROLLATA
    @FXML
    private void faiLogout(ActionEvent evento) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/formLogin.fxml"));
        Parent root = loader.load();
        ControlloreLogin controller = loader.getController();
        controller.settaDatiLogin(this.f);
        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(root));
        loginStage.show();
        Stage currentStage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
        currentStage.close();
    }

    //mostra le terapie CONTROLLATA
    public void mostraTerapieConAzioni(Paziente paziente, Diabetologo medicoLoggato) {
        List<String> logMomentaneo = new ArrayList<>();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Terapie di " + paziente.getNome() + " " + paziente.getCognome());

        ArrayList<Terapia> copiaTerapie = new ArrayList<>();
        if (f.ottieniTerapie(paziente) != null) {
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
                            String logModifica = f.rigaLog(medicoLoggato," modifica terapia del paziente " + paziente.getCodiceFiscale()
                                    + " con farmaco: " + t.getFarmaco().getNomeFarmaco()
                                    + " settandoli a quantita: " + t.getQuantita()
                                    + ", assunzioniGG: " + t.getAssunzioniGG()
                                    + ", indicazioni: " + t.getIndicazioni());
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
                String logAggiunta = f.rigaLog(medicoLoggato," aggiunge terapia al paziente " + paziente.getCodiceFiscale()
                        + " con farmaco: " + t.getFarmaco().getNomeFarmaco()
                        + ", quantita: " + t.getQuantita()
                        + ", assunzioniGG: " + t.getAssunzioniGG()
                        + ", indicazioni: " + t.getIndicazioni());
                logMomentaneo.add(logAggiunta);
            }
        });

        Button btnConfermaCambiamenti = new Button("Conferma Cambiamenti");
        btnConfermaCambiamenti.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        btnConfermaCambiamenti.setOnAction(event -> {
            try {
                if(f.settaTerapie(new ArrayList<Terapia>(terapieOsservabili),paziente,logMomentaneo)) {
                    Alert alertSuccesso = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccesso.setTitle("Salvataggio");
                    alertSuccesso.setHeaderText(null);
                    alertSuccesso.setContentText("Modifiche alle terapie salvate con successo!");
                    alertSuccesso.showAndWait();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    //mostra form per modifcare/aggiungere terapia CONTROLLATA
    private Optional<Terapia> apriFormTerapia(Terapia terapiaEsistente, Diabetologo medicoLoggato) {
        Dialog<Terapia> dialog = new Dialog<>();
        boolean isModifica = (terapiaEsistente != null);

        dialog.setTitle(isModifica ? "Modifica Terapia" : "Nuova Terapia");
        dialog.setHeaderText(isModifica ? "Aggiorna i parametri della terapia" : "Compila i dati per la nuova terapia");

        ButtonType btnSalvaForm = new ButtonType(isModifica ? "Applica" : "Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvaForm, ButtonType.CANCEL);

        ComboBox<Farmaco> cmbFarmaci = new ComboBox<>();
        try {
            ArrayList<Farmaco> listaFarmaci = f.caricaTuttiFarmaci();
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
                    f.modificaDatiTerapia(terapiaEsistente, assunzioni, indicazioni, quantita, farmacoScelto, medicoLoggato);
                    return terapiaEsistente;
                } else {
                    return new Terapia(assunzioni, indicazioni, quantita, farmacoScelto, medicoLoggato);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    //cariche le notifiche del diabetologo loggato CONTROLLATO
    private void caricaNotifiche() {
        try {
            if (diabetologo == null) {
                return;
            }

            String cf = f.ottieniCf(diabetologo);
            var notifiche = f.ottieniNotifiche(cf);
            listViewListaNotifiche.getItems().clear();
            if (notifiche != null && !notifiche.isEmpty()) {
                listViewListaNotifiche.getItems().addAll(notifiche);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //modifica i dati e alva su log CONTROLLATA
    private void modificaDatiPaziente(Paziente paziente) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifica Paziente");
        dialog.setHeaderText("Inserisci una breve descrizione");

        ButtonType btnSalva = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnAnnulla);

        TextArea txtDescrizione = new TextArea();
        txtDescrizione.setPromptText("Inserisci qui la descrizione...");
        txtDescrizione.setWrapText(true);
        txtDescrizione.setPrefWidth(500);
        txtDescrizione.setPrefHeight(200);

        if (f.ottieniBreveDesc(paziente) != null) {
            txtDescrizione.setText(paziente.getBreveDescrizione());
        }

        VBox content = new VBox(10, new Label("Descrizione:"), txtDescrizione);
        VBox.setVgrow(txtDescrizione, Priority.ALWAYS);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.setResizable(true);

        var btnSalvaNode = dialog.getDialogPane().lookupButton(btnSalva);
        btnSalvaNode.setDisable(txtDescrizione.getText().trim().isEmpty());

        txtDescrizione.textProperty().addListener((obs, oldVal, newVal) -> {
            btnSalvaNode.setDisable(newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalva) {
                return txtDescrizione.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            String descrizione = result.get();

            f.settaBreveDes(paziente,descrizione);

            String modifica = f.ottieniCf(diabetologo) + " modifica note/descrizione paziente "
                    + paziente.getCodiceFiscale() + ": \"" + descrizione + "\"";

            f.scriviLog(modifica);
        }
    }

    //CONTROLLATA
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
        f.eliminaNotifica(notificaSelezionata);
    }

    //CONTROLLATA
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

            try {
                if(f.inviaMail(email,diabetologo,messaggio,LivelloPericolo.MODERATO)){
                    mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Email inviata correttamente a " + email);
                }
                else{
                    mostraAlert(Alert.AlertType.ERROR, "Errore", "Indirizzo non trovato");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //CONTROLLATA
    private void mostraAlert(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
