/**
 * MainApplication.java
 * Handles window management (CSS load, page definition, etc.).
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.application;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        CalendarView calendarView = new CalendarView();
        Calendar vehiclesCalendar = new Calendar("Vehicles");
        Calendar equipmentCalendar = new Calendar("Equipment");
        CalendarSource myCalendarSource = new CalendarSource("My Calendars"); // (4)
        myCalendarSource.getCalendars().addAll(vehiclesCalendar, equipmentCalendar);

        calendarView.getCalendarSources().addAll(myCalendarSource); // (5)

        calendarView.setRequestedTime(LocalTime.now());

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

        Entry<String> entry = new Entry<>("V12345");
        vehiclesCalendar.addEntry(entry);

        Scene scene = new Scene(calendarView);
        stage.getIcons().add(new Image("file:src/main/resources/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
        stage.setTitle("Zip Around");
        stage.setScene(scene);
        stage.setWidth(1300);
        stage.setHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
