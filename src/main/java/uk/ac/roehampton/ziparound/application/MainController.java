package uk.ac.roehampton.ziparound.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.kordamp.bootstrapfx.scene.layout.Panel;

public class MainController {

    @FXML
    private Panel topPanel;

    @FXML
    private Button button1;

    @FXML
    public void initialize() {
        // Set the panel header/title
        topPanel.setText("Modern JavaFX Example");

        // Add style class
        topPanel.getStyleClass().add("panel-primary");
    }

}
