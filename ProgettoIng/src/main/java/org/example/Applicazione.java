package org.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class Applicazione extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Applicazione.class.getResource("/formLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ControlloreLogin controller = fxmlLoader.getController();
        controller.settaDatiLogin(new Facciata());
        stage.setTitle("Finestra Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
