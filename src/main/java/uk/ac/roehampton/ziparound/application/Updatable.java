/**
 * Updatable.java
 * Interface for updatable Controllers.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application;

import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;

import java.io.IOException;

/**
 * An interface that defines a controller to be updatable.
 */
public interface Updatable {
    void update() throws IOException;
    void clear();
    HeaderController getHeaderController();
}
