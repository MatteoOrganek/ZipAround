/**
 * Utils.java
 * General collection of utility functions / localization variables
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 *
 */

package uk.ac.roehampton.ziparound;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import uk.ac.roehampton.ziparound.application.MainApplication;
import uk.ac.roehampton.ziparound.application.SceneController;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.database.ApiDatabaseController;

import java.io.IOException;
import java.util.Objects;

public class Utils {
    public static String UNAUTHORIZED_ACCESS = "Unauthorized access!";
    public static String UNAUTHORIZED_MODIFICATION = "Unauthorized alteration!";
    public static String NOT_AVAILABLE = "Not available!!";


    public static BookingManager bookingManagerInstance;
    public static ApiDatabaseController apiDatabaseControllerInstance;
    public static SceneController sceneControllerInstance;

    public static void initializeInstances() throws IOException {
        setApiDatabaseControllerInstance();

        // TODO Make sure that BookingManager is instantiated after successful login
        // setBookingManagerInstance();


        Parent loginView = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("login-view.fxml")));
        sceneControllerInstance.addScreen("login", (Pane) loginView);

        Parent mainView = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-view.fxml")));
        sceneControllerInstance.addScreen("main", (Pane) mainView);
    }

    public static void setBookingManagerInstance() {
        bookingManagerInstance = BookingManager.getInstance();
    }

    public static void setApiDatabaseControllerInstance() {
        apiDatabaseControllerInstance = ApiDatabaseController.getInstance();
    }


    public static void log(String string, Integer signal){
        switch (signal){
            case 1:
                // ERROR
                System.out.println("[!] " + string);
                break;
            case 2:
                // SUCCESS
                System.out.println("[V] " + string);
                break;
            case 3:
                // INFO
                System.out.println("[i] " + string);
                break;
            case 4:
                // UNKNOWN
                System.out.println("[?] " + string);
                break;
            case 5:
                // FAIL
                System.out.println("[X] " + string);
                break;

        }

    }

    public static void log(String string){
        System.out.println("[ ] " + string);
    }

    public static void log(){
        System.out.println(" ");
    }
}
