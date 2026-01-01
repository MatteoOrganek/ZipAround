package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.ac.roehampton.ziparound.Utils;

public class HeaderController {
    public Button bookingsButton;
    public Button vehicleButton;
    public Button accountButton;
    public Button homeButton;
    public Button staffButton;
    public Button customerButton;
    public Label logo;

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

    public void goToVehicleView() {
        Utils.changeScene("vehicle");
    }

    public void goToAccountView() {
        Utils.changeScene("account");
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
        homeButton.setManaged(false);
        homeButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);

    }

    public void inStaffView() {
        reset();
        logo.setText("Staff Portal");
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        bookingsButton.setManaged(false);
        bookingsButton.setVisible(false);
        vehicleButton.setManaged(false);
        vehicleButton.setVisible(false);
        homeButton.setManaged(false);
        homeButton.setVisible(false);

    }

    public void inBookingView() {
        reset();
        bookingsButton.setManaged(false);
        bookingsButton.setVisible(false);
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);

    }

    public void inVehicleView() {
        reset();
        vehicleButton.setManaged(false);
        vehicleButton.setVisible(false);
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);
    }

    public void inAccountView() {
        reset();
        accountButton.setManaged(false);
        accountButton.setVisible(false);
        staffButton.setManaged(false);
        staffButton.setVisible(false);
        customerButton.setManaged(false);
        customerButton.setVisible(false);
    }

    public void reset() {
        logo.setText("ZipAround");
        vehicleButton.setManaged(true);
        vehicleButton.setVisible(true);
        accountButton.setManaged(true);
        accountButton.setVisible(true);
        customerButton.setManaged(true);
        customerButton.setVisible(true);
        bookingsButton.setManaged(true);
        bookingsButton.setVisible(true);
    }

}
