package uk.ac.roehampton.ziparound.application;

import javafx.fxml.FXML;
import org.kordamp.bootstrapfx.scene.layout.Panel;

public class MainController {

    @FXML
    private Panel topPanel;

    @FXML
    public void initialize() {
        // Set the panel header/title
        topPanel.setText("Modern JavaFX Example");

        // Add style class
        topPanel.getStyleClass().add("panel-primary");
    }
}
