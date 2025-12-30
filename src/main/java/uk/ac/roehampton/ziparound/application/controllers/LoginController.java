package uk.ac.roehampton.ziparound.application.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    public Button loginButton;
    public TextField usernameEntry;
    public PasswordField passwordEntry;

    public void login(){
        System.out.println("Logging in!");
        usernameEntry.getStyleClass().add("error");
        usernameEntry.getStyleClass().add("error");
        passwordEntry.getStyleClass().remove("error");
    }
}
