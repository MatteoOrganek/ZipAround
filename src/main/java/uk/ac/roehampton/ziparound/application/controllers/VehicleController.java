package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;

public class VehicleController implements Updatable {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;


    @FXML
    public void initialize() {
        headerController.inVehicleView();
    }

    @Override
    public void update() {

    }

    @Override
    public void clear() {

    }
}

