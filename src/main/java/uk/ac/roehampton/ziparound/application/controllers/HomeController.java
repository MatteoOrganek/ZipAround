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
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.calendarfx.model.Calendar;
import com.calendarfx.view.CalendarView;

public class HomeController implements Updatable {
    public Label greetText;
    public CalendarView calendarView;

    // Needed to prevent header controller to be null
    @FXML
    Parent header;

    @FXML
    private HeaderController headerController;

    @FXML
    public void initialize() throws IOException, InterruptedException {
        update();

        Vehicle vehicle = new EBike(999, "", "", "", Float.parseFloat("2.2"), true, 222, 2, 2);
        Utils.apiBridgeInstance.addObject(vehicle);
    }

    @Override
    public void update() throws IOException {

        headerController.inHomeView();

        if (!(Utils.currentUser instanceof Staff)){
            headerController.hideStaffControls();
        }

        if (Utils.currentStaff != null){
            greetText.setText("Hello %s,\nBook Your Ride\nin Seconds".formatted(Utils.currentUser.getForeName(Utils.currentStaff)));
        } else {
            greetText.setText("Book Your Ride in Seconds");
        }

        // Create bookings calendar
        Calendar bookingsCalendar = new Calendar("Bookings");


        for (Booking booking : Utils.bookingManagerInstance.getBookingArrayList()) {
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {

                LocalDateTime start = Utils.bookingManagerInstance.getStartLocalDateTime(booking);
                LocalDateTime end = Utils.bookingManagerInstance.getEndLocalDateTime(booking);


                Bookable bookable = booking.getBookableObject(Utils.currentStaff);
                String content = "#%s - %s".formatted(booking.getID(Utils.currentStaff), bookable.getName(Utils.currentStaff));
                Entry<?> entry = new Entry<>(content);
                entry.setInterval(start, end);
                bookingsCalendar.addEntry(entry);
            }
        }

        // Add to CalendarView
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


    @Override
    public void clear() {
        Utils.log("UI Cleared.", 3);

    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

    public void goToBookingCreationView() {
        Utils.changeScene("booking-creation");
    }

}
