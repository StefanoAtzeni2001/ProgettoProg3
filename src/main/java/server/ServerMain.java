package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class ServerMain extends Application{

    @Override
    public void start(Stage stage) throws IOException {

        URL serverUrl = ServerMain.class.getResource("/ServerView/server.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(serverUrl);
        Scene scene = new Scene(fxmlLoader.load(), 300, 500);
        stage.setTitle("Email Server");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}

