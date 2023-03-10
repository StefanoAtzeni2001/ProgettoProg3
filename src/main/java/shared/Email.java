package shared;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Email implements Serializable,Comparable<Email> {
    private Integer id;
    private String sender;
    private List<String> receivers;
    private String subject;
    private String text;

    private String date;


    public Email(Integer id,String sender, List<String> receivers, String subject, String text,LocalDateTime date) {
        this.id= id;
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.text = text;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = date.format(formatter);
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
        String tmp= (text.length()>70 ? text.substring(0,50)+ "...": text);
        tmp=tmp.replaceAll("\n"," ");
        return  "[Da: " +sender + "]    "  + subject + " - " +   tmp;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o!=null && o.getClass()==this.getClass())
        {
                return (this.id.equals(((Email) o).getID()));
        }
         return  false;
    }


    @Override
    public int compareTo(Email o) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(date, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(o.getDate(), formatter);
        return dateTime1.compareTo(dateTime2);
    }



}