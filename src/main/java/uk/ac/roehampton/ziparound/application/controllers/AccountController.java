package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;

import java.io.IOException;

public class AccountController implements Updatable {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;


    @FXML
    public void initialize() throws IOException {
        update();
    }

    @Override
    public void update() throws IOException {
        headerController.inAccountView();
    }

    @Override
    public void clear() {

    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }
}

