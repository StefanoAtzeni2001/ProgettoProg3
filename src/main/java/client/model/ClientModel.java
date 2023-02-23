package client.model;

import shared.Email;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ClientModel {
    private final ListProperty<Email> inbox; //email in entrata
    private final ObservableList<Email> inboxContent; //contenuto della inbox
    private final StringProperty account;//email dell'account
    private final ObjectProperty<Email> selectedEmail;
    private final StringProperty dest;
    private final StringProperty subject;
    private final StringProperty text;


    public ClientModel(String account){
        this.inboxContent= FXCollections.observableList(new LinkedList<>());
        this.inbox=new SimpleListProperty<>();
        this.inbox.set(inboxContent);
        this.account =new SimpleStringProperty(account);
        this.selectedEmail = new SimpleObjectProperty<Email>();
        this.dest=new SimpleStringProperty();
        this.subject=new SimpleStringProperty();
        this.text=new SimpleStringProperty();
    }

    //return property
    public ListProperty<Email> inboxProperty() {return inbox;}
    public StringProperty accountProperty(){return account;}
    public ObjectProperty<Email> selectedEmailProperty(){return selectedEmail;}
    public StringProperty destProperty(){return dest;}
    public StringProperty subjectProperty(){return subject;}
    public StringProperty textProperty(){return text;}


    //getter
    public Email getSelectedEmail(){return selectedEmail.getValue();}
    public String getAccount(){return account.getValue();}
    public String getDest() {return dest.getValue();}
    public String getSubject() {return subject.getValue();}
    public String getText() {return text.getValue();}


    //setter
    public void setDest(String dest) {this.dest.setValue(dest);}
    public void setSubject(String subject) {this.subject.setValue(subject);}
    public void setText(String text) {this.text.setValue(text);}



    public synchronized void deleteEmail(Email email) {
        inboxContent.remove(email);
    }

    public synchronized void addEmail(Email email){
        inboxContent.add(0,email);
    }

    public synchronized void addAllEmail(List<Email> emails){
        emails.sort(null);
        for (Email email:  emails){
            if (!inboxContent.contains(email))
                inboxContent.add(0,email);
        }
    }
}

