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
    requires spring.security.crypto;
    requires Java.WebSocket;
    requires com.zaxxer.hikari;
    requires java.sql;

    opens org.example.mensajeriacliente to javafx.fxml;
    opens org.example.mensajeriacliente.controllers to javafx.fxml;
    exports org.example.mensajeriacliente;
    exports org.example.mensajeriacliente.controllers;
}