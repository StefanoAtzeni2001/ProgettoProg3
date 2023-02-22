package server.controller;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import shared.Email;
import shared.Message;
import server.model.ServerModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ServerController {
    @FXML
    private TextArea txtAreaLog;

    private static final int THREAD_NUMBER=5;
    private ServerSocket socket;
    private ExecutorService threadPool;

    private boolean running=true;
    private ServerModel model;
    public void initialize()  {
        if (this.model != null)
            throw new IllegalStateException("[SERVER] Model can only be initialized once");
        model=new ServerModel();
        txtAreaLog.textProperty().bind(model.logProperty());
        startServer();
    }

    private void startServer() {
        MemoryManager mem = new MemoryManager(model);

        try {
            socket = new ServerSocket(4242);
        } catch (Exception e) {
            System.out.println("[SERVER] Error opening the socket");
        }
        threadPool = Executors.newFixedThreadPool(THREAD_NUMBER);
        //il thread main si occupa della gui
        new Thread(()->{//thread che rimane in loop in ascolto sul socket
            while(running){
                try{
                    Socket req=socket.accept(); //bloccante
                    threadPool.execute(()->{ //quando arriva una richiesta viene assegnato il task a un thread della threadpool
                        try {
                            ObjectInputStream in = new ObjectInputStream(req.getInputStream());
                            ObjectOutputStream out= new ObjectOutputStream(req.getOutputStream());

                            Message msg = (Message) in.readObject();
                            System.out.println(msg);
                            model.setLog(model.getLog() + msg+"\n" );

                            OperationThread op=new OperationThread(msg,mem,out);
                            threadPool.execute(op);


                        }catch(Exception e ){System.out.println("[SERVER] Connection Error, Could not read from client");}
                    });
                }catch(Exception e){System.out.println("[SERVER] Connection Error, Socket close");}

            }
        }).start();

    }


    private void closeServer(){
        running=false;
    }


    public List<Email> generateEmails(int n){
        if (n==0) return null;
        List<Email> list=new ArrayList<>();
        String[] people = new String[] {"paolo.verdi@gmail.com", "Alessandro.rossi@gmail.com", "Enrico.bianchi@gmail.com", "Giulia.neri@edu.unito.it", "Gaia.deLuigi@libero.it", "Simone.viola@unito.it"};
        String[] subjects = new String[] {
                "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                "Non dimenticare...", "Domani scuola" };
        String[] texts = new String[] {
                "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo"
        };
        Random r = new Random();
        for (int i=0; i<n; i++) {
            Email email = new Email(
                    i,
                    people[r.nextInt(people.length)],
                    List.of(people[r.nextInt(people.length)],people[r.nextInt(people.length)],people[r.nextInt(people.length)]),
                    subjects[r.nextInt(subjects.length)],
                    texts[r.nextInt(texts.length)],
                    LocalDateTime.now());
            list.add(email);
        }
        return list;
    }

}
