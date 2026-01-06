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

public class BookingController implements Updatable {

    public VBox container;

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    public void initialize() throws IOException {
        update();
    }

    @Override
    public void update() throws IOException {

        clear();

        Utils.log("Updating UI...", 3);

        headerController.inBookingView();
        List<Booking> listBooking = Utils.bookingManagerInstance.getBookingArrayList();
        List<Booking> sortedListBooking =
                listBooking.stream()
                        .sorted(Comparator.comparingInt(o -> ((Booking)o).getID(Utils.currentStaff)).reversed())
                        .toList();

        for (Booking booking : sortedListBooking) {

            // TODO check for null user, as the user might have been deleted from db (same for staff and bookable)
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/uk/ac/roehampton/ziparound/application/modules/booking-card.fxml")
                );

                Parent bookingCard = loader.load();
                BookingCardController controller = loader.getController();

                // Configure the card
                controller.setBooking(booking);

                // Attach to UI
                container.getChildren().add(bookingCard);
            }

        }
    }

    @Override
    public void clear() {
        // Remove all items in container
        container.getChildren().clear();
        Utils.log("UI Cleared.", 3);
    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    public void goToBookView() {
        Utils.changeScene("booking-creation");
    }
}

