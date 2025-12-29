package uk.ac.roehampton.ziparound.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

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
