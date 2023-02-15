package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import client.model.ClientModel;
import client.model.Email;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientController {

    @FXML
    private Label lblFrom;

    @FXML
    private Label lblTo;

    @FXML
    private Label lblSubject;

    @FXML
    private Label lblUsername;

    @FXML
    private TextArea txtEmailContent;

    @FXML
    private ListView<Email> lstEmails;//Observer

    private ClientModel model;
    private Email selectedEmail;
    private Email emptyEmail;
    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    @FXML
    public void initialize(){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        model = new ClientModel("myAccount@gmail.it");
        model.generateEmails(10);
        selectedEmail = null;
        emptyEmail = new Email("", List.of(""), "", "");
        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        updateDetailView(emptyEmail);
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
    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = lstEmails.getSelectionModel().getSelectedItem();//seleziona la mail cliccata
        selectedEmail = email;
        updateDetailView(email);
    }


    //aggiorna la visualizzazione integrale della mail in base a quella selezionata
    protected void updateDetailView(Email email) {
        if(email != null) {
            lblFrom.setText(email.getSender());
            lblTo.setText(String.join(", ", email.getReceivers()));
            lblSubject.setText(email.getSubject());
            txtEmailContent.setText(email.getText());
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }


    @FXML
    public void onSendButtonClick() {
        new Thread(()->{
            try{
                getSocket();
                Email msg= new Email("pippo",
                        Arrays.asList("tizio", "caio"),
                        "oggetto","testo");
                System.out.println(msg);
                out.writeObject(msg);
                out.flush();
                closeConnection();
            }catch(Exception e) {
                System.out.println("[CLIENT] Conncection Error, Could not send obj");
                //e.printStackTrace();
            }
        }).start();
    }


}
