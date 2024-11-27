module com.example.finalsprogtools {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.finalsprogtools to javafx.fxml;
    exports com.example.finalsprogtools;
}