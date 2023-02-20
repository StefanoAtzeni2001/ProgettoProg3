package server.controller;

import Shared.Email;
import server.model.ObjString;
import server.model.ServerModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MemoryManager {
    private ServerModel model;
    private String  dirpath= "./src/main/java/server/accounts";
    private ArrayList<ObjString> accounts;
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
           if (!corretto||!accounts.contains(new ObjString(dest) )) err.add(dest);
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

    private ArrayList<ObjString> getAccounts(File dir) {
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
            ArrayList<ObjString> a= new ArrayList<>();
            File[] lista=dir.listFiles(filter);
            if(lista!=null)  for (File f: lista) {
                a.add(new ObjString(f.getName()) );
            }
            return a;
    }

    public boolean delete_Email(Email mail)
    {
        boolean ok = false;
        String path;
        File receiver;
        for (String dest:mail.getReceivers()) {
            path = dest + "/InArrivo/" + mail.getID();
            receiver = new File(dirpath + "/" + path);

            if (receiver.exists()) {
                receiver.delete();
                ok=true;
                model.setLog(model.getLog() + "Email  " + mail.getID() + " eliminata da casella InArrivo di " + dest + "\n");
            } else {
                path = dest + "/Ricevute/" + mail.getID();
                receiver = new File(dirpath + "/" + path);
                if (receiver.exists()) {
                    receiver.delete();
                    ok=true;
                    model.setLog(model.getLog() + "Email  " + mail.getID() + " eliminata da casella Ricevute di " + dest + "\n");
                }else model.setLog(model.getLog() + "Email  " + mail.getID() + " non trovata in " + dest + "\n");
            }
        }
        return ok;
    }

    public boolean move_email(String ID,String dest){
        String path1 = dirpath+"/"+ dest + "/InArrivo/" + ID;
        String path2 = dirpath+"/"+ dest + "/Ricevute/" + ID;

        File sourceFile = new File(path1);
        File targetFile = new File(path2);


        if (sourceFile.renameTo(targetFile)) {
            model.setLog("Email  " + ID + " inviata a " + dest + "\n");
            return true;
        } else {
            System.err.println("Nessuna mail con ID "+ID+" in arrivo a "+dest);
            return false;
        }
    }
}
