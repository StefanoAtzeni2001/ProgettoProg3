package client.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ClientModel {
    private final ListProperty<Email> inbox; //email in entrata
    private final ObservableList<Email> inboxContent; //contenuto della inbox
    private final StringProperty emailAddress;//email dell'account

    public ClientModel(String emailAddress){
        this.inboxContent= FXCollections.observableList(new LinkedList<>());
        this.inbox=new SimpleListProperty<>();
        this.inbox.set(inboxContent);
        this.emailAddress=new SimpleStringProperty(emailAddress);
    }

    //@return lista di email
    public ListProperty<Email> inboxProperty() {return inbox;}

    //@return email dell'account
    public StringProperty emailAddressProperty(){return emailAddress;}

    public void deleteEmail(Email email) {
        inboxContent.remove(email);
    }







    // genera qualche email random.
    public void generateEmails(int n){
        String[] people = new String[] {"Paolo", "Alessandro", "Enrico", "Giulia", "Gaia", "Simone"};
        String[] subjects = new String[] {
                "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                "Non dimenticare...", "Domani scuola" };
        String[] texts = new String[] {
                "È necessario che ci parliamo di persona, per mail rischiamo sempre fraintendimenti",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo"
        };
        Random r = new Random();
        for (int i=0; i<n; i++) {
            Email email = new Email(
                    people[r.nextInt(people.length)],
                    List.of(people[r.nextInt(people.length)]),
                    subjects[r.nextInt(subjects.length)],
                    texts[r.nextInt(texts.length)]);
            inboxContent.add(email);
        }
    }
    }

