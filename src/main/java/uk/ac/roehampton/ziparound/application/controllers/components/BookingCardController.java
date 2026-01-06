/**
 * BookingCardController.java
 * Controller for booking-card.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

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
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * This class Controller controls booking-card.fxml.
 */
public class BookingCardController {

    // Declare variables
    public Label bookingIdText;
    public Label statusText;
    public DatePicker startDatePicker;
    public TextField startTimeField;
    public DatePicker endDatePicker;
    public TextField endTimeField;
    public Label createdOnLabel;
    public Label hintLabel;
    public Label staffLabel;
    public Button saveButton;
    public Button deleteButton;
    public Button cancelButton;
    public HBox staffBox;
    public Label bookableText;
    public Button approveButton;
    public ImageView bookableImage;
    public HBox buttonBox;
    public AnchorPane root;
    public Label hintText;

    private Boolean showingButtons = false;

    private Booking currentBooking;

    // Mouse handler used to keep the onMouseClicked action and reapply it when disabled
    EventHandler<? super MouseEvent> handler;

    /**
     * This function populates and adds logic to the fxml using the data presented by booking.
     * @param booking Booking object used for data population.
     */
    public void setUp(Booking booking) {

        // Set current booking
        currentBooking = booking;

        // Disable bottom right buttons
        buttonBox.setManaged(false);
        buttonBox.setVisible(false);

        // Get bookable from booking
        Bookable bookable = booking.getBookableObject(Utils.currentStaff);

        // Get name ant type of bookable
        String name = bookable.getName(Utils.currentStaff);
        name += " " + bookable.getModel(Utils.currentStaff);
        if (bookable instanceof EBike) {
            name += " Ebike";
        } else if (bookable instanceof Scooter) {
            name += " Scooter";
        }
        // Set bookable name nad id
        bookingIdText.setText("%s [Booking #%s]".formatted(name, booking.getID(Utils.currentStaff)));
        bookableText.setText(name);

        // Get bookable image with path for image based on current bookable
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.findBookableImagePath(bookable))));

        // Set the image in the container
        bookableImage.setImage(image);

        // Get LocalDate and LocalTime from Instant in startTime
        LocalDate startDate = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDate endDate = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime endTime = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDateTime createdOnDate = LocalDateTime.ofInstant(booking.getCreatedOn(Utils.currentStaff), ZoneId.systemDefault());

        // Format LocalTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTimeString = startTime.format(timeFormatter);
        String endTimeString = endTime.format(timeFormatter);
        timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String createdDateString = createdOnDate.format(timeFormatter);


        // Insert Data in Fields
        startDatePicker.setValue(startDate);
        startTimeField.setText(startTimeString);
        endDatePicker.setValue(endDate);
        endTimeField.setText(endTimeString);
        createdOnLabel.setText(createdDateString);

        // Check if the booking ahas been approved
        if (booking.getApproved(Utils.currentStaff)) {
            statusText.setText("Approved");
        } else {
            statusText.setText("Awaiting approval");
        }

        // Hide staff info if not staff
        if (Utils.currentUser instanceof Staff) {
            if (Utils.currentStaff.canModifyBookings()) {
                // Check if the booking has been approved or rejected
                if (booking.getApproved(Utils.currentStaff) && booking.getStaff(Utils.currentStaff) != null) {
                    approveButton.setText("Reject");
                    staffLabel.setText("Approved by %s.".formatted(booking.getStaff(Utils.currentStaff).getFullName(Utils.currentStaff)));
                } else {
                    approveButton.setText("Approve");
                }
            } else {
                // Hide staff label (approved by)
                staffLabel.setManaged(false);
                staffLabel.setVisible(false);
            }
        } else {
            staffBox.setManaged(false);
            staffBox.setVisible(false);
        }

        // Can be edited only if permissions allow
        boolean canEdit = Utils.currentStaff.canModifyBookings();
        boolean canApprove = Utils.currentStaff.canApproveBookings();

        // Disable all entries if user can not edit
        startDatePicker.setDisable(!canEdit);
        startTimeField.setDisable(!canEdit);
        endDatePicker.setDisable(!canEdit);
        endTimeField.setDisable(!canEdit);
        saveButton.setDisable(!canEdit);

        // Disable approve button if staff cannot approve
        approveButton.setDisable(!canApprove);

        // Get on mouse clicked action handler
        handler = root.getOnMouseClicked();
    }

    /**
     * Hides and shows buttons
     */
    public void hideShowButtons() {

        if (showingButtons) {

            // Change style
            root.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");

            // Hide buttons
            buttonBox.setManaged(false);
            buttonBox.setVisible(false);

            // Enables action (after being disabled)
            root.setOnMouseClicked(handler);

        } else {

            // Change style
            root.setStyle("-fx-border-color: #446356; -fx-border-width: 2; -fx-cursor: cursor;");

            // Show buttons
            buttonBox.setManaged(true);
            buttonBox.setVisible(true);

            // Not showing buttons, remove onMouseClicked action handler
            root.setOnMouseClicked(null);
        }

        // Invert boolean
        showingButtons = !showingButtons;

    }

    /**
     * This function saves the content inside the current card, validating the provided time and building a booking class from the ground up.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public void save() throws IOException, InterruptedException {

        Utils.log("Saving...", 3);

        // Validate date (dd-MM-yyyy)

        // Initialize startDate
        LocalDate startDate;
        // Get raw date from the datePicker
        String rawDate = startDatePicker.getEditor().getText();

        // Try parsing the date, if invalid, inform user
        try {
            LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            startDate = startDatePicker.getValue();
        } catch (DateTimeParseException e) {
            hintText.setText("Wrong start date format! Use DD/MM/YYYY");
            return;
        }

        // Initialize endDate
        LocalDate endDate;
        // Get raw date from the datePicker
        rawDate = endDatePicker.getEditor().getText();

        // Try parsing the date, if invalid, inform user
        try {
            LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            endDate = endDatePicker.getValue();
        } catch (DateTimeParseException e) {
            hintText.setText("Wrong end date format! Use DD/MM/YYYY");
            return;
        }

        // Validate time (HH:mm)

        // Initialize startTime
        LocalTime startTime;
        // Try parsing startTime to LocalTime, if it results in an error, inform user
        try {
            startTime = LocalTime.parse(
                    startTimeField.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm")
            );
        } catch (DateTimeParseException e) {
            hintText.setText("Check the time, start time must be in HH:mm format!");
            Utils.log("User did not follow the time format in the Start Time Entry.", 5);
            return;
        }

        // Initialize endTime
        LocalTime endTime;
        try {
            // Try parsing endTime to LocalTime, if it results in an error, inform user
            endTime = LocalTime.parse(
                    startTimeField.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm")
            );
        } catch (DateTimeParseException e) {
            hintText.setText("Check the time, end time must be in HH:mm format!");
            Utils.log("User did not follow the time format in the End Time Entry.", 5);
            return;
        }

        // Combine date and time
        try {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            // Convert to Instant using system timezone
            Instant startInstant = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endDateTime.atZone(ZoneId.systemDefault()).toInstant();

            // Insert start and end time in current booking
            currentBooking.setBookedStartTime(startInstant, Utils.currentStaff);
            currentBooking.setBookedEndTime(endInstant, Utils.currentStaff);

            // Check if the time provided is in order (not startTime > endTime)
            if (Utils.bookingManagerInstance.isTheTimeInOrder(currentBooking)) {
                // Check if the booking is overlapping with another booking with the same id as current booking
                if (Utils.bookingManagerInstance.isOverlapping(currentBooking)) {
                    // Remove approved status
                    currentBooking.setApproved(false, Utils.currentStaff);
                    // Update booking to db
                    Utils.apiBridgeInstance.updateObject(currentBooking);
                    // Hide buttons
                    hideShowButtons();
                } else {
                    hintText.setText("Sorry, this item is booked for that time period!");
                    Utils.log("User tried to book an already booked time slot.", 5);
                }
            } else {
                hintText.setText("Check the date time, end time cannot begin\nbefore start time.");
                Utils.log("User inputted end time before start time or date overlaps with another booking of the same item.", 5);
            }

        } catch (Error e) {
            hintText.setText("Please check your entries.");
            Utils.log("User did not follow the time format in Entries.", 5);
        }

    }

    /**
     * This Button action approves the current booking based on whether it is not and vice versa
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public void approveReject() throws IOException, InterruptedException {
        // Set approved bool based on the opposite of the current status
        boolean approved = !currentBooking.getApproved(Utils.currentStaff);
        currentBooking.setApproved(approved, Utils.currentStaff);
        // Set current staff if approved. If not, pass a null value
        currentBooking.setStaff((approved) ? Utils.currentStaff : null, Utils.currentStaff);
        // Update the booking
        Utils.apiBridgeInstance.updateObject(currentBooking);
    }

    /**
     * This Button action deletes the current booking
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public void delete() throws IOException, InterruptedException {
        // Delete booking if able to do it, if not alert the user
        if (Utils.currentStaff.canDeleteBookings()) Utils.apiBridgeInstance.deleteObject(currentBooking);
        else Utils.alert("Error", "", "Not enough permissions to delete bookings.");
        hideShowButtons();
    }
}
