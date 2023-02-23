package client.controller;

import client.model.ClientModel;
import client.model.Connection;
import javafx.event.ActionEvent;
import shared.Email;
import shared.Message;
import static client.controller.Dialogs.showErrorDialog;
import static client.controller.Dialogs.showInfoDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MainViewController {
    @FXML
    private Button btnBack;
    @FXML
    private Label lblAccount;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnDark;

    private ClientModel model;
    private Stage stage;
    private boolean running = true;
    private boolean darkMode = false;
    private String urlTheme;

    @FXML
    public void initModel(ClientModel model, Stage stage) {
        if (this.model != null) {// ensure model is only set once:
            throw new IllegalStateException("[CLIENT] Model can only be initialized once");
        }
        this.model = model;
        this.stage = stage;
        urlTheme = getClass().getResource("/ClientView/DarkTheme.css").toExternalForm();
        loadListView();
        receiveEmails();
        lblAccount.textProperty().bind(model.accountProperty());
        stage.setOnCloseRequest(event -> running = false);

    }


    @FXML//Create and load ListView in the rootPane of mainView
    public void loadListView() {
        try {
            URL listViewUrl = getClass().getResource("/ClientView/listView.fxml");
            FXMLLoader listLoader = new FXMLLoader(listViewUrl);
            AnchorPane pane = listLoader.load();
            ListViewController listController = listLoader.getController();
            listController.initModel(model);
            rootPane.getChildren().setAll(pane);
            btnBack.setVisible(false);

        } catch (IOException e) {
            System.out.println("[CLIENT] GUI Error");
        }
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
            if (darkMode) scene.getStylesheets().add(urlTheme);
            return stage;
        } catch (IOException e) {
            System.out.println("[CLIENT] GUI Error");
            return null;
        }
    }

    public void onBackBtnClick() {
        loadListView();
    }


    //send a "delete message" to server,delete selected email from the model and reload the listView
    public void onDeleteBtnClick() {
        if (model.getSelectedEmail() != null) {
            //send Message to server using a new thread
            new Thread(() -> {
                Connection conn = new Connection();
                Message res = conn.sendMessage(new Message("DEL", List.of(model.getSelectedEmail())));
                if (res.getMsg().equals("OK")) {
                    Platform.runLater(
                            () -> {
                                model.deleteEmail(model.getSelectedEmail());
                                showInfoDialog("Email correctly deleted!");
                                loadListView();
                            });
                } else if (res.getMsg().equals("DWN")) {
                    Platform.runLater(
                            () -> showErrorDialog("Server is not responding...\nPlease try later"));
                }
            }).start();
        }
    }


    //show the writeView but "dest" and "subject" are already compiled and not editable
    public void onReplyBtnClick() {
        if (model.getSelectedEmail() != null) {
            Stage stage = loadWriteView();
            Scene scene = stage.getScene();
            scene.lookup("#txtTo").setDisable(true);
            scene.lookup("#txtTo").setOpacity(0.7);
            scene.lookup("#txtSubject").setDisable(true);
            scene.lookup("#txtSubject").setOpacity(0.7);
            //if the selected email has more than 1 receiver, it's possible to select the "Reply all" option
            if(model.getSelectedEmail().getReceivers().size()>1)
                scene.lookup("#chkReplyAll").setVisible(true);
            model.setDest(model.getSelectedEmail().getSender());
            model.setSubject("Re: " + model.getSelectedEmail().getSubject());
            stage.show();
        }
    }


    //show the writeView but "text" and "subject" are already compiled and not editable
    public void onForwardBtnClick() {
        if (model.getSelectedEmail() != null) {
            Stage stage = loadWriteView();
            Scene scene = stage.getScene();
            scene.lookup("#txtText").setDisable(true);
            scene.lookup("#txtText").setOpacity(0.7);
            scene.lookup("#txtSubject").setDisable(true);
            scene.lookup("#txtSubject").setOpacity(0.7);
            model.setText("[Inoltrato da: " + model.getSelectedEmail().getSender() + "]\n" + model.getSelectedEmail().getText());
            model.setSubject("Fwd: " + model.getSelectedEmail().getSubject());
            stage.show();
        }
    }


    //show the writeView
    public void onWriteBtnClick() {
        loadWriteView().show();
    }

    public void receiveEmails() {
        new Thread(() -> {
            String msg="ALL";
            final String myAccount=model.getAccount();
            int sleepTime=7000;
            while(running) {
                Connection conn = new Connection();
                Email email = new Email(0,myAccount, null, "", "", LocalDateTime.now());
                Message res = conn.sendMessage(new Message(msg, List.of(email)));
                if (res.getMsg().equals("OK")){//Server received message
                    if(!res.getEmails().isEmpty()) {//Server sent some emails
                        Platform.runLater(() -> {
                            model.addAllEmail(res.getEmails());
                            Scene scene = stage.getScene();
                            if (scene.lookup("#listPane") != null) loadListView();
                        });
                        if (msg.equals("CHK")) { //client received new emails
                            ackMails(res);
                            Platform.runLater(() -> showInfoDialog("You received new emails ", "check your inbox!"));
                        }else if(msg.equals("ALL")) {//client received all inbox
                            msg = "CHK";
                        }
                    }
                } else if (res.getMsg().equals("DWN")) {//Server didn't receive respond
                    Platform.runLater(() -> showErrorDialog("Server is not responding...\nPlease try later"));
                    sleepTime = 10000;
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {System.out.println(e);}
            }
        }).start();
    }

    private void ackMails(Message res) {
        ArrayList<String> idList;
        idList=new ArrayList<>();
        for( Email em: res.getEmails())
            idList.add(em.getID().toString());
        Email cmtList=new Email(0,model.getAccount(),idList,"","", LocalDateTime.now());
        Connection conn=new Connection();
        Message mes=new Message("ACK", List.of(cmtList));
        conn.sendMessage(mes);
    }

    public void onDarkBtnClick() {
        Scene scene = stage.getScene();
        if (darkMode) {
            scene.getStylesheets().remove(urlTheme);
            btnDark.setText("DarkMode");
        } else {
            scene.getStylesheets().add(urlTheme);
            btnDark.setText("LightMode");
        }
        darkMode=!darkMode;
    }
}
