package server.controller;

import com.google.gson.Gson;
import server.model.IdSequence;
import server.model.ObjString;
import shared.Email;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemoryManager {
    private final IdSequence ID_Seq;
    private final String  dirpath= "./src/main/java/server/accounts";
    private ArrayList<ObjString> accounts;
    public MemoryManager() throws IOException {

        File dir = new File(dirpath);
        if (!dir.isDirectory())
            dir.mkdir();
        ID_Seq = new IdSequence(dirpath);

        accounts = getAccounts(dir);
    }
    public ObjString findAccount(String dest){
        return accounts.get(accounts.indexOf(new ObjString(dest)));
    }

    public List<String> addMail(Email mail) throws IOException {
        List<String> dests=mail.getReceivers();
        List<String> err=new ArrayList<>();
        for (String dest:dests) {
           if (!accounts.contains(new ObjString(dest) )) err.add(dest);
        }
        if(err.isEmpty()) {
            mail.setID(ID_Seq.getNextID()); //autoboxing
            save(mail);
            return null;}
        else {
            return err;
        }

    }

    private void save(Email mail) throws IOException {
        Gson gson=new Gson();
        String path;
        File receiver;
        FileWriter out;
        for (String dest:mail.getReceivers()) {
            synchronized (this.findAccount(dest)) {
                path= dest +"/InArrivo/"+mail.getID();
                receiver = new File(dirpath+"/"+path);
                receiver.createNewFile();
                out=new FileWriter(receiver) ;
                out.write(gson.toJson(mail)+"\n");
                out.flush();
                out.close();

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

    public boolean delete_Email (Email mail) throws RuntimeException
    {
        boolean ok = false;
        String path;
        File receiver;
        for (String dest:(mail.getReceivers())) {
            if(!accounts.contains(new ObjString(dest))) throw new RuntimeException("Not found");
            path = dest + "/InArrivo/" + mail.getID();
            receiver = new File(dirpath + "/" + path);

            if (receiver.exists()) {
                receiver.delete();
                ok=true;
            } else {
                path = dest + "/Ricevute/" + mail.getID();
                receiver = new File(dirpath + "/" + path);
                if (receiver.exists()) {
                    receiver.delete();
                    ok=true;
                }
            }
        }
        return ok;
    }

    public boolean move_email(String ID,String dest){
        String path1 = dirpath+"/"+ dest + "/InArrivo/" + ID;
        String path2 = dirpath+"/"+ dest + "/Ricevute/" + ID;

        File sourceFile = new File(path1);
        File targetFile = new File(path2);
        return sourceFile.renameTo(targetFile);
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

}
