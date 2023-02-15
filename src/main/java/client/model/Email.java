package client.model;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Email implements Serializable {
    private String ID;
    private String sender;
    private List<String> receivers;
    private String subject;
    private String text;

    private LocalDateTime date;


    public Email(String sender, List<String> receivers, String subject, String text) {
        this.sender = sender;
        this.receivers = new ArrayList<>(receivers);
        this.subject = subject;
        this.text = text;
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

    public String getID() {return ID;}

    public LocalDateTime getDate() {return date;}


    public String toString() {
        return String.join(" - ", List.of(this.sender,this.subject));
    }
}
