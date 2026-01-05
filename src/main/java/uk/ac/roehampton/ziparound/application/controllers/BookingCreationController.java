package uk.ac.roehampton.ziparound.application.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.DayView;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.page.MonthPage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import uk.ac.roehampton.ziparound.application.controllers.components.BookingCardController;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.role.BookingAgent;

import java.awt.print.Book;
import java.io.IOException;
import java.sql.PseudoColumnUsage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.IsoFields;
import java.util.*;

public class BookingCreationController implements Updatable {

    public HBox container;
    public CalendarView calendarView;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public Button bookButton;
    public VBox bookableSelectionContainer;
    public VBox dateSelectionContainer;
    
    public Booking currentBooking;
    public TextField startTimeField;
    public TextField endTimeField;
    public Bookable currentBookable;
    // Create bookings calendar
    Calendar<Node> bookingsCalendar = new Calendar("Bookings");

    public List<BookableCardController> bookableCardControllerList = new ArrayList<>();

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    public void initialize() throws IOException {
        update();
    }

    @Override
    public void update() throws IOException {

        clear();

        Utils.log("Updating UI...", 3);

        headerController.inBookingCreationView();
        // Show bookable selection
        back();

    }

    public void updateBookableView() throws IOException {

        clear();
        ArrayList<Bookable> bookableArrayList = new ArrayList<>();
        bookableArrayList.addAll(Utils.bookingManagerInstance.getVehicleArrayList());
        bookableArrayList.addAll(Utils.bookingManagerInstance.getEquipmentArrayList());

        for (Bookable bookable : bookableArrayList) {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/uk/ac/roehampton/ziparound/application/modules/bookable-card.fxml")
            );

            Parent bookableCard = loader.load();
            BookableCardController controller = loader.getController();

            // Configure the card
            controller.setBookable(bookable, this);
            container.setAlignment(Pos.CENTER);

            // Attach to UI
            container.getChildren().add(bookableCard);

            bookableCardControllerList.add(controller);
        }
    }


    public void updateCalendar(Bookable bookable) {

        // Add to CalendarView
        calendarView.getCalendarSources().clear();

        for (Calendar calendar : calendarView.getCalendars()) {
            calendar.clear();
        }

        var source = new com.calendarfx.model.CalendarSource("My Bookings");

        List<Booking> bookingList = new ArrayList<>();
        for (Booking booking : Utils.bookingManagerInstance.getBookingArrayList()) {
            if (Objects.equals(booking.getBookableObject(Utils.currentStaff).getID(Utils.currentStaff), bookable.getID(Utils.currentStaff))) {
                bookingList.add(booking);
            }
        }

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


        if (bookable != null) {

    //        Platform.runLater(() -> {
    //                for (Node node : calendarView.getMonthPage().lookupAll("*")) {
    //                    System.out.println(node + " - " + node.getStyleClass());
    //                }
    //            });

            // Store first click
            final LocalDate[] firstClick = {null};
            final List<Region> highlightedCells = new ArrayList<>();
            final List<Region> blockedCells = new ArrayList<>();
            final List<Region> intersectedCells = new ArrayList<>();


            Platform.runLater(() -> {
                Set<Node> dayViews = monthPage.lookupAll(".day"); // MonthDayView nodes


                for (Node node : dayViews) {

                    // Reset node
                    node.getStyleClass().removeAll( "selected-day", "blocked-day", "intersected-day");

                    if (!(node instanceof Region dayCell)) continue;

                    // Grab the date from the label
                    Node labelNode = dayCell.lookup(".day-of-month-label");

                    int day;
                    String dayString = "";
                    try {
                        dayString = ((javafx.scene.control.Label) labelNode).getText();
                        day = Integer.parseInt(dayString);
                    } catch (NumberFormatException | NullPointerException e) {
                        if (dayString.contains(" ")) {
                            day = Integer.parseInt(dayString.split(" ")[1]);
                        } else {
                            continue; // skip headers, empty labels, etc.
                        }
                    }

                    LocalDate cellDate = LocalDate.of(
                            calendarView.getYearMonthView().getYearMonth().getYear(),
                            calendarView.getYearMonthView().getYearMonth().getMonth(),
                            day
                    );

                    dayCell.setUserData(cellDate);


                    for (Booking booking : bookingList) {
                        if (!cellDate.isBefore(Utils.bookingManagerInstance.getStartLocalDateTime(booking).toLocalDate()) &&
                                !cellDate.isAfter(Utils.bookingManagerInstance.getEndLocalDateTime(booking).toLocalDate())) {

                            blockedCells.add((Region) dayCell);
                            dayCell.getStyleClass().add("blocked-day");
                        }

                    }

                    LocalDate today = LocalDate.now();
                    int currentWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int currentYear = today.getYear();

                    // Highlight current week
                    int cellWeek = cellDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int cellYear = cellDate.getYear();
                    if (cellWeek == currentWeek && cellYear == currentYear) {
                        dayCell.getStyleClass().add("current-week");
                    }

                    dayCell.setOnMouseClicked(null);
                    ((Region) dayCell).setOnMouseClicked(event -> {
                        if (firstClick[0] == null) {

                            // First click

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

                            for (Region blocked : blockedCells) {
                                blocked.getStyleClass().add("blocked-day");
                            }

                            dayCell.getStyleClass().remove("blocked-day");
                            dayCell.getStyleClass().add("selected-day");
                            highlightedCells.add((Region) dayCell);

                        } else {
                            // Second click highlight range
                            LocalDate start = firstClick[0].isBefore(cellDate) ? firstClick[0] : cellDate;
                            LocalDate end = firstClick[0].isBefore(cellDate) ? cellDate : firstClick[0];

                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            startDatePicker.getEditor().setText("%s".formatted(start.format(dateFormatter)));
                            endDatePicker.getEditor().setText("%s".formatted(end.format(dateFormatter)));

                            // Second-click highlight range
                            for (Node onClickNode : dayViews) {
                                Region onClickRegion = (Region) onClickNode;
                                Node onClickLabelNode = onClickRegion.lookup(".day-of-month-label");
                                if (!(onClickLabelNode instanceof Label l)) continue;

                                LocalDate date = (LocalDate) onClickRegion.getUserData();

                                if (!date.isBefore(start) && !date.isAfter(end)) {

                                    if (blockedCells.contains(onClickRegion)) {
                                        onClickRegion.getStyleClass().removeAll("blocked-day", "selected-day", "intersected-day");
                                        onClickRegion.getStyleClass().add("intersected-day");
                                        intersectedCells.add(onClickRegion);
                                    } else {
                                        onClickRegion.getStyleClass().removeAll("blocked-day", "intersected-day");
                                        onClickRegion.getStyleClass().add("selected-day");
                                        highlightedCells.add(onClickRegion);
                                    }
                                }
                            }


                            bookButton.setDisable(!intersectedCells.isEmpty());

                            // Reset first click
                            firstClick[0] = null;
                        }

                    });
                }
            });
        }
    }


    public void deselectAllBookable() {
        for (BookableCardController bookableCardController : bookableCardControllerList) {
            if (bookableCardController.selected) {
                bookableCardController.select();
            }
        }
    }
    
    public Boolean validateBookingRequest() {
        
        // Validate date (dd/MM/yyyy)

        LocalDate startDate;
        String rawDate = startDatePicker.getEditor().getText();

        try {
            startDate = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            Utils.alert("Error", "Wrong input", "Wrong start date format! Use DD/MM/YYYY");
            return false;
        }


        LocalDate endDate;
        rawDate = endDatePicker.getEditor().getText();

        try {
            endDate = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            Utils.alert("Error", "Wrong input", "Wrong end date format! Use DD/MM/YYYY");
            return false;
        }

        // Validate time (HH:mm)

        LocalTime startTime;
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

        LocalTime endTime;
        try {
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
            if (Utils.bookingManagerInstance.isTheTimeInOrder(currentBooking)) {
                if (Utils.bookingManagerInstance.isOverlapping(currentBooking)) {
                    // Upload it to the db
                    currentBooking.printInfo(Utils.currentStaff);
                    Utils.apiDatabaseControllerInstance.addObject(currentBooking);
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


    @Override
    public void clear() {
        container.getChildren().clear();
        Utils.log("UI Cleared.", 3);
    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    public void book() {
        if (validateBookingRequest()) {
            Utils.changeScene("booking");
        }
    }

    public void back() throws IOException {
        bookableSelectionContainer.setManaged(true);
        bookableSelectionContainer.setVisible(true);
        dateSelectionContainer.setManaged(false);
        dateSelectionContainer.setVisible(false);
        updateBookableView();

    }

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


