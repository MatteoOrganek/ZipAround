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
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import uk.ac.roehampton.ziparound.application.MainApplication;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.database.ApiBridge;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/**
 * Observable Helper class that contains general functions used to aid other classes
 */
public class Utils {

    // Variables declaration

    public static String UNAUTHORIZED_ACCESS = "Unauthorized access!";
    public static String UNAUTHORIZED_MODIFICATION = "Unauthorized alteration!";
    public static String NOT_AVAILABLE = "Not available!";

    public static BookingManager bookingManagerInstance;
    public static ApiBridge apiBridgeInstance;

    public static Scene rootStage;
    public static Scene currentScene;
    public static Updatable currentController;

    public static User currentUser;
    public static Staff currentStaff;

    /**
     * This function initializes the booking manager and api bridge singleton instance
     */
    public static void initializeInstances() {
        Utils.currentStaff = new SelfService();
        setBookingManagerInstance();
        Utils.log("Booking instance initialized.", 3);
        setApiBridgeInstance();
        Utils.log("API Bridge instance initialized.", 3);
    }


    public static void setBookingManagerInstance() {
        bookingManagerInstance = BookingManager.getInstance(currentStaff);
    }

    public static void setApiBridgeInstance() {
        apiBridgeInstance = ApiBridge.getInstance();
    }


    /**
     * This function is used instead of sout, as it makes logs more readable.
     * @param string String to log
     * @param signal Symbol used to convey importance or output
     */
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

    // Variation of log for printing with no signal
    public static void log(String string){
        System.out.println("[ ] " + string);
    }

    // Variation of log used for printing an empty line
    public static void log(){
        System.out.println(" ");
    }

    // Variation of log used for testing
    public static void logBreakpoint(){

        System.out.println();
        log("--------------------------------------------------------------------------------------------------", 1);
        log("Breakpoint reached!", 1);
        log("--------------------------------------------------------------------------------------------------", 1);
        System.out.println();
    }

    /**
     * This function changes the current scene by switching the root stage.
     * @param name String of the name of the view
     */
    public static void changeScene(String name) {
        try {

            // Create loader
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(MainApplication.class.getResource(name + "-view.fxml"))
            );

            // Load loader
            Parent root = loader.load();

            // Get the controller (must implement Updatable)
            Object controller = loader.getController();
            if (controller instanceof Updatable updatable) {
                currentController = updatable;
            } else {
                currentController = null;
            }

            // Setup current scene
            Stage stage = (Stage) rootStage.getWindow();
            Scene scene = stage.getScene();
            currentScene = scene;

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
            // Show the stage
            stage.show();

        } catch (Exception e) {
            log("Failed to change scene: " + e.getMessage(), 5);
            e.printStackTrace();
        }
    }


    /**
     * THis function inputs a bookable object and is able to output the model's path
     * @param bookable Bookable object to use
     * @return Image path of the bookable object
     */
    public static String findBookableImagePath(Bookable bookable) {
        if (bookable instanceof Equipment) {
            switch (bookable.getModel(Utils.currentStaff)) {
                // Helmets
                case "Six Peaks":
                    return "/uk/ac/roehampton/ziparound/application/imgs/bookable/equipment/six_peaks_adult_cycling_helmet.png";
                case "HJC Ibex 3":
                    return "/uk/ac/roehampton/ziparound/application/imgs/bookable/equipment/hjc_ibex_3_helmet.png";
                // Chains
                case "Oxford Hardcore":
                    return "/uk/ac/roehampton/ziparound/application/imgs/bookable/equipment/oxford_hardcore_xc13_chain_lock.png";
                case "Oxford Alarm":
                    return "/uk/ac/roehampton/ziparound/application/imgs/bookable/equipment/oxford_alarm_chain.png";
                // Air Pumps
                case "Visio":
                    return "/uk/ac/roehampton/ziparound/application/imgs/bookable/equipment/air_pump.png";
            }
        } else {
            if (bookable instanceof Scooter) {
                switch (bookable.getModel(Utils.currentStaff)) {
                    case "D8 Pro":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/scooters/iwheels_d8_pro.png";
                    case "Kukirin G2 Master":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/scooters/kugoo_kukirin_g2_master.png";
                    case "VS6 Pro":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/scooters/vipcoo_vs6_pro.png";
                }
            }

            if (bookable instanceof EBike) {
                switch (bookable.getModel(Utils.currentStaff)) {
                    case "Nova":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/bikes/cyrusher_nova.png";
                    case "Rumble 2":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/bikes/cyrusher_rumble_2.png";
                    case "One":
                        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/bikes/raleigh_one.png";
                }
            }
        }
        return "/uk/ac/roehampton/ziparound/application/imgs/bookable/vehicles/bikes/raleigh_one.png";
    }

    /**
     * This function is used to create a popup alert that informs the user.
     */
    public static void alert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Function used to hash a string using SHA-256
     * @param s String to be hashed
     * @return Hashed string
     */
    public static String hashString(String s) {

        // Encrypt string using sha265
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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


    /**
     * This function is able to determine if a string is an Integer
     * @param str String to check
     * @return Numericness
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
