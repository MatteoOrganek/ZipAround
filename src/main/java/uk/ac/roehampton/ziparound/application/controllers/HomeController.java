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
import java.net.UnknownServiceException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            greetText.setText("Hello %s,\nBook Your Ride in Seconds".formatted(Utils.currentUser.getForeName(Utils.currentStaff)));
        } else {
            greetText.setText("Book Your Ride in Seconds");
        }

        // Create bookings calendar
        Calendar bookingsCalendar = new Calendar("Bookings");


        for (Booking booking : Utils.bookingManagerInstance.getBookingArrayList()) {
            if (Objects.equals(booking.getUser(Utils.currentStaff).getID(Utils.currentStaff), Utils.currentUser.getID(Utils.currentStaff))) {

                Instant startInstant = booking.getBookedStartTime(Utils.currentStaff);
                Instant endInstant = booking.getBookedEndTime(Utils.currentStaff);
                LocalDateTime start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
                LocalDateTime end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());


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
        MonthPage monthPage = calendarView.getMonthPage();

        // Store first click
        final LocalDate[] firstClick = {null};
        final List<Region> highlightedCells = new ArrayList<>();

//        Platform.runLater(() -> {
//            for (Node node : monthPage.lookupAll("*")) {
//                System.out.println(node + " - " + node.getStyleClass());
//            }
//        });

        Set<Node> dayViews = monthPage.lookupAll(".day"); // MonthDayView nodes

        for (Node node : dayViews) {
            Region dayCell = (Region) node;
            dayCell.getStyleClass().remove("selected-day");

            // Grab the day label inside
            Node labelNode = dayCell.lookup(".day-of-month-label");
            if (!(labelNode instanceof javafx.scene.control.Label label)) continue;

            int dayOfMonth;
            try {
                dayOfMonth = Integer.parseInt(label.getText());
            } catch (NumberFormatException e) {
                continue;
            }

            // You might need the current displayed month/year
            LocalDate cellDate = LocalDate.of(
                    calendarView.getYearMonthView().getYearMonth().getYear(),
                    calendarView.getYearMonthView().getYearMonth().getMonth(),
                    dayOfMonth
            );

            dayCell.setOnMouseClicked(event -> {
                if (firstClick[0] == null) {
                    // First click store date and highlight
                    firstClick[0] = cellDate;
                    dayCell.getStyleClass().add("selected-day");
                    highlightedCells.add(dayCell);

                    // Clear previous highlights
                    for (Region r : highlightedCells) {
                        r.getStyleClass().remove("selected-day");
                    }
                    highlightedCells.clear();

                } else {
                    // Second click highlight range
                    LocalDate start = firstClick[0].isBefore(cellDate) ? firstClick[0] : cellDate;
                    LocalDate end = firstClick[0].isBefore(cellDate) ? cellDate : firstClick[0];


                    // Highlight all cells in range
                    for (Node n : dayViews) {
                        Region r = (Region) n;
                        Node lblNode = r.lookup(".day-of-month-label");
                        if (!(lblNode instanceof javafx.scene.control.Label l)) continue;

                        int d;
                        try {
                            d = Integer.parseInt(l.getText());
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        LocalDate date = LocalDate.of(
                                calendarView.getYearMonthView().getYearMonth().getYear(),
                                calendarView.getYearMonthView().getYearMonth().getMonth(),
                                d
                        );

                        if (!date.isBefore(start) && !date.isAfter(end)) {
                            r.getStyleClass().add("selected-day");
                            highlightedCells.add(r);
                        }
                    }

                    // Reset first click
                    firstClick[0] = null;
                }
            });
        }
    }

    @Override
    public void clear() {
        Utils.log("UI Cleared.", 3);

    }

    @Override
    public HeaderController getHeaderController() {
        return headerController;
    }

}
