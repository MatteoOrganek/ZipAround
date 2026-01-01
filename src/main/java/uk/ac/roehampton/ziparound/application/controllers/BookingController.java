package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.controllers.components.BookingCardController;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Booking;

import java.io.IOException;
import java.util.List;

public class BookingController {

    public VBox container;
    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;


    @FXML
    public void initialize() throws IOException {


        headerController.inBookingView();
        List<Booking> listBooking = Utils.bookingManagerInstance.getBookingArrayList();
        for (Booking booking : listBooking) {
            Utils.log("Adding booking #%s".formatted(booking.getID(Utils.currentStaff)));

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/uk/ac/roehampton/ziparound/application/components/booking-card.fxml")
            );

            Parent bookingCard = loader.load();
            BookingCardController controller = loader.getController();

            // configure the card
            controller.setBooking(booking);

            // attach to UI
            container.getChildren().add(bookingCard);

        }
    }

    public void createBooking() {
    }
}

