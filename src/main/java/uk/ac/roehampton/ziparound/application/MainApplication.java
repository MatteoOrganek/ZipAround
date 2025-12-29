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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class MainApplication extends Application {

    public SceneController sceneController;
    BookingManager bookingManager;


    @Override
    public void start(Stage stage) throws Exception {

        Admin admin = new Admin(0, "Matteo", "Organek", "Admin");

        bookingManager = BookingManager.getInstance(admin);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));

        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("file:src/main/resources/uk/ac/roehampton/ziparound/application/imgs/logo_circular.png"));
        stage.setTitle("Zip Around");
        stage.setScene(scene);
        stage.show();

        sceneController = new SceneController(stage.getScene());
        Parent testView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("test-view.fxml")));
        sceneController.addScreen("test", (Pane) testView);
        Parent loginView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-view.fxml")));
        sceneController.addScreen("login", (Pane) loginView);
        sceneController.activate("login");


    }
    public static void main(String[] args) {
        launch(args);
    }
}
