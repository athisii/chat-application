package com.athisii.chatapplication.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("server-view.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 640, 480));

        // on closing the app by clicking X button
        primaryStage.setOnCloseRequest(event -> {
            LOG.log(Level.INFO, () -> "Shutting down the app.");
            ServerController serverController = fxmlLoader.getController();
            if (serverController.getServer() != null) {
                serverController.getServer().closeEverything();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
