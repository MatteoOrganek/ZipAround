/**
 * MainApplication.java
 * Handles window management (CSS load, page definition, etc.).
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.database.ApiDatabaseController;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;

import java.util.Map;
import java.util.Objects;

public class MainApplication extends Application {

    public SceneManager sceneController;
    BookingManager bookingManager;


    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));


        Scene scene = new Scene(root);
        scene.getStylesheets().add("modern.css");
        stage.setMinHeight(300);
        stage.setMinWidth(600);
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:src/main/resources/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
        stage.setTitle("Zip Around");
        stage.setScene(scene);
        stage.show();

        sceneController = new SceneManager(stage.getScene());
        Parent testView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("test-view.fxml")));
        sceneController.addScreen("test", (Pane) testView);
        Parent loginView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-view.fxml")));
        sceneController.addScreen("login", (Pane) loginView);
        sceneController.activate("login");


    }
    public static void main(String[] args) {
        launch(args);
    }
}
