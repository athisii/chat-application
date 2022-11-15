package com.athisii.chatapplication.client;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            LOG.log(Level.INFO, () -> "Successfully connected to the server.");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, () -> "Error creating client");
            ex.printStackTrace();
            closeEverything();
        }

    }

    public void sendMessageToServer(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ex) {
            //client app remains opened unless user clicked the X button, even if server has already shutdown.
            if (!socket.isClosed()) {
                LOG.log(Level.SEVERE, () -> "Error sending message to server.");
                ex.printStackTrace();
                closeEverything();
            }
        }

    }

    public void receiveMessageFromServer(VBox vbMessages) {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer == null) {
                        LOG.log(Level.INFO, () -> "server has already shutdown.");
                        // updates the ui
                        Platform.runLater(() -> {
                            Dialog<String> dialog = new Dialog<>();
                            dialog.setTitle("Information");
                            dialog.setContentText("Server has already shutdown.");
                            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().add(buttonType);
                            dialog.showAndWait();
                        });
                        closeEverything();
                        return;
                    }
                    ClientController.addLabel(messageFromServer, vbMessages);
                } catch (IOException ex) {
                    // ignores the exception if user closed the window by clicking the X button.
                    if (!socket.isClosed()) {
                        LOG.log(Level.SEVERE, () -> "Error receiving message from server.");
                        ex.printStackTrace();
                        closeEverything();
                    }
                    break;
                }
            }

        }).start();

    }

    public void closeEverything() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
