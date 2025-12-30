package uk.ac.roehampton.ziparound.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("test-view.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

//     CalendarView calendarView = new CalendarView();
//        Calendar vehiclesCalendar = new Calendar("Vehicles");
//        Calendar equipmentCalendar = new Calendar("Equipment");
//        CalendarSource myCalendarSource = new CalendarSource("My Calendars");
//        myCalendarSource.getCalendars().addAll(vehiclesCalendar, equipmentCalendar);
//
//        calendarView.getCalendarSources().addAll(myCalendarSource);
//
//        calendarView.setRequestedTime(LocalTime.now());
//
//        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
//            @Override
//            public void run() {
//                while (true) {
//                    Platform.runLater(() -> {
//                        calendarView.setToday(LocalDate.now());
//                        calendarView.setTime(LocalTime.now());
//                    });
//
//                    try {
//                        // update every 10 seconds
//                        sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        };
//
//        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
//        updateTimeThread.setDaemon(true);
//        updateTimeThread.start();
//
//        Entry<String> entry = new Entry<>("V12345");
//        vehiclesCalendar.addEntry(entry);
//
//
//
//        Scene scene = new Scene(calendarView);
//
//        stage.setScene(scene);
}
