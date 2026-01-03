package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uk.ac.roehampton.ziparound.application.Updatable;

import java.io.IOException;

public class TestController implements Updatable {

    @FXML
    public Button buttonTest;

    public void test(){
        System.out.println("Testing");
    }

    @FXML
    public void initialize() {
        System.out.println("Test controller ready");
    }

    @Override
    public void update() throws IOException {

    }

    @Override
    public void clear() {

    }
}
