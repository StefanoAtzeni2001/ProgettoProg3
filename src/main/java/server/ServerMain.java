package server;

import client.ClientMain;
import client.controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.controller.ServerController;
import server.model.ServerModel;

import java.io.IOException;
import java.net.URL;


public class ServerMain extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        URL serverViewUrl = ClientMain.class.getResource("/ServerView/server.fxml");
        FXMLLoader serverLoader = new FXMLLoader(serverViewUrl);
        Parent par=serverLoader.load();
        ServerController serverController = serverLoader.getController();
        ServerModel model=new ServerModel();
        serverController.initModel(model,stage);

        Scene scene = new Scene(par, 300, 500);
        stage.setTitle("Email Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

