package client.controller;

import client.model.ClientModel;
import Shared.Email;
import Shared.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class WriteViewController {

    public TextField txtTo;
    public TextField txtSubject;
    public TextArea txtText;
    public VBox mainBox;
    private List<String> destList;


    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    private ClientModel model;



    public void initModel(ClientModel model) {
        this.model=model;
        txtText.textProperty().bindBidirectional(model.textProperty());
        txtTo.textProperty().bindBidirectional(model.destProperty());
        txtSubject.textProperty().bindBidirectional(model.subjectProperty());
        destList=new ArrayList<>();
    }

    protected void getSocket()  {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 4242);
            out = new ObjectOutputStream(socket.getOutputStream());
        }catch(IOException e){System.out.println("[CLIENT] Connection Error, Could not connect to Server");}
    }
    protected void closeConnection() {
        try {
            if (socket != null){socket.close();}
            if (in != null){in.close();}
            if (out != null){out.close();}
        } catch (IOException e) {
            System.out.println("[CLIENT] Connection Error, Could not properly close Connection");
        }
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
        if(err.isEmpty()) {;
            return null;}
        else return err;}

    //checks input fields, create the Email Object, wrap it into a Message Object and call sendMessage()
    public void onSendBtnClick() {
       // System.out.println(txtTo.getText()+" - "+txtSubject.textProperty()+" - "+txtText.textProperty());
        if (txtTo.getText().equals("") || txtSubject.getText().equals("") || txtText.getText().equals("")) {
            showErrorDialog("Compile all camps!");
        } else {
            destList = List.of(model.getDest().split(";"));
            List<String> err = checkEmails(destList);
            if (err == null) {
                Email email = new Email(0, model.getAccount(), destList, model.getSubject(), model.getText(), LocalDateTime.now());
                Message msg = new Message("invio", List.of(email));
                sendMessage(msg);
            } else {
                showErrorDialog("invalid emails: " + err.toString());
            }
        }
    }
    public void sendMessage(Message msg) {;
        new Thread(()->{
            try{
                getSocket();
                System.out.println(msg);
                out.writeObject(msg);
                out.flush();
                closeConnection();
                Platform.runLater(//Other Thread can't modify GUI
                        ()->showInfoDialog("Email correctly delivered!"));
            }catch(Exception e) {
                Platform.runLater(//Other Thread can't modify GUI
                        ()->showErrorDialog("Error connecting to Server..."));
                System.out.println("[CLIENT] Conncection Error, Could not send obj");
                e.printStackTrace();
            }
        }).start();

    }

    public void showInfoDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText(msg);
        alert.setHeaderText("Operation Completed");
        alert.showAndWait();
        Stage stage=(Stage)mainBox.getScene().getWindow();
        stage.close();
    }

    public void showErrorDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Operation failed :(");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
