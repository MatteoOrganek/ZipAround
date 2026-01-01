package uk.ac.roehampton.ziparound.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import uk.ac.roehampton.ziparound.Utils;

public class BookingController {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;


    @FXML
    public void initialize() {
        headerController.inBookingView();
    }

    public void createBooking() {
    }
}

