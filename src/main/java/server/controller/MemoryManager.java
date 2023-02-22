package server.controller;

import com.google.gson.Gson;
import server.model.IdSequence;
import shared.Email;
import server.model.ObjString;
import server.model.ServerModel;

import java.io.*;
import java.util.*;

public class MemoryManager {
    private IdSequence ID_Seq;
    private ServerModel model;
    private String  dirpath= "./src/main/java/server/accounts";
    private ArrayList<ObjString> accounts;
    public MemoryManager(ServerModel model) {

        this.model=model;

        File dir = new File(dirpath);
        if (!dir.isDirectory())
            dir.mkdir();
        ID_Seq=new IdSequence(dirpath);
        accounts = getAccounts(dir);
        System.out.println(accounts);
    }
    public ObjString findAccount(String dest){
        return accounts.get(accounts.indexOf(new ObjString(dest)));
    }

    public List<String> addMail(Email mail){
        List<String> dests=mail.getReceivers();
        List<String> err=new ArrayList<>();
        for (String dest:dests) {
           if (!accounts.contains(new ObjString(dest) )) err.add(dest);
        }
        if(err.isEmpty()) {
            System.out.println("sto Inviando");
            mail.setID(ID_Seq.getNextID()); //autoboxing
            save(mail);
            return null;}
        else return err;
    }

    private void save(Email mail) {
        Gson gson=new Gson();
        String path;
        File receiver;
        FileWriter out;
        System.out.println("sono qua");
        for (String dest:mail.getReceivers()) {
            synchronized (this.findAccount(dest)) {
                path= dest +"/InArrivo/"+mail.getID();
                receiver = new File(dirpath+"/"+path);
                try {
                    receiver.createNewFile();

                    out=new FileWriter(receiver) ;
                    System.out.println("scrivo "+ gson.toJson(mail));
                    out.write(gson.toJson(mail)+"\n");
                    out.flush();
                    out.close();
                    model.setLog(model.getLog() + "Email inviata da "+mail.getSender()+" a "+dest+"\n" );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

    public ArrayList<Email> getMails(boolean bool,String dest) throws IOException, ClassNotFoundException {

        ArrayList<Email> out=new ArrayList<>();
        String path = dirpath+"/"+ dest + "/InArrivo";
        readMailFromFile(path,out);
        if(bool) {
            path = dirpath + "/" + dest + "/Ricevute";
            readMailFromFile(path, out);
        }
        return out;
    }

    private void readMailFromFile(String path,ArrayList<Email> out) throws IOException, ClassNotFoundException {
        Scanner reader=null;
        Gson gson=new Gson();
        File dir=new File(path);
        String line = null;
        File[] daInviare=dir.listFiles();
        for(File i:daInviare)
        {
            reader = new Scanner(i);
            while (reader.hasNextLine()) {
                line = reader.nextLine();
            }
            out.add( gson.fromJson(line, Email.class ));
            reader.close();
        }
        if (reader!=null)
            reader.close();
    }


    public void close(){
        ID_Seq.close();
    }


}
