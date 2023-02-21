package client.controller;

import client.model.ClientModel;
import client.model.Connection;
import javafx.application.Platform;
import javafx.stage.Stage;
import shared.Email;
import shared.Message;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static client.controller.Dialogs.*;

public class WriteViewController {

    public TextField txtTo;
    public TextField txtSubject;
    public TextArea txtText;
    public VBox mainBox;
    private List<String> destList;

    private ClientModel model;



    public void initModel(ClientModel model) {
        this.model=model;
        txtText.textProperty().bindBidirectional(model.textProperty());
        txtTo.textProperty().bindBidirectional(model.destProperty());
        txtSubject.textProperty().bindBidirectional(model.subjectProperty());
        destList=new ArrayList<>();
    }


    //NOTA: non controlla tutte le mail invalide tipo( ciao@@@a12! .a è valida)
    // ci sono delle classi apposta per validare le mail con le espressioni regolari (da vedere poi)
    private List<String> checkEmails(List<String> dests){
        List<String> err=new ArrayList<>();
        boolean correct;
        StringTokenizer st;
        String a;
        for (String dest:dests) {
            correct=true;
            st= new StringTokenizer(dest);
            try {
                a=st.nextToken("@");
                if (a.length()==0) correct=false;
                a=st.nextToken(".");
                if (a.length()==0) correct=false;
                a=st.nextToken();
                if (a.length()==0) correct=false;

            }catch (NoSuchElementException e){ correct=false;}
            if (!correct) err.add(dest);
        }
        if(err.isEmpty()) {
            return null;}
        else return err;}

    //checks input fields, create Message object and send it to server
    public void onSendBtnClick() {
        //checks input fields
        if (txtTo.getText().equals("") || txtSubject.getText().equals("") || txtText.getText().equals("")) {
            showWarningDialog("Compile all camps!");
        } else {
            destList = List.of(model.getDest().split(";"));
            List<String> err = checkEmails(destList);
            if (err != null) {
                showWarningDialog("invalid emails: " + err);
            }else {
                //create Message
                Email email = new Email(0, model.getAccount(), destList, model.getSubject(), model.getText(), LocalDateTime.now());
                Message msg = new Message("SND", List.of(email));
                //send Message to server using a new thread
                new Thread(() -> {
                    Connection conn=new Connection();
                    Message res = conn.sendMessage(msg);
                    if (res.getMsg().equals("OK")) {
                        Platform.runLater(//Other Thread can't modify GUI
                                () -> {
                                    showInfoDialog("Email correctly delivered!");
                                    Stage stage = (Stage)mainBox.getScene().getWindow();
                                    stage.close();
                                 });
                    } else if (res.getMsg().equals("DWN")) {
                        Platform.runLater(
                                () -> showErrorDialog("Server is not responding...\nPlease try later"));
                    }
                }).start();
            }
        }
    }




}


