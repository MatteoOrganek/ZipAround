package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public class AccountController {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;


    @FXML
    public void initialize() {
        headerController.inAccountView();
    }

}

