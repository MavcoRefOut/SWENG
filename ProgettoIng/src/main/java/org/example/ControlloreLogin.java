package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControlloreLogin {

    private InterfacciaFacciata f;
    @FXML
    private Button btnLogin;

    @FXML
    private TextField textUsername;

    @FXML
    private PasswordField textPassword;

    public void settaDatiLogin(InterfacciaFacciata f){
        this.f = f;
    }

    @FXML
    private void FaiLogin(ActionEvent event) {
        String username = textUsername.getText().trim();
        String password = textPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostraMessErrore("Compilare sia username che password.");
            return;
        }

        try {
            Persona utente = f.cercaPersona(username, password);

            if (utente == null) {
                mostraMessErrore("Credenziali sbagliate.");
                return;
            }

            FXMLLoader loader;
            Parent root;

            if (utente instanceof Diabetologo) {
                loader = new FXMLLoader(getClass().getResource("/formDiabetologo.fxml"));
                root = loader.load();

                ControlloreDiabetologo controller = loader.getController();
                controller.settaDatiDiabetologo((Diabetologo) utente, f);
            } else if (utente instanceof Paziente) {
                loader = new FXMLLoader(getClass().getResource("/formPaziente.fxml"));
                root = loader.load();

                ControllorePaziente controller = loader.getController();
                controller.settaDatiPaziente((Paziente) utente, f);
            } else {
                mostraMessErrore("Ruolo utente non riconosciuto.");
                return;
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostraMessErrore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    private void mostraMessErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Finestra di Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}

