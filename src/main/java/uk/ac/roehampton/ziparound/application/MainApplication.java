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
import javafx.stage.Stage;
import jdk.jshell.execution.Util;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;

import java.util.List;
import java.util.Objects;

public class MainApplication extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        // TODO Remove ghost login
        Utils.currentUser = new Admin(-1, "Matteo", "Organek", "Admin");
        Utils.currentStaff = new Admin(-1, "Matteo", "Organek", "Admin");
        Utils.setBookingManagerInstance();

        // Declare root
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));

        // Create base scene
        Scene scene = new Scene(root);

        // Select css style
        scene.getStylesheets().add("modern.css");

        // Setup Stage
        stage.setMinHeight(300);
        stage.setMinWidth(600);
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:src/main/resources/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
        stage.setTitle("Zip Around");
        stage.setScene(scene);
        stage.show();

        Utils.rootStage = stage.getScene();


        // Initialize instances (ApiDatabaseController and BookingManager)
        Utils.initializeInstances();

        // Switch to Log in scene
        // TODO Change to login when done testing
        Utils.changeScene("home");


        // TODO Remove this
        Utils.apiDatabaseControllerInstance.update();
        List<Bookable> bookableList = Utils.bookingManagerInstance.getBookableArrayList();

        for (Bookable bookable : bookableList){
            bookable.printInfo(Utils.currentStaff);
        }

    }
    public static void main(String[] args) {
        launch(args);
    }

}
