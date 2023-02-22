package client.controller;

import client.model.ClientModel;
import client.model.Connection;
import javafx.event.ActionEvent;
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
        getAllEmails();
        receiveEmails();

        lblAccount.textProperty().bind(model.accountProperty());
        stage.setOnCloseRequest(event -> running = false);

    }


    @FXML
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
                System.out.println(res);
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


    public void getAllEmails() {
        new Thread(() -> {
            Connection conn = new Connection();
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

    public void receiveEmails() {
        new Thread(() -> {
            int sleepTime = 3000;
            while (running) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                Connection conn = new Connection();
                Message res = conn.sendMessage(new Message("CHK", null));
                System.out.println("check al server");
                if (res.getMsg().equals("OK") && res.getEmails() != null) {
                    Platform.runLater(
                            () -> {
                                model.addAllEmail(res.getEmails());
                                Scene scene = stage.getScene();
                                if (scene.lookup("#listPane") != null) loadListView();
                                showInfoDialog("You received new emails ", "check your inbox!");
                            });
                } else if (res.getMsg().equals("DWN")) {
                    sleepTime = 15000;
                    Platform.runLater(
                            () -> showErrorDialog("OPS... connection lost :(", "Server is not responding...\nPlease try later"));
                }

            }
        }).start();
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
