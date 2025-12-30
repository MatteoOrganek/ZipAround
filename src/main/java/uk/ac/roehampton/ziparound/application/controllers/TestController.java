package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TestController {

    @FXML
    public Button buttonTest;

    public void test(){
        System.out.println("Testing");
    }

    @FXML
    public void initialize() {
        System.out.println("Test controller ready");
    }
}
