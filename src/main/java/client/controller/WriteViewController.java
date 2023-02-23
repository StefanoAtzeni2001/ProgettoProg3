package client.controller;

import client.model.ClientModel;
import client.model.Connection;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.Email;
import shared.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static client.controller.Dialogs.*;

public class WriteViewController {

    public TextField txtTo;
    public TextField txtSubject;
    public TextArea txtText;
    public VBox mainBox;
    public CheckBox chkReplyAll;
    private List<String> destList;
    private ClientModel model;




    public void initModel(ClientModel model) {
        this.model=model;
        txtText.textProperty().bindBidirectional(model.textProperty());
        txtTo.textProperty().bindBidirectional(model.destProperty());
        txtSubject.textProperty().bindBidirectional(model.subjectProperty());
        destList=new ArrayList<>();
    }



    //checks input fields, create Message object and send it to server
    public void onSendBtnClick() {
        //checks input fields
        if (txtTo.getText().equals("") || txtSubject.getText().equals("") || txtText.getText().equals("")) {
            showWarningDialog("Compile all camps!");
        }else {
                destList = List.of(model.getDest().replaceAll("\\s", "").split(";"));
                List<String> err = checkEmails(destList);
                if (err != null) {
                    showWarningDialog("invalid emails: " +err,
                            "Email non valida:\n" +
                                    " deve contenere @ e non può contenere caratteri speciali \n" +
                                    "usare il ';' per separare più destinatari");
                } else {
                    //create Message
                    Email email = new Email(0, model.getAccount(), destList, model.getSubject(), model.getText(), LocalDateTime.now());
                    Message msg = new Message("SND", List.of(email));
                    //send Message to server using a new thread
                    new Thread(() -> {
                        Connection conn = new Connection();
                        Message res = conn.sendMessage(msg);
                        if (res.getMsg().equals("OK")) {
                            Platform.runLater(//Other Thread can't modify GUI
                                    () -> {
                                        showInfoDialog("Email correctly delivered!");
                                        Stage stage = (Stage) mainBox.getScene().getWindow();
                                        stage.close();
                                    });
                        }else if (res.getMsg().equals("ERR")) {
                            Platform.runLater(
                                    () -> showErrorDialog("Email not found: "+res.getEmails().get(0).getReceivers()));
                        } else if (res.getMsg().equals("DWN")) {
                            Platform.runLater(
                                    () -> showErrorDialog("Server is not responding...\nPlease try later"));
                        }
                    }).start();
                }
            }
        }

    private boolean isWrong(String text) {
        //"[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,4}";
        String regexPattern = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+[a-zA-Z0-9][.]+[a-zA-Z]{2,4}";
        Pattern pattern=Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(text);
        return !matcher.matches();
    }
    private List<String> checkEmails(List<String> dests){
        List<String> err=new ArrayList<>();
        for (String dest:dests) {
            if(isWrong(dest))
                err.add(dest);
        }
        if(err.isEmpty()) {
            return null;}
        else return err;
    }

    public void onReplyAllChkClick() {
        String dests=model.getSelectedEmail().getSender();
        if(chkReplyAll.isSelected()) {
            for(String rec:model.getSelectedEmail().getReceivers())
                if(!rec.equals(model.getAccount())) dests=dests+"; "+rec;
       }
           model.setDest(dests);
    }
}


