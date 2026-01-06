/**
 * BookableCardController.java
 * Controller for bookable-card.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.controllers.BookingCreationController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.Electric;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;

import java.time.*;
import java.util.Objects;

/**
 * This class Controller controls bookable-card.fxml.
 */
public class BookableCardController {

    // Variables declaration
    public ImageView bookableImage;
    public Label bookableText;
    public Label bookableIdText;
    public Boolean selected = false;
    public Bookable currentBookable;
    public VBox root;

    // Get parent class
    BookingCreationController currentBookingCreationController;

    /**
     * This function populates and adds logic to the fxml using the data presented by bookable.
     * @param bookable Bookable object used for data population.
     * @param bookingCreationController Instance of parent fxml's class.
     */
    public void setUp(Bookable bookable, BookingCreationController bookingCreationController) {

        // Set current bookable and parent instance to the ones given
        currentBookable = bookable;
        currentBookingCreationController = bookingCreationController;

        // Fill name
        String name = bookable.getName(Utils.currentStaff);
        name += " " + bookable.getModel(Utils.currentStaff);
        if (bookable instanceof EBike) {
            name += " Ebike";
        } else if (bookable instanceof Scooter) {
            name += " Scooter";
        }
        // Show name and bookable id
        bookableIdText.setText("%s [#%s]".formatted(name, bookable.getID(Utils.currentStaff)));

        // Fill description
        StringBuilder info = new StringBuilder();
        if (bookable instanceof Vehicle) {
            // Model
            String model = bookable.getModel(Utils.currentStaff);
            info.append(model);
            info.append(" | ");
            // Amount of batteries
            String batteriesAmount = String.valueOf(((Electric) bookable).getAmountOfBatteries(Utils.currentStaff));
            info.append(batteriesAmount);
            info.append((Objects.equals(batteriesAmount, "1")) ? "x battery | " : "x batteries | ");
            // Max power
            String maxPowerKw = String.valueOf(((Electric) bookable).getMaxPowerKw(Utils.currentStaff));
            info.append(maxPowerKw);
            info.append("Kw | ");
            // Number plate
            String numberPlate = ((Vehicle) bookable).getNumberPlate(Utils.currentStaff);
            info.append(numberPlate);
        } else {
            // The current bookable is an Equipment
            info.append(((Equipment) bookable).getDescription(Utils.currentStaff));
        }
        // Add description to bookableText
        bookableText.setText(info.toString());

        // Get bookable image with path for image based on current bookable
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.findBookableImagePath(bookable))));

        // Set the image in the container
        bookableImage.setImage(image);

    }

    /**
     * This function selects and deselects the root card, whe informing the user if a card has not been selected and disable/enable next button.
     */
    public void select() {
        // User clicked on the card
        if (selected) {
            // Change card style
            root.setStyle("-fx-cursor: hand; -fx-padding: 0px; -fx-border-width: 2; -fx-border-color: transparent;");
            // Disable next button
            currentBookingCreationController.nextButton.setDisable(true);
            // Inform user
            currentBookingCreationController.hintText.setText("Please select an item.");
        // User deselected the card
        } else {
            // Change style
            root.setStyle("-fx-cursor: hand; -fx-padding: 0px; -fx-border-width: 2; -fx-border-color: #446356;");
            // Deselect all other cards
            currentBookingCreationController.deselectAllBookable();
            // Disable next button
            currentBookingCreationController.nextButton.setDisable(false);
            // Remove hint
            currentBookingCreationController.hintText.setText("");
        }
        // Invert selection
        selected = !selected;
    }

}
