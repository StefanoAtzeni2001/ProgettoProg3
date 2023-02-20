package Shared;

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
        return "{" +msg+"} " + emails;
    }
}
