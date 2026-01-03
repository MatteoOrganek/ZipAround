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

        Utils.log("Currently updating UI...");
        headerController.inBookingView();
        List<Booking> listBooking = Utils.bookingManagerInstance.getBookingArrayList();

        for (Booking booking : listBooking) {

            // TODO check for null user, as the user might have been deleted from db (same for staff and bookable)
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {
                Utils.log("Adding booking #%s".formatted(booking.getID(Utils.currentStaff)));

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/uk/ac/roehampton/ziparound/application/modules/booking-card.fxml")
                );

                Parent bookingCard = loader.load();
                BookingCardController controller = loader.getController();

                // configure the card
                controller.setBooking(booking);

                // attach to UI
                container.getChildren().add(bookingCard);
            }

        }
    }

    @Override
    public void clear() {

    }
}

