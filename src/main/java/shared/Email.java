package shared;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Email implements Serializable {
    private Integer id;
    private String sender;
    private List<String> receivers;
    private String subject;
    private String text;

    private String date;


    public Email(Integer id,String sender, List<String> receivers, String subject, String text,LocalDateTime date) {
        this.id= id;
        this.sender = sender;
        this.receivers = new ArrayList<>(receivers);
        this.subject = subject;
        this.text = text;
        this.date=date.toString();
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {return subject;}

    public String getText() {
        return text;
    }

    public Integer getID() {return id;}

    public void setID(Integer x) {this.id=x;}

    public String getDate() {return date;}


    @Override
    public String toString() {
        return  "[Da: " +sender + "]    "  + subject + " - " +   (text.length()>70 ? text.substring(0,50)+ "...": text);
    }



}