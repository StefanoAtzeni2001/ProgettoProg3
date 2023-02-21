package server.model;

import java.io.*;

public class IdSequence {
    int i;
    DataInputStream in;
    DataOutputStream out;
    public IdSequence(String dirpath)
    {
        String path = dirpath+"/indice";
        try
        {
            FileInputStream inS = new FileInputStream(path);
            this.in=new DataInputStream(inS);
            FileOutputStream outS=new FileOutputStream(path);
            this.out=new DataOutputStream(outS);
            i= in.readInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized int getNextID(){
        try {
            i++;
            out.writeInt(i);
            out.flush();
            return i-1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        try {
            if (in!=null)
                in.close();
            if (out!=null)
                out.close();
        } catch (IOException e) {
            try {
                out.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }


}