package uk.ac.roehampton.ziparound.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import java.awt.event.KeyEvent;

public class MainController {

    @FXML
    private Panel topPanel;

    @FXML
    public void initialize() {
    }

    public void onEnter(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getEventType());
    }
    public void onExit(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getEventType());
    }
}
