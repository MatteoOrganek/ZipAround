/**
 * BookingController.java
 * Controller for booking.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.BookingCardController;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Booking;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * This class Controller controls booking.fxml.
 */
public class BookingController implements Updatable {

    // Initialize the container
    @FXML public VBox container;

    // Needed to prevent header controller to be null
    @FXML Parent header;

    // Fetch HeaderController
    @FXML private HeaderController headerController;

    // On initialization update
    @FXML
    public void initialize() throws IOException {
        update();
    }

    // Update UI
    @Override
    public void update() throws IOException {

        // Clear all Bookings
        clear();
        Utils.log("Updating UI...", 3);
        // Change header buttons layout
        headerController.inBookingView();

        // Get all bookings and save it in a list
        List<Booking> listBooking = Utils.bookingManagerInstance.getBookingArrayList();
        // Sort list based on id using stream
        List<Booking> sortedListBooking =
                listBooking.stream()
                        .sorted(Comparator.comparingInt(o -> ((Booking)o).getID(Utils.currentStaff)).reversed())
                        .toList();
        // For each booking in the list
        for (Booking booking : sortedListBooking) {

            // If the current booking's id match
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {

                // Create new loader for booking-card.fxml
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/uk/ac/roehampton/ziparound/application/modules/booking-card.fxml")
                );

                // Load the loader
                Parent bookingCard = loader.load();

                // Get controller
                BookingCardController controller = loader.getController();

                // Configure the card
                controller.setUp(booking);

                // Attach to UI
                container.getChildren().add(bookingCard);

                // Disable if it is not available
                bookingCard.setDisable(!booking.getBookableObject(Utils.currentStaff).isAvailable(Utils.currentStaff));
            }

        }
    }

    // Clear all children in container
    @Override
    public void clear() {
        // Remove all items in container
        container.getChildren().clear();
        Utils.log("UI Cleared.", 3);
    }

    // Give current header controller
    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    /**
     * Button action that sends the user to the new booking view
     */
    public void goToBookView() {
        Utils.changeScene("booking-creation");
    }
}

