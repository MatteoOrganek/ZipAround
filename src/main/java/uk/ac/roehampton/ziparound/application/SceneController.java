package uk.ac.roehampton.ziparound.application;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class SceneController {
    private final HashMap<String, Pane> screenMap = new HashMap<>();
    private final Scene main;

    public SceneController(Scene main) {
        this.main = main;
    }

    public void addScreen(String name, Pane pane){
        screenMap.put(name, pane);
    }

    protected void removeScreen(String name){
        screenMap.remove(name);
    }

    public void switchTo(String name){
        main.setRoot( screenMap.get(name) );
    }
}
