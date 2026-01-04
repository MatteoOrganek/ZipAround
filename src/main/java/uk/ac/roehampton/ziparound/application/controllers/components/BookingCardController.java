package uk.ac.roehampton.ziparound.application.controllers.components;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    private Boolean showingButtons = false;

    private Booking currentBooking;

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
        createdOnLabel.setText(booking.getCreatedOn(Utils.currentStaff).toString());

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.findBookableImagePath(bookable))));

        bikeImage.setImage(image);

        // Get LocalDate and LocalTime from Instant in startTime
        LocalDate startDate = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = booking.getBookedStartTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDate endDate = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime endTime = booking.getBookedEndTime(Utils.currentStaff).atZone(ZoneId.systemDefault()).toLocalTime();

        // Format LocalTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTimeString = startTime.format(timeFormatter);
        String endTimeString = endTime.format(timeFormatter);

        // Insert Data in Fields
        startDatePicker.setValue(startDate);
        startTimeField.setText(startTimeString);
        endDatePicker.setValue(endDate);
        endTimeField.setText(endTimeString);

        if (booking.getApproved(Utils.currentStaff)) {
            statusText.setText("Approved");
        } else {
            statusText.setText("Awaiting approval");
        }


        // Hide staff info if not staff
        if (Utils.currentUser instanceof Staff) {
            if (Utils.currentStaff.canViewStaffInfo()) {
                if (booking.getApproved(Utils.currentStaff) && booking.getStaff(Utils.currentStaff) != null) {
                    staffLabel.setText("Approved by %s.".formatted(booking.getStaff(Utils.currentStaff).getFullName(Utils.currentStaff)));
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

    }

    public void hideShowButtons() {

        if (showingButtons) {
            root.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
            buttonBox.setManaged(false);
            buttonBox.setVisible(false);
        } else {
            root.setStyle("-fx-border-color: #446356; -fx-border-width: 2;");
            buttonBox.setManaged(true);
            buttonBox.setVisible(true);
        }
        showingButtons = !showingButtons;

    }

    public void save() throws IOException, InterruptedException {
        Utils.apiDatabaseControllerInstance.updateObject(currentBooking);
        hideShowButtons();
    }

    public void approveReject() throws IOException, InterruptedException {
        currentBooking.setApproved(!currentBooking.getApproved(Utils.currentStaff), Utils.currentStaff);
        currentBooking.setStaff(Utils.currentStaff, Utils.currentStaff);
        Utils.apiDatabaseControllerInstance.updateObject(currentBooking);
    }

    public void delete() {
        hideShowButtons();
    }

    public void cancel() {
        hideShowButtons();
    }
}
