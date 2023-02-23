package server.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class IdSequence {


    String path;
    int i;

    public IdSequence(String dirpath) throws IOException {
        i=0;
        path = dirpath+"/indice.txt";

        // Read from file
        File f=new File(path);

        if(f.createNewFile())
        {
            FileWriter writer = new FileWriter(f);
            writer.write(i+"\n");
            writer.close();
        }
        Scanner reader= new Scanner(f);

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            i=Integer.parseInt(line);
        }
        reader.close();
    }



    public synchronized int getNextID() throws IOException {
        i++;
        FileWriter writer = new FileWriter(path);
        writer.write(i+"\n");
        writer.close();
        return i-1;
    }

}