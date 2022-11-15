package com.athisii.chatapplication.client;

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
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("client-view.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 640, 480));

        // on closing the app by clicking X button
        primaryStage.setOnCloseRequest(windowEvent -> {
            LOG.log(Level.INFO, () -> "Shutting down the app.");
            ClientController controller = fxmlLoader.getController();
            if (controller.getClient() != null) {
                controller.getClient().closeEverything();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
