package uk.ac.roehampton.ziparound.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import uk.ac.roehampton.ziparound.Utils;

public class TestController {

    @FXML
    public Button buttonTest;

    public void test(){
        Utils.log("Testing");
    }

    @FXML
    public void initialize() {

    }
}
