package client.controller;

import client.model.ClientModel;
import Shared.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

public class MainViewController {
    @FXML
    private Button btnBack;
    @FXML
    private Label lblAccount;
    @FXML
    private AnchorPane rootPane;

    private ClientModel model;
    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    

    @FXML
    public void initialize(){
    }
    @FXML
    public void initModel(ClientModel model) {
        if (this.model != null) {// ensure model is only set once:
            throw new IllegalStateException("[CLIENT] Model can only be initialized once");
        }
        this.model = model;
        model.generateEmails(50);
        loadListView();
        lblAccount.textProperty().bind(model.accountProperty());
    }

    @FXML
    public void loadListView(){
            try {
                URL listViewUrl = getClass().getResource("/ClientView/listView.fxml");
                FXMLLoader listLoader = new FXMLLoader(listViewUrl);
                AnchorPane pane=listLoader.load();
                ListViewController listController = listLoader.getController();
                listController.initModel(model);
                rootPane.getChildren().setAll(pane);
                btnBack.setVisible(false);
            }catch(IOException e){System.out.println("[CLIENT] GUI Error");}
    }

    @FXML //Create and initialize the writeView, return the stage (not already shown)
    public Stage loadWriteView() {
        model.setDest("");
        model.setSubject("");
        model.setText("");
        try {
            URL writeViewUrl = getClass().getResource("/ClientView/writeView.fxml");
            FXMLLoader writeLoader = new FXMLLoader(writeViewUrl);
            Parent par = writeLoader.load();
            WriteViewController writeController = writeLoader.getController();
            writeController.initModel(model);
            Scene scene = new Scene(par, 800, 500);
            Stage stage = new Stage();
            stage.setTitle("Write");
            stage.setScene(scene);
            return stage;
        } catch (IOException e) {System.out.println("[CLIENT] GUI Error");return null;}
    }

    public void onBackBtnClick() {
        loadListView();
    }


    //delete selected email from the model and reload the listView
    public void onDeleteBtnClick() {
        if(model.getSelectedEmail()!=null) {
            model.deleteEmail(model.getSelectedEmail());
            loadListView();
        }
    }


    //show the writeView but "dest" and "subject" are already compiled and not editable
    public void onReplyBtnClick() {
        if(model.getSelectedEmail()!=null) {
            Stage stage = loadWriteView();
            Scene scene = stage.getScene();
            scene.lookup("#txtTo").setDisable(true);
            scene.lookup("#txtSubject").setDisable(true);
            model.setDest(model.getSelectedEmail().getSender());
            model.setSubject("Re: " + model.getSelectedEmail().getSubject());
            stage.show();
        }
    }


    //show the writeView but "text" and "subject" are already compiled and not editable
    public void onForwardBtnClick() {
        if(model.getSelectedEmail()!=null) {
            Stage stage = loadWriteView();
            Scene scene = stage.getScene();
            scene.lookup("#txtText").setDisable(true);
            scene.lookup("#txtSubject").setDisable(true);
            model.setText("[Inoltrato da: " + model.getSelectedEmail().getSender() + "]\n" + model.getSelectedEmail().getText());
            model.setSubject("Fwd: " + model.getSelectedEmail().getSubject());
            stage.show();
        }
    }


    //show the writeView
    public void onWriteBtnClick()  {
        loadWriteView().show();
    }

}
