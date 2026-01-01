package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.users.staff.Staff;

public class HomeController {
    public Label greetText;
    public Button staffButton;

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    public void initialize() {

        headerController.inHomeView();

        if (!(Utils.currentUser instanceof Staff)){
            headerController.hideStaffControls();
        }

        if (Utils.currentStaff != null){
            greetText.setText("Hello %s,\nBook Your Ride in Seconds".formatted(Utils.currentUser.getForeName(Utils.currentStaff)));
        } else {
            greetText.setText("Book Your Ride in Seconds");
        }

    }
}
