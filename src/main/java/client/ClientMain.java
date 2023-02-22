package client;

import client.controller.MainViewController;
import client.model.ClientModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parameters params = getParameters();
        if(params!=null) {
            List<String> list = params.getRaw();
            ClientModel model = new ClientModel(list.get(0));

            URL mainViewUrl = ClientMain.class.getResource("/ClientView/mainView.fxml");
            FXMLLoader mainLoader = new FXMLLoader(mainViewUrl);
            Parent par=mainLoader.load();
            MainViewController mainController = mainLoader.getController();
            mainController.initModel(model,stage);


            Scene scene = new Scene(par, 800, 500);
            stage.setTitle("Email Client");
            stage.setScene(scene);
            stage.show();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}