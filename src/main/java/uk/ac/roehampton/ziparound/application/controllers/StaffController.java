/**
 * StaffController.java
 * Controller for staff.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;

import java.io.IOException;

/**
 * This class Controller controls staff.fxml.
 */
public class StaffController implements Updatable {

    // Initialize Variables
    public Button availabilityButton;

    // Needed to prevent header controller to be null
    @FXML Parent header;

    // Initialize header controller
    @FXML private HeaderController headerController;

    // Booking table
    @FXML private TableView<Booking> bookingRequestsTable;
    @FXML private TableColumn<Booking, Integer> bookingIDColumn;
    @FXML private TableColumn<Booking, String> bookingNameColumn;
    @FXML private TableColumn<Booking, String> bookingCustomerNameColumn;
    @FXML private TableColumn<Booking, String> bookingStartTimeColumn;
    @FXML private TableColumn<Booking, String> bookingEndTimeColumn;
    @FXML private TableColumn<Booking, Boolean> bookingApprovedColumn;

    // Bookable table
    @FXML private TableView<Bookable> bookableRequestsTable;
    @FXML private TableColumn<Bookable, Integer> bookableIDColumn;
    @FXML private TableColumn<Bookable, String> bookableTypeColumn;
    @FXML private TableColumn<Bookable, String> bookableNameColumn;
    @FXML private TableColumn<Bookable, String> bookableModelColumn;
    @FXML private TableColumn<Bookable, Float> bookableMilesColumn;
    @FXML private TableColumn<Bookable, Integer> bookableAmountColumn;
    @FXML private TableColumn<Bookable, Boolean> bookableAvailableColumn;

    // On initialization, update
    @FXML
    public void initialize() {
        update();
    }

    // Update UI
    @Override
    public void update() {

        // Update header's buttons
        headerController.inStaffView();

        // Configure booking columns
        bookingIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().bookingID));
        bookingCustomerNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser(Utils.currentStaff).getFullName(Utils.currentStaff)));
        bookingNameColumn.setCellValueFactory(data -> new SimpleStringProperty("%s | %s".formatted(data.getValue().getBookableObject(Utils.currentStaff).getName(Utils.currentStaff), data.getValue().getBookableObject(Utils.currentStaff).getModel(Utils.currentStaff))));
        bookingStartTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedStartTime(Utils.currentStaff).toString()));
        bookingEndTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookedEndTime(Utils.currentStaff).toString()));
        bookingApprovedColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getApproved(Utils.currentStaff)));

        // Fill table
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        bookings.addAll(Utils.bookingManagerInstance.getBookingArrayList());

        bookingRequestsTable.setItems(bookings);

        // Add constraints to the bookings table
        bookingRequestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

        // Sort table
        bookingIDColumn.setSortType(TableColumn.SortType.DESCENDING);

        // Configure bookable columns
        bookableIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getID(Utils.currentStaff)));
        bookableTypeColumn.setCellValueFactory(data -> (data.getValue() instanceof Vehicle) ? new SimpleStringProperty("Vehicle") : new SimpleStringProperty("Equipment"));
        bookableNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName(Utils.currentStaff)));
        bookableModelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModel(Utils.currentStaff)));
        bookableMilesColumn.setCellValueFactory(data -> (data.getValue() instanceof Vehicle) ? new SimpleFloatProperty(((Vehicle) data.getValue()).getTotalMiles(Utils.currentStaff)).asObject() : null);
        bookableAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmountOfBookings(Utils.currentStaff)));
        bookableAvailableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isAvailable(Utils.currentStaff)));


        // Fill table
        ObservableList<Bookable> bookable = FXCollections.observableArrayList();
        bookable.addAll(Utils.bookingManagerInstance.getBookableArrayList());

        bookableRequestsTable.setItems(bookable);

        // Add constraints to the bookable table
        bookableRequestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

        // If the current staff cannot modify vehicles
        if (!Utils.currentStaff.canModifyVehicles()) {
            availabilityButton.setManaged(false);
            availabilityButton.setVisible(false);
        }
    }


    /**
     * This function approves all items selected in the booking table
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    @FXML
    private void approveSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            booking.setApproved(true, Utils.currentStaff);
            booking.setStaff(Utils.currentStaff, Utils.currentStaff);
            Utils.apiBridgeInstance.updateObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    /**
     * This function rejects all items selected in the booking table
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    @FXML
    private void rejectSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            booking.setApproved(false, Utils.currentStaff);
            booking.setStaff(null, Utils.currentStaff);
            Utils.apiBridgeInstance.updateObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    /**
     * This function deletes all items selected in the booking table
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    @FXML
    private void deleteSelected() throws IOException, InterruptedException {
        for (Booking booking : bookingRequestsTable.getSelectionModel().getSelectedItems()) {
            Utils.apiBridgeInstance.deleteObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    /**
     * This function changes the availability of a bookable item.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    @FXML
    private void changeAvailability() throws IOException, InterruptedException {
        for (Bookable bookable : bookableRequestsTable.getSelectionModel().getSelectedItems()) {
            bookable.setAvailable(!bookable.isAvailable(Utils.currentStaff), Utils.currentStaff);
            Utils.apiBridgeInstance.updateObject(bookable);
        }
        bookingRequestsTable.refresh();
    }

    /**
     * This function books a maintenance booking in the nearest future
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    @FXML
    private void bookMaintenance() throws IOException, InterruptedException {
        for (Bookable bookable : bookableRequestsTable.getSelectionModel().getSelectedItems()) {
            Booking booking = Utils.bookingManagerInstance.findMaintenanceSlot(bookable, 2, Utils.currentStaff);
            Utils.apiBridgeInstance.addObject(booking);
        }
        bookingRequestsTable.refresh();
    }

    // Clear UI
    @Override
    public void clear() {
        // Nothing to clear
    }

    // Fetch header controller
    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

}
