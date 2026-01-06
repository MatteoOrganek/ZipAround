/**
 * HeaderController.java
 * Controller for header.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import uk.ac.roehampton.ziparound.Utils;

import java.io.IOException;

/**
 * This class Controller controls header.fxml. It enables and disables buttons based on the current page selection
 */
public class HeaderController {

    // Declare variables
    public Button bookingsButton;
    public Button homeButton;
    public Button staffButton;
    public Button customerButton;
    public ProgressBar loadBar;
    public Label logo;
    public Label currentText;

    /**
     * Logs the user out and resets current staff and user
     */
    public void logout(){
        Utils.currentStaff = null;
        Utils.currentUser = null;
        Utils.changeScene("login");
    }

    public void goToHomeView() {
        Utils.changeScene("home");
    }

    public void goToBookingView() {
        Utils.changeScene("booking");
    }

    public void goToStaffView() {
        Utils.changeScene("staff");
    }

    public void goToCustomerView() {
        Utils.changeScene("home");
    }

    public void hideStaffControls() {
        staffButton.setManaged(false);
        staffButton.setVisible(false);

    }

    public void inHomeView() {
        reset();
        currentText.setText("Home");
        homeButton.setManaged(false);
        homeButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);

    }

    public void inStaffView() {
        reset();
        logo.setText("Staff Portal");
        currentText.setText(Utils.currentStaff.getFullName(Utils.currentStaff));
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        bookingsButton.setManaged(false);
        bookingsButton.setVisible(false);
        homeButton.setManaged(false);
        homeButton.setVisible(false);

    }

    public void inBookingView() {
        reset();
        currentText.setText("Current Bookings");
        bookingsButton.setManaged(false);
        bookingsButton.setVisible(false);
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);

    }

    public void inBookingCreationView() {
        reset();
        currentText.setText("New Booking");
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);

    }

    public void reset() {
        logo.setText("ZipAround");
        currentText.setText("");
        customerButton.setManaged(true);
        customerButton.setVisible(true);
        bookingsButton.setManaged(true);
        bookingsButton.setVisible(true);
    }


    @FXML
    public void initialize() throws IOException {
        // Set loadbar progress to zero, effectively hiding it
        loadBar.setProgress(0);
    }

    public void update() {
        // Update data, followed by the UI
        Utils.apiBridgeInstance.update();
    }
}
