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
    requires org.jetbrains.annotations;
    requires jdk.jshell;
    requires java.desktop;
    requires com.calendarfx.view;
    requires javafx.graphics;

    opens uk.ac.roehampton.ziparound to javafx.fxml;
    exports uk.ac.roehampton.ziparound;
    exports uk.ac.roehampton.ziparound.equipment.vehicle;
    opens uk.ac.roehampton.ziparound.equipment.vehicle to javafx.fxml;
    exports uk.ac.roehampton.ziparound.users;
    opens uk.ac.roehampton.ziparound.users to javafx.fxml;
    exports uk.ac.roehampton.ziparound.users.staff;
    opens uk.ac.roehampton.ziparound.users.staff to javafx.fxml;
    exports uk.ac.roehampton.ziparound.application;
    opens uk.ac.roehampton.ziparound.application to javafx.fxml;
    exports uk.ac.roehampton.ziparound.equipment;
    opens uk.ac.roehampton.ziparound.equipment to javafx.fxml;
    exports uk.ac.roehampton.ziparound.booking;
    opens uk.ac.roehampton.ziparound.booking to javafx.fxml;
}