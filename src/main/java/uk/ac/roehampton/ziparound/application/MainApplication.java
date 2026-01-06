/**
 * MainApplication.java
 * Handles window management (FXML pages, CSS load, Window size etc.).
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.ac.roehampton.ziparound.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

/**
 * This class handles the main application configuration, such as root page, CSS handling and Initial setup/update.
 */
public class MainApplication extends Application {

    // All images courtesy from https://rapidscooter.co.uk

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize instances (ApiDatabaseController and BookingManager)
        Utils.initializeInstances();

        // Declare root
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-view.fxml")));

        // Create base scene
        Scene scene = new Scene(root);

        // Select css style
        scene.getStylesheets().add("modern.css");

        // Setup Stage
        stage.setMinHeight(300);
        stage.setMinWidth(600);
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:src/main/resources/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
        stage.setTitle("Owres - Zip Around");
        stage.setScene(scene);
        stage.show();

        // Add initial rootScene and currentScene to Utils
        Utils.rootStage = stage.getScene();
        Utils.currentScene = stage.getScene();

        // Tray icon config
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image awtIcon = ImageIO.read(getClass().getResource("/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
            TrayIcon trayIcon = new TrayIcon(awtIcon, "ZipAround");
            tray.add(trayIcon);
        }

        // Fetch data from db
        Utils.apiBridgeInstance.update();

        // Switch to Log in scene
        Utils.changeScene("login");



    }
    public static void main(String[] args) {
        launch(args);
    }

}
