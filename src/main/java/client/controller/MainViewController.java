package client.controller;

import client.model.ClientModel;
import client.model.Connection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.Message;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static client.controller.Dialogs.showErrorDialog;
import static client.controller.Dialogs.showInfoDialog;

public class MainViewController {
    @FXML
    private Button btnBack;
    @FXML
    private Label lblAccount;
    @FXML
    private AnchorPane rootPane;

    private ClientModel model;
    private boolean running=true;
    @FXML
    public void initModel(ClientModel model) {
        if (this.model != null) {// ensure model is only set once:
            throw new IllegalStateException("[CLIENT] Model can only be initialized once");
        }
        this.model = model;
        loadListView();
        getAllEmails();
        receiveEmails();
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


    //send a "delete message" to server,delete selected email from the model and reload the listView
    public void onDeleteBtnClick() {
        if(model.getSelectedEmail()!=null) {
            //send Message to server using a new thread
            new Thread(() -> {
                Connection conn=new Connection();
                Message res = conn.sendMessage(new Message("DEL", List.of(model.getSelectedEmail())));
                System.out.println(res);
                if (res.getMsg().equals("OK")) {
                    Platform.runLater(
                            () -> {
                                model.deleteEmail(model.getSelectedEmail());
                                loadListView();
                                showInfoDialog("Email correctly deleted!");});
                } else if (res.getMsg().equals("DWN")) {
                    Platform.runLater(
                            () -> showErrorDialog("Server is not responding...\nPlease try later"));
                }
            }).start();
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


    public void getAllEmails(){
        new Thread(() -> {
            Connection conn=new Connection();
            Message res = conn.sendMessage(new Message("ALL", null));
            System.out.println(res);
            if (res.getMsg().equals("OK")) {
                Platform.runLater(
                        () -> {
                            model.addAllEmail(res.getEmails());
                            loadListView();
                        });
            } else if (res.getMsg().equals("DWN")) {
                Platform.runLater(
                        () -> showErrorDialog("Server is not responding...\nPlease try later"));
            }
        }).start();
    }

    public void receiveEmails(){
        new Thread(() -> {
            while(running) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {System.out.println(e);}
                Connection conn=new Connection();
                Message res = conn.sendMessage(new Message("CHK", null));
                System.out.println("check al server");
                if (res.getMsg().equals("OK")) {
                    Platform.runLater(
                            () -> {
                                model.addAllEmail(res.getEmails());
                                loadListView();
                                showInfoDialog("You received new emails ","check your inbox!");
                            });
                } else if (res.getMsg().equals("DWN")) {
                    Platform.runLater(
                            () -> showErrorDialog("OPS... connection lost :(", "Server is not responding...\nPlease try later"));
                }

            }
        }).start();
    }
}
