package server.controller;

import javafx.application.Platform;
import server.model.ServerModel;
import shared.Email;
import shared.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Task che gestiscono le operazion sul server
public class OperationThread implements Runnable{
    private Message msg;
    private MemoryManager mem;
    ObjectOutputStream out;
    ServerModel model;

    public OperationThread(Message e, MemoryManager mem, ObjectOutputStream out, ServerModel model){
        this.mem=mem;
        this.msg=e;
        this.out=out;
        this.model=model;
    }
    @Override
    public void run() {
        try {
            switch (msg.getMsg()) {
                case "SND" -> {

                        List<Email> mails = new ArrayList<>();
                        List<String> NonExistentAcc = null;
                        Email m;

                        m = msg.getEmails().get(0);
                        //NonExistentAcc raccoglie gli account non presenti nel server
                        try {
                            NonExistentAcc = mem.addMail(m);
                        }catch (IOException e){
                            mem.delete_Email(m);
                            Platform.runLater(()-> model.setLog(model.getLog()+"SERVER ERROR: Email not sent\n" ));
                        }
                        if (NonExistentAcc != null) {
                            //invia un messaggio con Email composta di metadati
                            mails.add(new Email(m.getID(), m.getSender(), NonExistentAcc, m.getSubject(), m.getText(), LocalDateTime.now()));
                            model.setLog(model.getLog()+m.getSender()+" ERROR: Invalid Receiver : "+NonExistentAcc.size()+"\n" );
                            out.writeObject(new Message("ERR", mails));
                        } else {
                            out.writeObject(new Message("OK", null));
                        }

                }
                case "DEL" -> {
                    boolean b=false;
                    try {
                         b = mem.delete_Email(msg.getEmails().get(0));
                    }catch(RuntimeException e){
                        if(e.getMessage()=="Not found")
                            Platform.runLater(()-> model.setLog(model.getLog()+"Account - "+msg.getEmails().get(0)+" not found\n" ));
                    }
                    Message risp;
                    if (b) {
                        risp = new Message("OK", null);
                        Platform.runLater(()-> model.setLog(model.getLog()+"Email - "+msg.getEmails().get(0)+" deleted\n" ));
                    }
                    else
                        risp = new Message("ERR", null); //nel caso in cui la mai non sia stata effettivamente cancellata si manda messaggio di errore
                    out.writeObject(risp);
                }
                case "ALL" -> {
                    //synch su cartella dell'account tramite ObjString
                    synchronized (mem.findAccount(msg.getEmails().get(0).getSender())) {
                        String dest = msg.getEmails().get(0).getSender();
                        List<Email> list= mem.getMails(true, dest);
                        Platform.runLater(()-> model.setLog(model.getLog()+"[SERVER] sending - "+list.size()+" to "+dest+"\n" ));
                        out.writeObject(new Message("OK",list));
                    }
                }
                case "CHK" -> {
                    //synch su cartella dell'account tramite ObjString
                    synchronized (mem.findAccount(msg.getEmails().get(0).getSender())) {
                        String dest = msg.getEmails().get(0).getSender();
                        List<Email> list= mem.getMails(false, dest);
                        Platform.runLater(()->  model.setLog(model.getLog()+"[SERVER] sending - "+list.size()+" to "+dest+"\n" ));
                        out.writeObject(new Message("OK", mem.getMails(false, dest)));
                    }
                }
                case "ACK" -> {
                    //synch su cartella dell'account tramite ObjString
                    synchronized (mem.findAccount(msg.getEmails().get(0).getSender())) {
                        //estrazione id dal messaggio contenuti in una mail di metadati
                        List<String> IDs = msg.getEmails().get(0).getReceivers();
                        String target = msg.getEmails().get(0).getSender();
                        for (String i : IDs) {
                            if(mem.move_email(i, target))
                                Platform.runLater(()-> model.setLog(model.getLog()+"{ "+i+" } - "+target+" moved\n" ));
                        }
                    }
                }
            }
            out.flush();
            out.close();
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
