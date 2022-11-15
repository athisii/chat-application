package com.athisii.chatapplication.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerController {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @FXML
    private Button btnSend;
    @FXML
    private TextField tfMessage;
    @FXML
    private ScrollPane spMain;
    @FXML
    private VBox vbMessages;

    private Server server;

    // automatically called by the FXMLLoader
    // not needed to implement Initializable
    public void initialize() {
        try {
            server = new Server(new ServerSocket(9000));
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, () -> "Error creating socket.");
        }

        btnSend.setOnAction(actionEvent -> {
            String messageToSend = tfMessage.getText();
            if (!messageToSend.isEmpty()) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));

                Text text = new Text(messageToSend);
                text.setFill(Color.WHITE);

                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color: rgb(15, 125, 242);" +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));

                hBox.getChildren().add(textFlow);
                vbMessages.getChildren().add(hBox);

                server.sendMessageToClient(messageToSend);
                tfMessage.clear();
            }
        });

        vbMessages.heightProperty()
                .addListener((observableValue, oldValue, newValue) -> spMain.setVvalue((double) newValue));

        server.receiveMessageFromClient(vbMessages);
    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        text.setFill(Color.BLACK);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        hbox.getChildren().add(textFlow);

        // updates ui using javafx main thread
        Platform.runLater(() -> vbox.getChildren().add(hbox));

    }

    public Server getServer() {
        return server;
    }
}
