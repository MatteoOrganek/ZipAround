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
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uk.ac.roehampton.ziparound.application.MainApplication;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.database.ApiDatabaseController;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Utils {
    public static String UNAUTHORIZED_ACCESS = "Unauthorized access!";
    public static String UNAUTHORIZED_MODIFICATION = "Unauthorized alteration!";
    public static String NOT_AVAILABLE = "Not available!!";


    public static BookingManager bookingManagerInstance;
    public static ApiDatabaseController apiDatabaseControllerInstance;

    public static Scene rootStage;

    public static User currentUser;
    public static Staff currentStaff;

    public static void initializeInstances() throws IOException {
        setApiDatabaseControllerInstance();
    }

    public static void setBookingManagerInstance() {
        log("New booking manager set!", 3);
        bookingManagerInstance = BookingManager.getInstance(currentStaff);
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

    public static void logBreakpoint(){

        System.out.println();
        log("--------------------------------------------------------------------------------------------------", 1);
        log("Breakpoint reached!", 1);
        log("--------------------------------------------------------------------------------------------------", 1);
        System.out.println();
    }

    public static String hashString(String s) throws NoSuchAlgorithmException {

        // Encrypt string using sha265
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(s.getBytes(StandardCharsets.UTF_8));

        // Reassemble hex from bytes
        StringBuilder hashedString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hashedString.append('0');
            hashedString.append(hex);
        }
        return hashedString.toString();
    }

    public static void changeScene(String name) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(MainApplication.class.getResource(name + "-view.fxml"))
            );

            Stage stage = (Stage) rootStage.getWindow();
            Scene scene = stage.getScene();

            if (scene == null) {
                scene = new Scene(root);
                // add stylesheet once
                scene.getStylesheets().add("/modern.css");
                stage.setScene(scene);
            } else {
                // The root node gets replaced
                scene.setRoot(root);
            }

            log("Heading to %s-view.fxml".formatted(name), 3);
            stage.show();
        } catch (Exception e) {
            log("Failed to change scene: " + e.getMessage(), 5);
            e.printStackTrace();
        }
    }

    public static Instant convertStringToInstant(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Parse to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(s, formatter);
        // Convert to Instant (specify the timezone, e.g., system default)
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

}
