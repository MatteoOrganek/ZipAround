package uk.ac.roehampton.ziparound.application;

import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;

import java.io.IOException;

public interface Updatable {
    void update() throws IOException;
    void clear();
    HeaderController getHeaderController();
}
