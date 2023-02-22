package client.controller;

import client.model.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReadViewController {

    public TextArea txtText;
    public Label lblTo;
    public Label lblSubject;
    public Label lblFrom;
    public Label lblDate;
    public AnchorPane readPane;

    private ClientModel model;

    @FXML
    public void initialize(){

    }
    @FXML
    public void initModel(ClientModel model) {
        if (this.model != null) {// ensure model is only set once:
            throw new IllegalStateException("[CLIENT] Model can only be initialized once");
        }
        this.model = model;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        lblDate.setText(model.getSelectedEmail().getDate()/*.format(formatter)*/);
        lblTo.setText(model.getSelectedEmail().getReceivers().toString());
        lblFrom.setText(model.getSelectedEmail().getSender());
        lblSubject.setText(model.getSelectedEmail().getSubject());
        txtText.setText(model.getSelectedEmail().getText());
    }

}
