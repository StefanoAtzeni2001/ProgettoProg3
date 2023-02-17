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

    @FXML
    private ListView<Email> lstEmails;//Observer

    private ClientModel model;
    private Email emptyEmail;
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
            }catch(IOException e){System.out.println("[CLIENT] GUI Error");
            }
        }

    public void onBackBtnClick(ActionEvent actionEvent) {
        loadListView();
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        if(model.getSelectedEmail()!=null) {
            model.deleteEmail(model.getSelectedEmail());
            loadListView();
        }
    }

    public void onReplyBtnClick(ActionEvent actionEvent) {
    }



    public void onForwardBtnClick(ActionEvent actionEvent) {
    }

    public void onWriteBtnClick() throws IOException {
        Stage stage=new Stage();
        URL writeViewUrl = getClass().getResource("/ClientView/writeView.fxml");
        FXMLLoader writeLoader = new FXMLLoader(writeViewUrl);
        Parent par=writeLoader.load();
        WriteViewController writeController = writeLoader.getController();
        writeController.initModel(model);
        Scene scene = new Scene(par, 800, 500);
        stage.setTitle("Write");
        stage.setScene(scene);
        stage.show();
    }

}
