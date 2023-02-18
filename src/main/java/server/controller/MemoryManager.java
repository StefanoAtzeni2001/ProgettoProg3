package server.controller;

import Shared.Email;
import server.model.ServerModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MemoryManager {
    private ServerModel model;
    private String  dirpath= "./src/main/java/server/accounts";
    private ArrayList<String> accounts;
    public MemoryManager(ServerModel model) {

        this.model=model;

        File dir = new File(dirpath);
        if (!dir.isDirectory())
            dir.mkdir();

        accounts = getAccounts(dir);
        System.out.println(accounts);
    }

    public List<String> addMail(Email mail){
        List<String> dests=mail.getReceivers();
        List<String> err=new ArrayList<>();
        boolean corretto=true;
        StringTokenizer st;
        String a;
        for (String dest:dests) {
            corretto=true;
            st= new StringTokenizer(dest);
            try {
                a=st.nextToken("@");
                if (a.length()==0) corretto=false;
                a=st.nextToken(".");
                if (a.length()==0) corretto=false;
                a=st.nextToken();
                if (a.length()==0) corretto=false;

            }catch (NoSuchElementException e){ corretto=false;}
           if (!corretto||!accounts.contains(dest)) err.add(dest);
        }
        if(err.isEmpty()) {
            System.out.println("sto Inviando");
            save(mail);
            return null;}
        else return err;
    }

    private void save(Email mail) {
        String path;
        File receiver;
        ObjectOutputStream out;
        System.out.println("sono qua");
        for (String dest:mail.getReceivers()) {
            path= dest +"/InArrivo/"+mail.getID();
            receiver = new File(dirpath+"/"+path);
            try {
                out=new ObjectOutputStream(new FileOutputStream(receiver)) ;
                out.writeObject(mail);
                model.setLog(model.getLog() + "Email inviata da "+mail.getSender()+" a "+dest+"\n" );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ArrayList<String> getAccounts(File dir) {
        {
            FileFilter filter = new FileFilter() {
                File subFile;
                public boolean accept(File file) {
                    if(file.isDirectory()){
                        subFile=new File(file.getPath()+"/InArrivo");
                        if(!subFile.isDirectory()) subFile.mkdir();
                        subFile=new File(file.getPath()+"/Ricevute");
                        if(!subFile.isDirectory()) subFile.mkdir();
                        return true;

                    }
                    else return false;
                }
            };
            ArrayList<String> a= new ArrayList<>();
            File[] lista=dir.listFiles(filter);
            if(lista!=null)  for (File f: lista) {
                a.add(f.getName());
            }
            return a;
        }
    }
}
