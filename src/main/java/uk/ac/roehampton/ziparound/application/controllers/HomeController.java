package uk.ac.roehampton.ziparound.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;

public class HomeController {
    public Label greetText;
    public Button staffButton;

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    // TODO Remove Exceptions
    public void initialize() throws IOException, InterruptedException {

        headerController.inHomeView();

        if (!(Utils.currentUser instanceof Staff)){
            headerController.hideStaffControls();
        }
        if (Utils.currentStaff != null){
            greetText.setText("Hello %s,\nBook Your Ride in Seconds".formatted(Utils.currentUser.getForeName(Utils.currentStaff)));
        }

        // TODO Remove this
        Utils.apiDatabaseControllerInstance.getAllBookings();

    }
}
