module org.example.mensajeriacliente {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires spring.security.crypto;

    opens org.example.mensajeriacliente to javafx.fxml;
    opens org.example.mensajeriacliente.controllers to javafx.fxml;
    exports org.example.mensajeriacliente;
    exports org.example.mensajeriacliente.controllers;
}