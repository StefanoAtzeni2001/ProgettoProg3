package server.model;
import com.google.gson.*;

import java.io.*;
import java.util.Scanner;

public class IdSequence {


    String path;
    Gson gson = new GsonBuilder().create();
    private Scanner reader;
    int i;

    public IdSequence(String dirpath)
    {
        i=0;
        path = dirpath+"/indice.txt";

        // Read from file
        try {
            File f=new File(path);

            if(f.createNewFile())
            {
                FileWriter writer = new FileWriter(f);
                writer.write(i+"\n");
                writer.close();
            }
            reader = new Scanner(f);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                i=Integer.parseInt(line);
            }
            System.out.println(i);
            reader.close();
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }



    public synchronized int getNextID(){
        i++;
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(i+"\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }
        return i-1;
    }

    public void close(){
       // writer.close();


    }



}