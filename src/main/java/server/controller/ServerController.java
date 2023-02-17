package server.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import server.model.Email;
import server.model.ServerModel;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
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

    private void startServer(){
        MemoryManager mem=new MemoryManager(model);
        
        try{
            socket=new ServerSocket(4242);
        }catch(Exception e){System.out.println("[SERVER] Error opening the socket");}
        threadPool= Executors.newFixedThreadPool(THREAD_NUMBER);
        //il thread main si occupa della gui
        new Thread(()->{//thread che rimane in loop in ascolto sul socket
            while(running){
                try{
                    Socket req=socket.accept(); //bloccante
                    threadPool.execute(()->{ //quando arriva una richiesta viene assegnato il task a un thread della threadpool
                        try {
                            ObjectInputStream in = new ObjectInputStream(req.getInputStream());
                            Email msg = (Email) in.readObject();
                            System.out.print(msg);
                            model.setLog(model.getLog() + msg+"\n" );
                        }catch(Exception e ){System.out.println("[SERVER] Connection Error, Could not read from client");
                        e.printStackTrace();}
                    });
                }catch(Exception e){System.out.println("[SERVER] Connection Error, Socket close");}

            }
        }).start();
    }


    private void closeServer(){}

}
