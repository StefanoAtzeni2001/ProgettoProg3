package server.controller;

import shared.Email;
import shared.Message;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OperationThread implements Runnable{
    private Message msg;
    private MemoryManager mem;
    ObjectOutputStream out;

    public OperationThread(Message e, MemoryManager mem, ObjectOutputStream out){
        this.mem=mem;
        this.msg=e;
        this.out=out;
    }
    @Override
    public void run() {
        List<Email>  mails = new ArrayList<>();
        switch (msg.getMsg()) {
            case "SND" -> {
                List<String> strings;
                Email m;
                try {
                    m = msg.getEmails().get(0);
                    strings = mem.addMail(m);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Errore nella lista di mail nel messaggio");
                    return;
                }
                if (strings != null) {
                    mails.add(new Email(m.getID(), m.getSender(), strings, m.getSubject(), m.getText(), m.getDate()));
                    try {
                        out.writeObject(new Message("ERR", mails));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        out.writeObject(new Message("OK", null));
                    } catch (IOException e) {
                        System.err.println("Interruzione output");
                    }
                }
            }
            case "DEL" -> {
                boolean b = mem.delete_Email(msg.getEmails().get(0));
                Message risp;
                if (b)
                    risp = new Message("OK", null);
                else
                    risp = new Message("ERR", null);
                try {
                    out.writeObject(risp);
                } catch (IOException e) {
                    System.err.println("Interruzione output");
                }
            }
            case "ALL" -> {}
            case "CHK" -> {}
        }

    }
}
