module myapp.generated {
    requires java.desktop;
    requires org.apache.commons.lang3;

    requires static lombok;

    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.slf4j;
    requires javafx.controls;
    requires java.sql;
    requires java.net.http;
    requires jdk.crypto.ec;
    requires java.security.sasl;

    exports by.forwardsystem;
    exports by.forwardsystem.dto;
    exports by.forwardsystem.controller;
    exports by.forwardsystem.utils;

    opens by.forwardsystem to javafx.fxml, javafx.controls, com.google.gson;
    opens by.forwardsystem.controller to javafx.fxml, javafx.controls, com.google.gson;
    opens by.forwardsystem.dto to javafx.fxml, javafx.controls, com.google.gson;
    opens by.forwardsystem.utils to javafx.fxml, javafx.controls, com.google.gson;
}