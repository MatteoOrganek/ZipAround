/**
 * BookingCreationController.java
 * Controller for booking-creation.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.page.MonthPage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.BookableCardController;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.IsoFields;
import java.util.*;

/**
 * This class Controller controls booking-creation.fxml.
 */
public class BookingCreationController implements Updatable {

    // Variables declaration and initialization
    public HBox container;
    public VBox bookableSelectionContainer;
    public VBox dateSelectionContainer;
    public CalendarView calendarView;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public TextField startTimeField;
    public TextField endTimeField;
    public Button nextButton;
    public Button bookButton;
    public Label hintText;

    public Booking currentBooking;
    public Bookable currentBookable;

    // Create new bookings calendar
    Calendar<Node> bookingsCalendar = new Calendar("Bookings");

    // Create new bookableCardControllerList for the bookable scrollbar card
    public List<BookableCardController> bookableCardControllerList = new ArrayList<>();

    // Needed to prevent header controller to be null
    @FXML Parent header;

    // Fetch HeaderController
    @FXML private HeaderController headerController;

    // On initialization, update
    @FXML
    public void initialize() throws IOException {
        update();
    }

    // Update UI
    @Override
    public void update() throws IOException {

        // Clear all bookable cards
        clear();
        Utils.log("Updating UI...", 3);
        // Change header layout
        headerController.inBookingCreationView();
        // Show bookable selection
        back();
    }

    /**
     * This function converts all bookable items in a card and adds them to the container
     * @throws IOException Exception thrown if the addition process was interrupted.
     */
    public void updateBookableView() throws IOException {

        // Clear all cards in container
        clear();

        // Create a new list that will contain all bookable items
        ArrayList<Bookable> bookableArrayList = new ArrayList<>();
        // Add vehicles and equipment to it
        bookableArrayList.addAll(Utils.bookingManagerInstance.getVehicleArrayList());
        bookableArrayList.addAll(Utils.bookingManagerInstance.getEquipmentArrayList());

        // For each bookable in the list
        for (Bookable bookable : bookableArrayList) {

            // If the bookable is available
            if (bookable.isAvailable(Utils.currentStaff)) {

                // Create loader for bookable-card.fxml
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/uk/ac/roehampton/ziparound/application/modules/bookable-card.fxml")
                );

                // Load the loader
                Parent bookableCard = loader.load();

                // Get the controller from the loader
                BookableCardController controller = loader.getController();

                // Configure the card using this instance
                controller.setUp(bookable, this);

                // Align the container to the center
                container.setAlignment(Pos.CENTER);

                // Attach to UI
                container.getChildren().add(bookableCard);

                // Add the controller to the list of bookable controllers
                bookableCardControllerList.add(controller);
            }

        }
    }


    /**
     * This function is able to set up and mod a calendarFX instance, giving the user the option to select start and end dates
     * in two click. The first click will reset everything and the second click will highlight the date range showing intersected days if any.
     * The function is able to detect and show already-booked days as well.
     * @param bookable Bookable item used to show availability.
     */
    public void updateCalendar(Bookable bookable) {

        // Remove all calendars and calendar sources
        calendarView.getCalendarSources().clear();
        for (Calendar calendar : calendarView.getCalendars()) {
            calendar.clear();
        }

        // Create new source
        var source = new com.calendarfx.model.CalendarSource("My Bookings");

        // for each booking in all bookings, add the booking (if there is a matching bookable) to a new list
        List<Booking> bookingList = new ArrayList<>();
        for (Booking booking : Utils.bookingManagerInstance.getBookingArrayList()) {
            if (Objects.equals(booking.getBookableObject(Utils.currentStaff).getID(Utils.currentStaff), bookable.getID(Utils.currentStaff))) {
                bookingList.add(booking);
            }
        }

        // Setup the calendar and configure the view to only show a view-only month page
        source.getCalendars().add(bookingsCalendar);
        calendarView.getCalendarSources().add(source);

        calendarView.setShowSearchField(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPageSwitcher(false);
        calendarView.setShowToolBar(false);
        calendarView.setDate(LocalDate.now());
        calendarView.showMonthPage();
        calendarView.getCalendars().forEach(c -> c.setReadOnly(true));
        MonthPage monthPage = calendarView.getMonthPage();
        monthPage.setSelectionMode(SelectionMode.MULTIPLE);

        // If the current bookable does exist
        if (bookable != null) {

    //        Platform.runLater(() -> {
    //                for (Node node : calendarView.getMonthPage().lookupAll("*")) {
    //                    System.out.println(node + " - " + node.getStyleClass());
    //                }
    //            });

            // Store first click
            final LocalDate[] firstClick = {null};
            // Create new lists of highlighted, blocked and selected cells
            final List<Region> highlightedCells = new ArrayList<>();
            final List<Region> blockedCells = new ArrayList<>();
            final List<Region> intersectedCells = new ArrayList<>();

            // Run the following once everything has been loaded
            Platform.runLater(() -> {

                // Get all .day tagged nodes in monthPage (all day cells)
                Set<Node> dayViews = monthPage.lookupAll(".day");

                // for each node in the list of day cells
                for (Node node : dayViews) {

                    // Remove any css classes
                    node.getStyleClass().removeAll( "selected-day", "blocked-day", "intersected-day");

                    // Skip if node is not a Region and cast it to Region
                    if (!(node instanceof Region dayCell)) continue;

                    // Grab the date from the label
                    Node labelNode = dayCell.lookup(".day-of-month-label");

                    // Get current day number
                    int day;
                    String dayString = "";
                    try {
                        // Try to parse the label text onto an integer
                        dayString = ((javafx.scene.control.Label) labelNode).getText();
                        day = Integer.parseInt(dayString);
                    } catch (NumberFormatException | NullPointerException e) {
                        // Parse failed, the label probably contains "Mon 11", hence split it in the middle "Mon" "11"
                        // And get the second element
                        if (dayString.contains(" ")) {
                            day = Integer.parseInt(dayString.split(" ")[1]);
                        } else {
                            // skip headers, empty labels, etc.
                            continue;
                        }
                    }

                    // Create the cell date
                    LocalDate cellDate = LocalDate.of(
                            calendarView.getYearMonthView().getYearMonth().getYear(),
                            calendarView.getYearMonthView().getYearMonth().getMonth(),
                            day
                    );

                    // Add cellDate to user data of the cell itself, this will be used later
                    dayCell.setUserData(cellDate);

                    // Find if each booking in bookingList in in between the cellDate
                    for (Booking booking : bookingList) {
                        if (!cellDate.isBefore(Utils.bookingManagerInstance.getStartLocalDateTime(booking).toLocalDate()) &&
                                !cellDate.isAfter(Utils.bookingManagerInstance.getEndLocalDateTime(booking).toLocalDate())) {

                            // If so, the cell becomes blocked and is added to the blocked list
                            blockedCells.add((Region) dayCell);
                            dayCell.getStyleClass().add("blocked-day");
                        }

                    }

                    // Get today's date, week and year
                    LocalDate today = LocalDate.now();
                    int currentWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int currentYear = today.getYear();

                    // Highlight current week with own css, as the importance level of current week overwrites this function's logic
                    int cellWeek = cellDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int cellYear = cellDate.getYear();
                    if (cellWeek == currentWeek && cellYear == currentYear) {
                        dayCell.getStyleClass().add("current-week");
                    }

                    // Set on mouse clicked on each cell
                    dayCell.setOnMouseClicked(null);
                    ((Region) dayCell).setOnMouseClicked(event -> {
                        // If it is the firs click
                        if (firstClick[0] == null) {

                            // First click store date and highlight
                            firstClick[0] = cellDate;

                            // Clear text labels
                            startDatePicker.getEditor().setText("");
                            endDatePicker.getEditor().setText("");

                            // Disable book button
                            bookButton.setDisable(true);

                            // Clear previous highlights
                            for (Node r : dayViews) {
                                r.getStyleClass().removeAll( "selected-day", "blocked-day", "intersected-day");
                            }
                            highlightedCells.clear();
                            intersectedCells.clear();

                            // Re-add all blocked cells
                            for (Region blocked : blockedCells) {
                                blocked.getStyleClass().add("blocked-day");
                            }

                            // Highlight current cell
                            dayCell.getStyleClass().remove("blocked-day");
                            dayCell.getStyleClass().add("selected-day");
                            highlightedCells.add((Region) dayCell);

                        // Currently on the second click
                        } else {

                            // Second click highlight range
                            LocalDate start = firstClick[0].isBefore(cellDate) ? firstClick[0] : cellDate;
                            LocalDate end = firstClick[0].isBefore(cellDate) ? cellDate : firstClick[0];

                            // Format the date and show in date pickers
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            startDatePicker.getEditor().setText("%s".formatted(start.format(dateFormatter)));
                            endDatePicker.getEditor().setText("%s".formatted(end.format(dateFormatter)));

                            // For each day cell
                            for (Node onClickNode : dayViews) {

                                // Cast node to Region
                                Region onClickRegion = (Region) onClickNode;

                                // Find all Nodes that are not labels
                                Node onClickLabelNode = onClickRegion.lookup(".day-of-month-label");
                                if (!(onClickLabelNode instanceof Label l)) continue;

                                // Get date added previously to the Region
                                LocalDate date = (LocalDate) onClickRegion.getUserData();

                                // if the current cell's date is in between the first and second click's date
                                if (!date.isBefore(start) && !date.isAfter(end)) {

                                    // If the cell is part of the blocked cell, highlight it as intersected
                                    if (blockedCells.contains(onClickRegion)) {
                                        onClickRegion.getStyleClass().removeAll("blocked-day", "selected-day", "intersected-day");
                                        onClickRegion.getStyleClass().add("intersected-day");
                                        intersectedCells.add(onClickRegion);

                                    // Else, the cell is empty and can be added as a selected cell
                                    } else {
                                        onClickRegion.getStyleClass().removeAll("blocked-day", "intersected-day");
                                        onClickRegion.getStyleClass().add("selected-day");
                                        highlightedCells.add(onClickRegion);
                                    }
                                }
                            }

                            // Disable the button if there are any intercepted cells
                            bookButton.setDisable(!intersectedCells.isEmpty());

                            // Reset first click
                            firstClick[0] = null;
                        }

                    });
                }
            });
        }
    }


    /**
     * This function deselects all bookable cards
     */
    public void deselectAllBookable() {
        // For each controller
        for (BookableCardController bookableCardController : bookableCardControllerList) {
            // If the card has been selected
            if (bookableCardController.selected) {
                // Deselect it
                bookableCardController.select();
            }
        }
    }

    /**
     * This function validates the booking request by fetching the entries' data and formatting them into usable data.
     * @return Whether the operation was successful or not
     */
    public Boolean validateBookingRequest() {
        
        // Validate date (dd/MM/yyyy)

        // Initialize startDate
        LocalDate startDate;
        // Get raw date from the datePicker
        String rawDate = startDatePicker.getEditor().getText();

        // Try parsing the date, if invalid, inform user
        try {
            startDate = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            Utils.alert("Error", "Wrong input", "Wrong start date format! Use DD/MM/YYYY");
            return false;
        }

        // Initialize endDate
        LocalDate endDate;
        // Get raw date from the datePicker
        rawDate = endDatePicker.getEditor().getText();

        // Try parsing the date, if invalid, inform user
        try {
            endDate = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            Utils.alert("Error", "Wrong input", "Wrong end date format! Use DD/MM/YYYY");
            return false;
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
            Utils.alert("Error", "Wrong input", "Check the time, start time must be in HH:mm format!");
            Utils.log("User did not follow the time format in the Start Time Entry.", 5);
            return false;
        }

        // Initialize endTime
        LocalTime endTime;
        try {
            // Try parsing endTime to LocalTime, if it results in an error, inform user
            endTime = LocalTime.parse(
                    endTimeField.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm")
            );
        } catch (DateTimeParseException e) {
            Utils.alert("Error", "Wrong input", "Check the time, end time must be in HH:mm format!");
            Utils.log("User did not follow the time format in the End Time Entry.", 5);
            return false;
        }

        // Combine date and time
        try {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            // Convert to Instant using system timezone
            Instant startInstant = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endDateTime.atZone(ZoneId.systemDefault()).toInstant();

            // Check if bookable has been selected
            if (currentBookable != null) {
                // Build
                currentBooking = new Booking(
                        startInstant,
                        endInstant,
                        Instant.now(),
                        Utils.currentUser,
                        currentBookable,
                        false,
                        Utils.currentStaff
                );
            } else {
                Utils.alert("Error", "Booking", "Please select a bookable item.");
                Utils.log("User tried to book without a bookable item.", 5);
                return false;
            }
            // If the time is in order (startTime > endTime)
            if (Utils.bookingManagerInstance.isTheTimeInOrder(currentBooking)) {
                // If the booking overlaps with another booking having the same bookable
                if (Utils.bookingManagerInstance.isOverlapping(currentBooking)) {
                    currentBooking.printInfo(Utils.currentStaff);
                    // Upload it to the db
                    Utils.apiBridgeInstance.addObject(currentBooking);
                    return true;
                } else {
                    Utils.alert("Error", "Booking", "Sorry, this item is booked for that time period!");
                    Utils.log("User tried to book an already booked time slot.", 5);
                }
            } else {
                Utils.alert("Error", "Wrong input", "Check the date time, end time cannot begin before start time.");
                Utils.log("User inputted end time before start time or date overlaps with another booking of the same item.", 5);
            }

        } catch (Error | IOException | InterruptedException e) {
            Utils.alert("Error", "Wrong input", "Please check your entries.");
            Utils.log("User did not follow the time format in Entries.", 5);
        }

        return false;
    }

    // Clear all the children in the container
    @Override
    public void clear() {
        container.getChildren().clear();
        Utils.log("UI Cleared.", 3);
    }

    // Get the current header controller
    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    /**
     * Button action that validates the booking request and sends the user to their current bookings after successfully validating the booking
     */
    public void book() {
        if (validateBookingRequest()) {
            Utils.changeScene("booking");
        }
    }


    /**
     * This function sends the user back to the bookable selection, while hiding the calendar
     * @throws IOException Exception thrown if the addition process was interrupted.
     */
    public void back() throws IOException {
        bookableSelectionContainer.setManaged(true);
        bookableSelectionContainer.setVisible(true);
        dateSelectionContainer.setManaged(false);
        dateSelectionContainer.setVisible(false);
        updateBookableView();

    }

    /**
     * This function hides the bookable selection, unhides the calendar and updates it.
     */
    public void next() {
        for (BookableCardController bookableCardController : bookableCardControllerList) {
            if (bookableCardController.selected) {

                bookableSelectionContainer.setManaged(false);
                bookableSelectionContainer.setVisible(false);
                dateSelectionContainer.setManaged(true);
                dateSelectionContainer.setVisible(true);
                currentBookable = bookableCardController.currentBookable;
                updateCalendar(currentBookable);
                break;
            }
        }
    }
}


