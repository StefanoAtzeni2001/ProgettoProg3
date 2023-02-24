package server.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServerModel {
    StringProperty log;

    public ServerModel() {
        this.log = new SimpleStringProperty();
        this.log.setValue("");
    }

    public StringProperty logProperty(){return log;}
    public synchronized void  setLog(String s){log.setValue(s);}
    public synchronized String getLog(){return log.getValue();}
}
