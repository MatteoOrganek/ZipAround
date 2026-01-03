package uk.ac.roehampton.ziparound.application;

import java.io.IOException;

public interface Updatable {
    void update() throws IOException;
    void clear();
}
