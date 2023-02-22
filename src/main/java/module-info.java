module org.progettoprog3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens shared to com.google.gson;


    opens client to javafx.fxml;
    exports client;
    opens client.controller to javafx.fxml;
    exports client.controller;


    opens server to javafx.fxml;
    exports server;
    opens server.controller to javafx.fxml;
    exports server.controller;

}