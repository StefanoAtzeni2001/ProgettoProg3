package client.controller;

import client.model.ClientModel;
import shared.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class ListViewController {
    public AnchorPane listPane;
    @FXML
    private ListView<Email> lstEmails;//Observer
    private ClientModel model;
    public void initModel(ClientModel model) {
        if (this.model != null) {// ensure model is only set once:
            throw new IllegalStateException("[CLIENT] Model can only be initialized once");
        }
        this.model = model;
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::onListClick);
        model.selectedEmailProperty().bind(lstEmails.getSelectionModel().selectedItemProperty());
    }

    private void onListClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {//DoubleClick
                onListDoubleClick();
            } else {
                //System.out.println(model.getSelectedEmail());//Click
            }
        }
    }

    protected void onListDoubleClick() {
        try {
            //loading and initializing readView and readViwController
            URL readViewUrl = getClass().getResource("/ClientView/readView.fxml");
            FXMLLoader readLoader = new FXMLLoader(readViewUrl);
            AnchorPane pane=readLoader.load();
            ReadViewController readController = readLoader.getController();
            readController.initModel(model);

            Scene scene=listPane.getScene() ; //get reference to main scene
            ((Pane) scene.lookup("#rootPane")).getChildren().setAll(pane);//switching pane
            scene.lookup("#btnBack").setVisible(true);
        } catch (IOException e) {
            System.out.println("[CLIENT] GUI Error");
        }
    }


}
