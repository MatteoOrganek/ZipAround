package uk.ac.roehampton.ziparound.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.roles.Admin;
import uk.ac.roehampton.ziparound.users.staff.roles.BookingAgent;
import uk.ac.roehampton.ziparound.users.staff.roles.Manager;
import uk.ac.roehampton.ziparound.vehicles.vehicletypes.Scooter;

import java.net.URL;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Test section
        Admin admin = new Admin();
        Manager manager = new Manager();
        BookingAgent bookingAgent = new BookingAgent();

        Scooter scooter = new Scooter(1, "Scooty", "Scooter", "EG3425", 12.3f, 30);


        System.exit(0);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/uk/ac/roehampton/ziparound/application/main-view.fxml")));
        Scene scene = new Scene(loader.load());

        // Correct CSS loading
        URL css = getClass().getResource("/modern.css");
        System.out.println(css);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("'modern.css' not found!");
        }

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/modern.css")).toExternalForm());

        primaryStage.setTitle("Modern JavaFX Demo");
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
