package shared;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private String msg;
    private List<Email> emails;

    public Message(String msg, List<Email> emails) {
        this.msg = msg;
        this.emails = emails;
    }

    public String getMsg() {
        return msg;
    }

    public List<Email> getEmails() {
        return emails;
    }

    @Override
    public String toString() {
        Email email= emails.get(0);
        String s="{"+msg+"} ("+email.getSender()+") ";

        switch (msg){
            case "SND":
                s=s+ "-> " +email.getReceivers();
                break;
            case "DEL":
                s=s+ "["+email.getID()+"]";
                break;
            case "CMT":
                s=s+" " +email.getReceivers() ;
                break;
        }
        return s;
    }

}