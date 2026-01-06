package uk.ac.roehampton.ziparound.application.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.booking.BookingManager;

import java.io.IOException;

public class StaffController implements Updatable {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    private TableView<Booking> bookingRequestsTable;

    @FXML
    public TableColumn<Booking, Integer> bookingIDColumn;

    @FXML
    public TableColumn<Booking, String> bookableColumn;

    @FXML
    public TableColumn<Booking, String> customerNameColumn;

    @FXML
    public TableColumn<Booking, String> startTimeColumn;

    @FXML
    public TableColumn<Booking, String> endTimeColumn;

    @FXML
    public TableColumn<Booking, Boolean> approvedColumn;

    @FXML
    public void initialize() {
        update();

    }

    @Override
    public void update() {
        headerController.inStaffView();
        // Configure columns
        bookingIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().bookingID));
        customerNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser(Utils.currentStaff).getFullName(Utils.currentStaff)));
        bookableColumn.setCellValueFactory(data -> new SimpleStringProperty("%s | %s".formatted(data.getValue().getBookableObject(Utils.currentStaff).getName(Utils.currentStaff), data.getValue().getBookableObject(Utils.currentStaff).getModel(Utils.currentStaff))));
        startTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedStartTime(Utils.currentStaff).toString()));
        endTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedEndTime(Utils.currentStaff).toString()));
        approvedColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getApproved(Utils.currentStaff)));

        // Fill table
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        bookings.addAll(BookingManager.getInstance().getBookingArrayList());

        bookingRequestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        bookingRequestsTable.setItems(bookings);
    }


    @FXML
    private void approveSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            booking.setApproved(true, Utils.currentStaff);
            booking.setStaff(Utils.currentStaff, Utils.currentStaff);
            Utils.apiDatabaseControllerInstance.updateObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    @FXML
    private void rejectSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            booking.setApproved(false, Utils.currentStaff);
            booking.setStaff(null, Utils.currentStaff);
            Utils.apiDatabaseControllerInstance.updateObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    @FXML
    private void deleteSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            Utils.apiDatabaseControllerInstance.deleteObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    @Override
    public void clear() {

    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }
}
