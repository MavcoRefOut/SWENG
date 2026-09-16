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

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//gestore della form del paziente
public class ControllorePaziente {

    private InterfacciaFacciata f;

    private Paziente paziente;

    //CONTROLLATA
    public void settaDatiPaziente(Paziente paziente, InterfacciaFacciata f) throws IOException {
        this.paziente = paziente;
        f.verificaAderenze(paziente,false);
        this.f = f;
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

    //per salvare dati quando si chiude la finestra CONTROLLATA
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnRilevazione.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    f.salvaPaziente(paziente);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    //metodo per inserire una rilevazione CONTROLLATA
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

        //scelta pre / post pasto
        CheckBox chkBooleano = new CheckBox("Pre Pasto");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Valore:"), 0, 0);
        grid.add(txtNumero, 1, 0);
        grid.add(new Label("Opzione:"), 0, 1);
        grid.add(chkBooleano, 1, 1);

        //visualizzazione dati nella form
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> risultato = dialog.showAndWait();

        //controllo inserimento dati
        if (risultato.isPresent() && risultato.get() == btnSalva) {
            String testo = txtNumero.getText().trim();
            if (!testo.isEmpty()) {
                double valore = Double.parseDouble(testo);
                boolean opzioneSelezionata = chkBooleano.isSelected();
                LocalDateTime ora = LocalDateTime.now();
                //inserimento rilevazione nel paziente
                f.inserisciRilevazione(paziente, new Rilevazione(opzioneSelezionata,ora,(int)valore));
            }
        }
    }

    //CONTROLLATA
    @FXML
    private void faiLogout(ActionEvent evento) throws IOException{
        f.salvaPaziente(paziente);
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

    //CONTROLLATA, caricamento notifiche ricevute 
    private void caricaNotifiche(){
        try {
            if (paziente == null) {
                return;
            }

            String cf = paziente.getCodiceFiscale();
            var notifiche = f.ottieniNotifiche(cf);
            listViewNotifiche.getItems().clear();
            if (notifiche != null && !notifiche.isEmpty()) {
                listViewNotifiche.getItems().addAll(notifiche);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //CONTROLLATA
    @FXML
    private void eliminaNotifica(ActionEvent evento){
        String notificaSelezionata = listViewNotifiche.getSelectionModel().getSelectedItem();
        if (notificaSelezionata == null) {
            mostraAlert(Alert.AlertType.WARNING,"Nessuna selezione","Nessuna notifica è stata selezionata");
            return;
        }
        listViewNotifiche.getItems().remove(notificaSelezionata);
        f.eliminaNotifica(notificaSelezionata);
    }

    //funzione per inserire una nuova segnalazione CONTROLLATA
    @FXML
    private void inserisciDatiSegnalazione(ActionEvent evento){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova Segnalazione");
        dialog.setHeaderText("Inserisci un sintomo, una patologia o una terapia concomitante");

        //bottoni per confermare o annullare l'inserimento della segnalazione
        ButtonType btnSalva = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnAnnulla);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        //scelta tra sintomo, patologia o terapia concomitante
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Sintomo", "Patologia", "Terapia Concomitante");
        cmbTipo.setValue("Sintomo");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        //inserimento nome sintomo
        Label lblNome = new Label("Nome Sintomo:");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Es. Nausea, Spossatezza...");
        GridPane.setHgrow(txtNome, Priority.ALWAYS);

        //inserimento data inizio
        DatePicker datePickerInizio = new DatePicker(LocalDate.now());
        datePickerInizio.setMaxWidth(Double.MAX_VALUE);

        //inserimento data fine
        DatePicker datePickerFine = new DatePicker();
        datePickerFine.setPromptText("Lascia vuoto se ancora in corso");
        datePickerFine.setMaxWidth(Double.MAX_VALUE);

        //inserimento note aggiuntive
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

        //visualizzazione informazioni e scelte del paziente
        dialog.getDialogPane().setContent(grid);

        //controllo della scelta del paziente riguardante la segnalazione
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
            //verifica coerenza date
            if (datePickerFine.getValue() != null && datePickerInizio.getValue() != null) {
                dateCoerenti = !datePickerFine.getValue().isBefore(datePickerInizio.getValue());
            }

            btnSalvaNode.setDisable(!nomeOk || !dataInizioOk || !dateCoerenti);
        };

        txtNome.textProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerInizio.valueProperty().addListener((o, prev, cur) -> validaInput.run());
        datePickerFine.valueProperty().addListener((o, prev, cur) -> validaInput.run());

        //salvataggio dati inseriti
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnSalva) {
            String tipo = cmbTipo.getValue();
            String nomeVal = txtNome.getText().trim();
            String noteVal = txtDescrizione.getText().trim();
            LocalDate dataInizio = datePickerInizio.getValue();
            LocalDate dataFine = datePickerFine.getValue();

            CreatoreSegnalazione creatore = null;

            //creazione segnalazione specifica con dati inseriti tramite factory method
            switch (tipo) {
                case "Sintomo":
                    creatore = new CreatoreSintomo();
                    break;

                case "Patologia":
                    creatore = new CreatorePatologia();
                    break;

                case "Terapia Concomitante":
                    creatore = new CreatoreTerapiaConcomitante();
                    break;
            }
            //se è stato istanziato il creatore allora viene aggiunta la segnalazione al paziente
            if(creatore!=null){
                Boolean ris = f.aggiungiSegnalazione(paziente,creatore,dataInizio,dataFine,noteVal,nomeVal);
                if(ris){
                    mostraAlert(Alert.AlertType.CONFIRMATION,"Segnalazione Registrata","La registrazione è stata salvata");
                }
            }
        }
    }

    //CONTROLLATA, inserimento dati assunzione di farmaci da parte del paziente
    @FXML
    private void inserisciDatiAssunzione(ActionEvent evento) throws IOException {
        if (paziente == null) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Nessun paziente selezionato.");
            return;
        }

        List<Terapia> terapieDisponibili = paziente.getTerapie();

        //avviso se non sono presenti terapie del paziente
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

        //toString della lista di farmaci da assumere con indicazione e assunzioni giornaliere
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

        //inserimento quantitàa assunta
        Spinner<Integer> spinnerQuantita = new Spinner<>(1, 100, 1);
        spinnerQuantita.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        grid.add(new Label("Terapia:"), 0, 0);
        grid.add(comboTerapie, 1, 0);
        grid.add(new Label("Quantità:"), 0, 1);
        grid.add(spinnerQuantita, 1, 1);

        //visualizzazione informazioni sull'assunzione a schermo
        dialog.getDialogPane().setContent(grid);

        //quando viene premuto il btnRegistra viene salvata l'assunzione con le sue info
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

        //viene inserita l'assunzione con controllo d'eccezione
        risultato.ifPresent(nuovaAssunzione -> {
                try {
                    f.inserisciAssunzione(paziente, nuovaAssunzione);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    //CONTROLLATA, invio mail, analogo a quello del diabetologo adattato al paziente
    @FXML
    private void inviaMail(ActionEvent evento) {
        if (paziente == null) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Nessun paziente selezionato.");
            return;
        }

        //ottenimento diabetologo di riferimento
        Diabetologo medico = f.ottieniDiabetologo(paziente);
        if (medico == null || medico.getEmail() == null || medico.getEmail().trim().isEmpty()) {
            mostraAlert(Alert.AlertType.ERROR, "Destinatario non valido", "Nessun medico di riferimento o email mancante.");
            return;
        }

        //informazioni sul destiantario
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Contatta Medico Curante");
        dialog.setHeaderText("Invia un messaggio al Dr. " + medico.getNome() + " " + medico.getCognome());

        //bottone d'invio email
        ButtonType btnInvia = new ButtonType("Invia Mail", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnInvia, ButtonType.CANCEL);

        //informazioni sul testo del messaggio
        TextArea txtMessaggio = new TextArea();
        txtMessaggio.setPromptText("Scrivi qui il messaggio per il tuo medico...");
        txtMessaggio.setWrapText(true);
        txtMessaggio.setPrefRowCount(7);
        txtMessaggio.setPrefColumnCount(30);
        
        //area per l'inserimento del testo del messaggio
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

        //controllo sull'inserimento del messaggio
        testoMessaggio.ifPresent(messaggio -> {
            if (messaggio.isEmpty()) {
                mostraAlert(Alert.AlertType.WARNING, "Messaggio vuoto", "Il testo della mail non può essere vuoto.");
                return;
            }

            String oggetto = "Comunicazione da paziente: " + paziente.getNome() + " " + paziente.getCognome()+"'"
                    +txtMessaggio.getText()+"'";

            //generazione della notifica per il diabetologo
            Notifica notifica = new Notifica(paziente, paziente.getDiabetologoRiferimento(),oggetto,LivelloPericolo.BASSO);
            try {
                f.scriviNotifica(notifica);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mostraAlert(Alert.AlertType.INFORMATION, "Invio Riuscito",
                    "Email inviata al Dr. " + medico.getCognome() + " (" + medico.getEmail() + ")");
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

