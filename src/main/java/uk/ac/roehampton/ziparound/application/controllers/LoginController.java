package uk.ac.roehampton.ziparound.application.controllers;

import eu.hansolo.toolbox.tuples.Tuple;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import jdk.jshell.execution.Util;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.database.ApiDatabaseController;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginController {
    public Button loginButton;
    public TextField usernameEntry;
    public PasswordField passwordEntry;
    public Label hint;

    @FXML
    public void initialize(){

        // Detect when the user clicks or writes on the entries or when they are focused

        usernameEntry.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> usernameEntry.setUserData(true));
        usernameEntry.addEventFilter(KeyEvent.KEY_PRESSED, e -> usernameEntry.setUserData(true));
        usernameEntry.focusedProperty().addListener((obs, oldVal, newVal) -> {
            Boolean userClicked = (Boolean) usernameEntry.getUserData();
            if (newVal && Boolean.TRUE.equals(userClicked)) {
                clearErrors();
                usernameEntry.setUserData(false); // reset flag
            }
        });
        passwordEntry.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> passwordEntry.setUserData(true));
        passwordEntry.addEventFilter(KeyEvent.KEY_PRESSED, e -> usernameEntry.setUserData(true));
        passwordEntry.focusedProperty().addListener((obs, oldVal, newVal) -> {
            Boolean userClicked = (Boolean) passwordEntry.getUserData();
            if (newVal && Boolean.TRUE.equals(userClicked)) {
                clearErrors();
                passwordEntry.setUserData(false); // reset flag
            }
        });

        // Press Enter on username -> go to password
        usernameEntry.setOnAction(e -> handleLogin());

        // Press Enter on password -> trigger login
        passwordEntry.setOnAction(e -> handleLogin());

    }
    public void handleLogin(){

        // Disable all entries and buttons
        usernameEntry.setDisable(true);
        passwordEntry.setDisable(true);
        loginButton.setDisable(true);

        // Create a Task for background work
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Possibly long login call
                return login();
            }
        };

        loginTask.setOnSucceeded(event -> {
            boolean success = loginTask.getValue();

            // Re-enable all entries and buttons
            usernameEntry.setDisable(false);
            passwordEntry.setDisable(false);
            loginButton.setDisable(false);

            if (success) {
                clearErrors();
                hint.setText("Successful login!");
                Utils.sceneControllerInstance.switchTo("main");
                // Switch scene
            } else {
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

    public boolean login() throws IOException, InterruptedException, NoSuchAlgorithmException {


        // Fetch username from entry
        String username = usernameEntry.getText();
        // Fetch password from entry and hash it
        String password = Utils.hashString(passwordEntry.getText());

        ApiDatabaseController apiDatabaseControllerInstance = Utils.apiDatabaseControllerInstance;

        Utils.log();
        Utils.log("Checking credentials u:%s p:%s...".formatted(username, password), 3);

        List<Map<String, Object>> credentialsList = apiDatabaseControllerInstance.getAll("credentials");

        boolean usernameHit;
        boolean passwordHit;

        // For each credential in list
        for (Map<String, Object> credentialInfo : credentialsList) {

            // Hold current id
            int user_id = -1;

            Utils.log();

            // Reset Hits for each credential
            usernameHit = false;
            passwordHit = false;

            // For each value in credential
            for (Map.Entry<String, Object> entry : credentialInfo.entrySet()) {

                // If the current key is username and the username provided is the same in the db
                if (Objects.equals(entry.getKey(), "username") && Objects.equals(username, entry.getValue())) {
                    Utils.log(entry.getKey() + "/" + entry.getValue(), 2);
                    usernameHit = true;
                }

                // Else if the current key is password_hash and the password hash provided is the same in the db
                else if (Objects.equals(entry.getKey(), "password_hash") && Objects.equals(password.toString(), entry.getValue())) {
                    Utils.log(entry.getKey() + "/" + entry.getValue(), 2);
                    passwordHit = true;
                }

                // No matches found in the current value.
                else {
                    Utils.log(entry.getKey() + "/" + entry.getValue());
                }

                // Get user_id
                if (Objects.equals(entry.getKey(), "user_id")){
                    user_id = Integer.parseInt((String) entry.getValue());
                }

            }

            // If both username and password match, there is no need to continue, return true.
            if (usernameHit && passwordHit) {
                assignGlobalUser(user_id);
                return true;
            }
        }

        Utils.log();

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

    private User assignGlobalUser(Integer user_id) throws IOException, InterruptedException {
        List<User> listUsers = Utils.apiDatabaseControllerInstance.getAllStaff();

        for (User user : listUsers){

            if (Objects.equals(user.getID(new SelfService()), user_id)){
                Utils.log();
                Utils.log("User found!", 2);
                if (user instanceof Staff){
                    Utils.currentUser = user;
                    user.printFullInformation((Staff) user);
                    Utils.log("User is Staff. Heading to Staff View.");
                } else {
                    Utils.log("User is a Customer. Creating selfService. Heading to Customer View.");
                    Utils.currentUser = new SelfService();
                }
                return user;
            }

        }
        return null;
    }
}
