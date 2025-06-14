module com.example.deansgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    requires org.apache.pdfbox;


    opens com.example.deansgenerator to javafx.fxml;
    exports com.example.deansgenerator;
}