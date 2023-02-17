package client.controller;

import client.model.ClientModel;
import Shared.Email;
import Shared.Message;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WriteViewController {

    public TextField txtTo;
    public TextField txtSubject;
    public TextArea txtText;
    private List<String> destList;
    private String subject;
    private String text;

    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    private ClientModel model;

    public void onSendBtnClick(ActionEvent actionEvent) {
        subject=txtSubject.getText();
        text=txtText.getText();
        destList= List.of(txtTo.getText().split(";"));
        if(checkEmails(destList)){
            Email email=new Email(0,model.getAccount(),destList,subject,text, LocalDateTime.now());
            Message msg=new Message("invio",List.of(email));
            sendMessage(msg);
        }
    }

    public void initModel(ClientModel model) {
        this.model=model;
        destList=new ArrayList<>();
    }

    private boolean checkEmails(List<String> list){return true;}

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

    public void sendMessage(Message msg) {
        new Thread(()->{
            try{
                getSocket();
                System.out.println(msg);
                out.writeObject(msg);
                out.flush();
                closeConnection();
            }catch(Exception e) {
                System.out.println("[CLIENT] Conncection Error, Could not send obj");
                e.printStackTrace();
            }
        }).start();
    }
}
