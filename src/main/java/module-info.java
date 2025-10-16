module uk.ac.roehampton.ziparound {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens uk.ac.roehampton.ziparound to javafx.fxml;
    exports uk.ac.roehampton.ziparound;
    exports uk.ac.roehampton.ziparound.vehicles;
    opens uk.ac.roehampton.ziparound.vehicles to javafx.fxml;
    exports uk.ac.roehampton.ziparound.users;
    opens uk.ac.roehampton.ziparound.users to javafx.fxml;
    exports uk.ac.roehampton.ziparound.users.staff;
    opens uk.ac.roehampton.ziparound.users.staff to javafx.fxml;
    exports uk.ac.roehampton.ziparound.application;
    opens uk.ac.roehampton.ziparound.application to javafx.fxml;
}