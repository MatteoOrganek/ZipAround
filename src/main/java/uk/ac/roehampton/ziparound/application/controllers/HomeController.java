package uk.ac.roehampton.ziparound.application.controllers;

import com.calendarfx.model.Entry;
import com.calendarfx.view.page.MonthPage;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
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
    public void initialize() throws IOException {
        update();
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
