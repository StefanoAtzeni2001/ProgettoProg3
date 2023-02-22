package client.model;

import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connection {
    private  Socket socket;
    private  ObjectInputStream in;
    private  ObjectOutputStream out;

    public Connection(){
        try{
            socket = new Socket(InetAddress.getLocalHost(), 4242);
            socket.setSoTimeout(10000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in= new ObjectInputStream(socket.getInputStream());
        }catch(IOException e){
            System.out.println("[CLIENT] Connection Error, Could not connect to Server");}
    }

    public  Message sendMessage(Message msg){
        try {
            out.writeObject(msg);
            out.flush();
            return (Message) in.readObject();
        } catch (Exception e) {
            System.out.println("[CLIENT] Connection Error, server is not Responding");
            e.printStackTrace();
            return new Message("DWN",null);
        } finally {
            closeConnection();
        }
    }


    private void closeConnection() {
        try{
            if (socket != null){socket.close();}
            if (in != null){in.close();}
            if (out != null){out.close();}
        } catch (IOException e) {
            System.out.println("[CLIENT] Connection Error, Could not properly close Connection");
        }
    }
}
