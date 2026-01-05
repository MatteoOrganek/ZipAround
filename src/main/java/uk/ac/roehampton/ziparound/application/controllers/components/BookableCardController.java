package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.controllers.BookingCreationController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.Electric;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.awt.print.Book;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class BookableCardController {


    public ImageView bookableImage;
    public Label bookableText;
    public Label bookableIdText;
    public Boolean selected = false;
    public Bookable currentBookable;
    public VBox root;

    BookingCreationController currentBookingCreationController;


    EventHandler<? super MouseEvent> handler;

    public void setBookable(Bookable bookable, BookingCreationController bookingCreationController) {

        currentBookable = bookable;
        currentBookingCreationController = bookingCreationController;
        // Fill fields

        String name = bookable.getName(Utils.currentStaff);
        name += " " + bookable.getModel(Utils.currentStaff);
        if (bookable instanceof EBike) {
            name += " Ebike";
        } else if (bookable instanceof Scooter) {
            name += " Scooter";
        }
        bookableIdText.setText("%s [#%s]".formatted(name, bookable.getID(Utils.currentStaff)));
        StringBuilder info = new StringBuilder();
        if (bookable instanceof EBike || bookable instanceof Scooter) {
            String model = bookable.getModel(Utils.currentStaff);
            info.append(model);
            info.append(" | ");
            String batteriesAmount = String.valueOf(((Electric) bookable).getAmountOfBatteries(Utils.currentStaff));
            info.append(batteriesAmount);
            info.append((Objects.equals(batteriesAmount, "1")) ? "x battery | " : "x batteries | ");
            String maxPowerKw = String.valueOf(((Electric) bookable).getMaxPowerKw(Utils.currentStaff));
            info.append(maxPowerKw);
            info.append("Kw | ");
            String numberPlate = ((Vehicle) bookable).getNumberPlate(Utils.currentStaff);
            info.append(numberPlate);
            
        } else {
            info.append(((Equipment) bookable).getDescription(Utils.currentStaff));
        }
        bookableText.setText(info.toString());

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.findBookableImagePath(bookable))));

        bookableImage.setImage(image);

    }

    public void select() {
        if (selected) {
            root.setStyle("-fx-cursor: hand; -fx-padding: 0px; -fx-border-width: 2; -fx-border-color: transparent;");
        } else {
            currentBookingCreationController.deselectAllBookable();
            root.setStyle("-fx-cursor: hand; -fx-padding: 0px; -fx-border-width: 2; -fx-border-color: #446356;");
        }
        selected = !selected;
    }

}
