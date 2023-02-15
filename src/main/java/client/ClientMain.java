package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL clientUrl = ClientMain.class.getResource("/client.fxml");
        System.out.println(clientUrl);
        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}