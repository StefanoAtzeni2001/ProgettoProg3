package client.model;

import Shared.Email;
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

    public ClientModel(String account){
        this.inboxContent= FXCollections.observableList(new LinkedList<>());
        this.inbox=new SimpleListProperty<>();
        this.inbox.set(inboxContent);
        this.account =new SimpleStringProperty(account);
        this.selectedEmail = new SimpleObjectProperty<Email>();
    }

    //@return lista di email
    public ListProperty<Email> inboxProperty() {return inbox;}

    //@return email dell'account
    public StringProperty accountProperty(){return account;}
    public String getAccount(){return account.getValue();}
    public ObjectProperty<Email> selecetedEmailProperty(){return selectedEmail;}

    public Email getSelectedEmail(){return selectedEmail.getValue();}



    public void deleteEmail(Email email) {
        inboxContent.remove(email);
    }







    // genera qualche email random.
    public void generateEmails(int n){
        String[] people = new String[] {"paolo.verdi@gmail.com", "Alessandro.rossi@gmail.com", "Enrico.bianchi@gmail.com", "Giulia.neri@edu.unito.it", "Gaia.deLuigi@libero.it", "Simone.viola@unito.it"};
        String[] subjects = new String[] {
                "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                "Non dimenticare...", "Domani scuola" };
        String[] texts = new String[] {
                "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo"
        };
        Random r = new Random();
        for (int i=0; i<n; i++) {
            Email email = new Email(
                    i,
                    people[r.nextInt(people.length)],
                    List.of(people[r.nextInt(people.length)],people[r.nextInt(people.length)],people[r.nextInt(people.length)]),
                    subjects[r.nextInt(subjects.length)],
                    texts[r.nextInt(texts.length)],
                    LocalDateTime.now());
            inboxContent.add(email);
        }
    }
    }

