/**
 * LoginController.java
 * Controller for login.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.database.ApiBridge;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class Controller controls login.fxml.
 */
public class LoginController {

    // Variables declaration
    public Button loginButton;
    public TextField usernameEntry;
    public PasswordField passwordEntry;
    public Label hint;
    public BorderPane rootStage;

    // On initialization, update
    @FXML
    public void initialize(){
        update();
    }

    /**
     * This function handles the login by checking for any errors (empty entry, wrong credentials)
     */
    public void login(){

        // Check if either entries are empty, if so notify user
        if (Objects.equals(usernameEntry.getText(), "") || Objects.equals(passwordEntry.getText(), "")){
            // Show error state to the user
            showError();
            hint.setText("Please fill out both entries.");

        } else {

            // Disable all entries and buttons
            usernameEntry.setDisable(true);
            passwordEntry.setDisable(true);
            loginButton.setDisable(true);

            // Create a Task for background work
            Task<Boolean> loginTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    // Possibly long login call
                    return checkCredentials();
                }
            };

            // If the task succeeded
            loginTask.setOnSucceeded(event -> {
                boolean success = loginTask.getValue();

                // Re-enable all entries and buttons
                usernameEntry.setDisable(false);
                passwordEntry.setDisable(false);
                loginButton.setDisable(false);

                if (success) {
                    // Reset buttons and labels
                    clearErrors();
                    // Log successful login
                    hint.setText("Successful login!");
                    // Initialize booking manager
                    Utils.setBookingManagerInstance();
                    // Change current scene to main
                    Utils.changeScene("home");
                } else {
                    // Notify user
                    Utils.log("Unsuccessful login!", 5);
                    showError();
                }

            });

            // Error handling
            loginTask.setOnFailed(event -> {
                Throwable ex = loginTask.getException();
                ex.printStackTrace();
            });

            // Run task in a background thread
            new Thread(loginTask).start();
        }
    }

    /**
     * This function takes the credentials as input and compares them to the
     *
     * @return Whether the function validated the credentials successfully or not.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public boolean checkCredentials() throws IOException, InterruptedException {


        // Fetch username from entry
        String username = usernameEntry.getText();
        // Fetch password from entry and hash it
        String password_hash = Utils.hashString(passwordEntry.getText());

        Utils.log("Checking credentials u:%s p:%s...".formatted(username, password_hash), 3);

        // Get a map of data from the table called credentials
        List<Map<String, Object>> listMaps = Utils.apiBridgeInstance.getAll("credentials");

        if (!listMaps.isEmpty()){
            Utils.log("User data successfully fetched.", 2);
        } else {
            Utils.log("No user data found!", 1);
        }

        // For each credential in list
        for (Map<String, Object> credentialInfo : listMaps) {

            // Check credentials
            if (Objects.equals((String) credentialInfo.get("username"), username) && Objects.equals((String) credentialInfo.get("password_hash"), password_hash)) {

                Utils.log("Credentials successfully validated.", 2);

                // Assign Utils' currentUser and currentStaff using the current user_id
                assignGlobalUser(Integer.parseInt((String) credentialInfo.get("user_id")));

                return true;
            }
        }

        // No matches found in all credentials. Return false.
        return false;
    }

    private void showError(){
        // Add error style
        usernameEntry.getStyleClass().add("error");
        passwordEntry.getStyleClass().add("error");
        hint.setText("Wrong credentials!");
        loginButton.requestFocus();
    }

    private void clearErrors(){
        // Remove error style
        usernameEntry.getStyleClass().remove("error");
        passwordEntry.getStyleClass().remove("error");
        hint.setText("");
    }

    /**
     * This function fetches the current user and staff based on the user id
     * @param user_id User's id
     * @return User object
     */
    private User assignGlobalUser(Integer user_id) {

        // Get all users list by adding Staff and customers together
        List<User> listUsers = new ArrayList<>();
        listUsers.addAll(Utils.bookingManagerInstance.getStaffArrayList());
        listUsers.addAll(Utils.bookingManagerInstance.getCustomerArrayList());

        // For each user in list
        for (User user : listUsers){
            // If the ids match
            if (Objects.equals(user.getID(new SelfService()), user_id)){
                Utils.log("User found!", 2);
                // If user is staff
                if (user instanceof Staff){
                    // Assign user to current user and staff
                    Utils.currentUser = user;
                    Utils.currentStaff = (Staff) user;
                    try {
                        user.printFullInformation((Staff) user);
                    } catch (SecurityException e) {
                        Utils.log("Could not fetch staff data, not enough permissions!");
                    }
                    Utils.log("Successful login! User is Staff.");
                } else {
                    // User is not staff
                    Utils.log("Successful login! User is a Customer. Creating selfService.");
                    Utils.currentUser = user;
                    // Assign self-service to user
                    Utils.currentStaff = new SelfService();
                }
                return user;
            }

        }
        return null;
    }

    // Update UI
    public void update() {

        // Detect when the user clicks or writes on the entries or when they are focused by raising flags

        usernameEntry.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> usernameEntry.setUserData(true));
        usernameEntry.addEventFilter(KeyEvent.KEY_PRESSED, e -> usernameEntry.setUserData(true));
        // Setup a listener for any change in flag value
        usernameEntry.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // If the user clicked or entered any keys, clear any error states and reset flag
            Boolean userClicked = (Boolean) usernameEntry.getUserData();
            if (newVal && Boolean.TRUE.equals(userClicked)) {
                clearErrors();
                usernameEntry.setUserData(false); // reset flag
            }
        });
        passwordEntry.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> passwordEntry.setUserData(true));
        passwordEntry.addEventFilter(KeyEvent.KEY_PRESSED, e -> usernameEntry.setUserData(true));
        // Setup a listener for any change in flag value
        passwordEntry.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // If the user clicked or entered any keys, clear any error states and reset flag
            Boolean userClicked = (Boolean) passwordEntry.getUserData();
            if (newVal && Boolean.TRUE.equals(userClicked)) {
                clearErrors();
                passwordEntry.setUserData(false); // reset flag
            }
        });

        // When enter is pressed, go to password entry
        usernameEntry.setOnAction(e -> passwordEntry.requestFocus());

        // When enter is pressed in the password entry, login
        passwordEntry.setOnAction(e -> login());
    }
}
