package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.event.ActionEvent;
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
import jdk.jshell.execution.Util;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.controllers.StaffController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class BookingCardController {

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
    public ImageView bikeImage;
    public HBox buttonBox;
    public AnchorPane root;
    public Label hintText;

    private Boolean showingButtons = false;

    private Booking currentBooking;


    EventHandler<? super MouseEvent> handler;

    public void setBooking(Booking booking) {

        currentBooking = booking;

        // Fill fields

        buttonBox.setManaged(false);
        buttonBox.setVisible(false);

        Bookable bookable = booking.getBookableObject(Utils.currentStaff);
        String name = bookable.getName(Utils.currentStaff);
        name += " " + bookable.getModel(Utils.currentStaff);
        if (bookable instanceof EBike) {
            name += " Ebike";
        } else if (bookable instanceof Scooter) {
            name += " Scooter";
        }
        bookingIdText.setText("%s [Booking #%s]".formatted(name, booking.getID(Utils.currentStaff)));
        bookableText.setText(name);

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.findBookableImagePath(bookable))));

        bikeImage.setImage(image);

        // Get LocalDate and LocalTime from Instant in startTime
        LocalDate startDate = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDate endDate = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime endTime = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDateTime createdOnDate = LocalDateTime.ofInstant(booking.getBookedStartTime(Utils.currentStaff), ZoneId.systemDefault());

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

        if (booking.getApproved(Utils.currentStaff)) {
            statusText.setText("Approved");
        } else {
            statusText.setText("Awaiting approval");
        }

        // Hide staff info if not staff
        if (Utils.currentUser instanceof Staff) {
            if (Utils.currentStaff.canModifyBookings()) {
                if (booking.getApproved(Utils.currentStaff) && booking.getStaff(Utils.currentStaff) != null) {
                    approveButton.setText("Reject");
                    staffLabel.setText("Approved by %s.".formatted(booking.getStaff(Utils.currentStaff).getFullName(Utils.currentStaff)));
                } else {
                    approveButton.setText("Approve");
                }
            } else {
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

        startDatePicker.setDisable(!canEdit);
        startTimeField.setDisable(!canEdit);
        endDatePicker.setDisable(!canEdit);
        endTimeField.setDisable(!canEdit);
        saveButton.setDisable(!canEdit);

        approveButton.setDisable(!canApprove);

        handler = root.getOnMouseClicked();
    }

    public void hideShowButtons() {


        if (showingButtons) {

            buttonBox.setManaged(false);
            buttonBox.setVisible(false);

            root.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");
            // Enables action
            root.setOnMouseClicked(handler);

        } else {

            root.setOnMouseClicked(null);
            root.setStyle("-fx-border-color: #446356; -fx-border-width: 2; -fx-cursor: cursor;");
            buttonBox.setManaged(true);
            buttonBox.setVisible(true);
        }
        showingButtons = !showingButtons;

    }

    public void save() throws IOException, InterruptedException {

        Utils.log("Saving...", 3);

        // Validate date (dd-MM-yyyy)

        LocalDate startDate;
        String rawDate = startDatePicker.getEditor().getText();

        try {
            LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            startDate = startDatePicker.getValue();
        } catch (DateTimeParseException e) {
            hintText.setText("Wrong start date format! Use DD/MM/YYYY");
            return;
        }


        LocalDate endDate;
        rawDate = endDatePicker.getEditor().getText();

        try {
            LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            endDate = endDatePicker.getValue();
        } catch (DateTimeParseException e) {
            hintText.setText("Wrong end date format! Use DD/MM/YYYY");
            return;
        }

        // Validate time (HH:mm)

        LocalTime startTime;
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

        LocalTime endTime;
        try {
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

            currentBooking.setBookedStartTime(startInstant, Utils.currentStaff);
            currentBooking.setBookedEndTime(endInstant, Utils.currentStaff);

            if (Utils.bookingManagerInstance.isTheTimeInOrder(currentBooking)) {
                if (Utils.bookingManagerInstance.isOverlapping(currentBooking)) {
                    // Remove approved status
                    currentBooking.setApproved(false, Utils.currentStaff);
                    // Update booking
                    Utils.apiDatabaseControllerInstance.updateObject(currentBooking);
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

    public void approveReject() throws IOException, InterruptedException {
        currentBooking.setApproved(!currentBooking.getApproved(Utils.currentStaff), Utils.currentStaff);
        currentBooking.setStaff(Utils.currentStaff, Utils.currentStaff);
        Utils.apiDatabaseControllerInstance.updateObject(currentBooking);
    }

    public void delete() throws IOException, InterruptedException {
        Utils.apiDatabaseControllerInstance.deleteObject(currentBooking);
        hideShowButtons();
    }

    public void cancel() {
        hideShowButtons();
    }
}
