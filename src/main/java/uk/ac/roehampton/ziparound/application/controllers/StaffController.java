package uk.ac.roehampton.ziparound.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Booking;

public class StaffController implements Updatable {

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    private TableView<Booking> bookingRequestsTable;

    @FXML
    private TableColumn<Booking, Integer> bookingIDColumn;

    @FXML
    private TableColumn<Booking, String> customerNameColumn;

    @FXML
    private TableColumn<Booking, String> startTimeColumn;

    @FXML
    private TableColumn<Booking, String> endTimeColumn;

    @FXML
    private TableColumn<Booking, Boolean> approvedColumn;

    @FXML
    public void initialize() {
        headerController.inStaffView();

//        // Configure columns
//        bookingIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().bookingID));
//        customerNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser(Utils.currentStaff).getFullName(Utils.currentStaff)));
//        startTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedStartTime(Utils.currentStaff).toString()));
//        endTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedEndTime(Utils.currentStaff).toString()));
//        approvedColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getApproved(Utils.currentStaff)));
//
//        // Fill table
//        ObservableList<Booking> bookings = FXCollections.observableArrayList();
//        bookings.addAll(BookingManager.getInstance().getBookingArrayList());
//
//        bookingRequestsTable.setItems(bookings);
    }

    @Override
    public void update() {

    }

    @Override
    public void clear() {

    }
}
