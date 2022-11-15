package com.athisii.chatapplication.client;

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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    @FXML
    private Button btnSend;
    @FXML
    private TextField tfMessage;
    @FXML
    private ScrollPane spMain;
    @FXML
    private VBox vbMessages;
    private Client client;

    // automatically called by the FXMLLoader
    // not needed to implement Initializable
    public void initialize() {
        try {
            client = new Client(new Socket("localhost", 9000));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, () -> """
                      Error creating socket.
                    Please make sure the server is up and running at the specified port number.
                    Exiting...
                    """);
            System.exit(1);
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

                client.sendMessageToServer(messageToSend);
                tfMessage.clear();
            }
        });

        vbMessages.heightProperty()
                .addListener((observableValue, oldValue, newValue) -> spMain.setVvalue((double) newValue));

        client.receiveMessageFromServer(vbMessages);
    }

    public static void addLabel(String messageFromServer, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromServer);
        text.setFill(Color.BLACK);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        hbox.getChildren().add(textFlow);

        // updates ui using javafx main thread
        Platform.runLater(() -> vbox.getChildren().add(hbox));
    }

    public Client getClient() {
        return client;
    }
}
