package client.controller;

import javafx.scene.control.Alert;

public class Dialogs {
    public static void showInfoDialog(String header,String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText(msg);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showWarningDialog(String header,String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public static void showErrorDialog(String header,String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showErrorDialog(String msg){
        showErrorDialog("Operation failed :(",msg);
    }
    public static void showInfoDialog(String msg){
        showInfoDialog("Operation Completed :)",msg);
    }
    public static void showWarningDialog(String msg){
        showWarningDialog("Something is wrong ...",msg);
    }


}
