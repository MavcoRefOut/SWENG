package org.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class Applicazione extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        // Carica la schermata definita nel file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Applicazione.class.getResource("/formLogin.fxml"));

        // Crea la scena con le dimensioni ricavate dal file FXML
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Finestra Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
