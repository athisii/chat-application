module com.athisii.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.athisii.chatapplication.server to javafx.fxml;
    opens com.athisii.chatapplication.client to javafx.fxml;

    exports com.athisii.chatapplication.server;
    exports com.athisii.chatapplication.client;
}