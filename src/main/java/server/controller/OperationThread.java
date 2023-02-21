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
        try {
            switch (msg.getMsg()) {
                case "SND" -> {
                    List<Email> mails = new ArrayList<>();
                    List<String> strings;
                    Email m;

                    m = msg.getEmails().get(0);
                    strings = mem.addMail(m);

                    if (strings != null) {
                        mails.add(new Email(m.getID(), m.getSender(), strings, m.getSubject(), m.getText(), m.getDate()));
                        out.writeObject(new Message("ERR", mails));

                    } else {
                        out.writeObject(new Message("OK", null));
                    }
                }
                case "DEL" -> {
                    boolean b = mem.delete_Email(msg.getEmails().get(0));
                    Message risp;
                    if (b)
                        risp = new Message("OK", null);
                    else
                        risp = new Message("ERR", null);
                    out.writeObject(risp);
                }
                case "ALL" -> {
                    String dest = msg.getEmails().get(0).getSender();
                    out.writeObject(new Message("OK", mem.getMails(true, dest)));
                }
                case "CHK" -> {
                    String dest = msg.getEmails().get(0).getSender();
                    out.writeObject(new Message("OK", mem.getMails(false, dest)));
                }
                case "CMT" -> {
                    List<String> IDs=msg.getEmails().get(0).getReceivers();
                    String target=msg.getEmails().get(0).getSender();
                    for(String i: IDs)
                    {
                        mem.move_email(i,target);
                    }

                }
            }
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
