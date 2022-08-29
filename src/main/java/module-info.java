module com.example.excelreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.apache.poi.ooxml;
    requires reload4j;

    opens com.example.excelreader to javafx.fxml;
    exports com.example.excelreader.Controller;
    opens com.example.excelreader.Controller to javafx.fxml;
    exports com.example.excelreader.View;
    opens com.example.excelreader.View to javafx.fxml;
    opens com.example.excelreader.DAO to javafx.base;
    exports com.example.excelreader.DAO;

}