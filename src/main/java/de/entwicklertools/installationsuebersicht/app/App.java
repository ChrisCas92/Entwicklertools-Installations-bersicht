package de.entwicklertools.installationsuebersicht.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class App extends Application {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 720);
        stage.setTitle("Entwicklertools Installationsübersicht");
        stage.setScene(scene);
        stage.show();
        LOGGER.info("Application started.");
    }
}
