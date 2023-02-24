package server.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import server.model.ServerModel;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
    public void initModel(ServerModel model, Stage stage)  {
        if (this.model != null)
            throw new IllegalStateException("[SERVER] Model can only be initialized once");
        this.model=model;
        stage.setOnCloseRequest((event)->closeServer());
        txtAreaLog.textProperty().bind(model.logProperty());
        startServer();
    }

    private void startServer() {
        MemoryManager mem = null;
        try {
            mem = new MemoryManager();
        } catch (IOException e) {
            model.setLog(model.getLog() + "FATAL ERROR: Corrupt Index "+"\n" );
        }

        try {
            socket = new ServerSocket(4242);
        } catch (Exception e) {
            model.setLog(model.getLog() + "[SERVER] Error opening the socket\n");
        }
        threadPool = Executors.newFixedThreadPool(THREAD_NUMBER);
        //il thread main si occupa della gui
        if(mem!=null) {
            MemoryManager finalMem = mem;
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

                            OperationThread op=new OperationThread(msg, finalMem,out,model);
                            threadPool.execute(op);


                        }catch(Exception e ){
                            Platform.runLater(
                                    () -> model.setLog(model.getLog() +"[SERVER] Connection Error, Could not read from client\n" ));}
                    });
                }catch(Exception e){model.setLog(model.getLog() +"[SERVER] Connection Error, Socket error" );}
                Platform.runLater(
                        () ->txtAreaLog.setScrollTop(Double.MIN_VALUE));
            }
        }).start();
        }else closeServer();

    }



    private void closeServer(){
        running=false;
        try{
            socket.close();
        }catch(Exception e){System.out.println(e);}
        threadPool.shutdown();
    }


    public void onClearBtnClick() {
        model.setLog("");
    }
}
