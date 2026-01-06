/**
 * HomeController.java
 * Controller for home.fxml.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.application.controllers;

import com.calendarfx.model.Entry;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.calendarfx.model.Calendar;
import com.calendarfx.view.CalendarView;

/**
 * This class Controller controls home.fxml.
 */
public class HomeController implements Updatable {
    public Label greetText;
    public CalendarView calendarView;

    // Needed to prevent header controller to be null
    @FXML Parent header;

    // Create a header instance
    @FXML private HeaderController headerController;

    // Call update on initialization
    @FXML
    public void initialize() throws IOException, InterruptedException {
        update();
    }

    // Update UI
    @Override
    public void update() throws IOException {

        // Update buttons in header
        headerController.inHomeView();

        // If current user is not staff, hide controls
        if (!(Utils.currentUser instanceof Staff)){
            headerController.hideStaffControls();
        }

        // If the current staff does not exist, show default text, else mention the user's name. (Current staff is needed for user name fetch)
        if (Utils.currentStaff != null){
            greetText.setText("Hello %s,\nBook Your Ride\nin Seconds".formatted(Utils.currentUser.getForeName(Utils.currentStaff)));
        } else {
            greetText.setText("Book Your Ride in Seconds");
        }

        // Create bookings calendar
        Calendar bookingsCalendar = new Calendar("Bookings");


        // Set up a personalized calendar view
        // For each booking in the all bookings list
        for (Booking booking : Utils.bookingManagerInstance.getBookingArrayList()) {
            // If the current booking's user id is equal to the current user id
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {

                // fetch start and end time and translate them to LocalDate
                LocalDateTime start = Utils.bookingManagerInstance.getStartLocalDateTime(booking);
                LocalDateTime end = Utils.bookingManagerInstance.getEndLocalDateTime(booking);

                // Get bookable
                Bookable bookable = booking.getBookableObject(Utils.currentStaff);
                // Add entry's content using bookable's name and booking's id
                String content = "#%s - %s".formatted(booking.getID(Utils.currentStaff), bookable.getName(Utils.currentStaff));
                // Create entry
                Entry<?> entry = new Entry<>(content);
                // Setup start and end time for entry
                entry.setInterval(start, end);
                // Add entry to bookings calendar
                bookingsCalendar.addEntry(entry);
            }
        }

        // Setup calendarView
        calendarView.getCalendarSources().clear();
        var source = new com.calendarfx.model.CalendarSource("My Bookings");
        source.getCalendars().add(bookingsCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowSearchField(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPageSwitcher(false);
        calendarView.setShowToolBar(false);
        calendarView.setDate(LocalDate.now());
        calendarView.showMonthPage();
        calendarView.getMonthPage().setSelectionMode(SelectionMode.MULTIPLE);
        calendarView.getCalendars().forEach(c -> c.setReadOnly(true));

    }


    // Clear UI
    @Override
    public void clear() {
        // Nothing to clear
    }

    // Get the current header controller
    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    // Head to new booking view
    public void goToBookingCreationView() {
        Utils.changeScene("booking-creation");
    }

}
