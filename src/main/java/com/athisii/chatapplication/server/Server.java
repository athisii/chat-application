package com.athisii.chatapplication.server;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientIP;

    public Server(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;
            socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clientIP = socket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0];
            LOG.log(Level.INFO, () -> clientIP + " connected.");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, () -> "Error creating server.");
            ex.printStackTrace();
            closeEverything();
        }
    }


    public void receiveMessageFromClient(VBox vbox) {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromClient = bufferedReader.readLine();
                    if (messageFromClient == null) {
                        LOG.log(Level.INFO, () -> clientIP + " disconnected.");
                        Platform.runLater(() -> {
                            Dialog<String> dialog = new Dialog<>();
                            dialog.setTitle("Information");
                            dialog.setContentText("Client has already disconnected.");
                            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().add(buttonType);
                            dialog.showAndWait();
                        });
                        closeEverything();
                        return;
                    }
                    ServerController.addLabel(messageFromClient, vbox);
                } catch (IOException ex) {
                    //ignores the exception if user closed the window by clicking the X button.
                    if (!socket.isClosed()) {
                        LOG.log(Level.SEVERE, () -> "Error receiving message from client");
                        ex.printStackTrace();
                    }
                    closeEverything();
                    return;
                }
            }
        }).start();
    }

    public void sendMessageToClient(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ex) {
            //server app remains opened unless user closed the window, even if the client has already disconnected.
            if (!socket.isClosed()) {
                ex.printStackTrace();
                LOG.log(Level.SEVERE, () -> "Error sending message to the client");
                closeEverything();
            }

        }

    }

    public void closeEverything() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (bufferedWriter != null) {
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
