module AdvancedPortChecker {
    requires inet.ipaddr;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    //noinspection Java9RedundantRequiresStatement
    requires jdk.crypto.ec; // Added for SSL handshakes
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires atlantafx.base;

    opens com.codedead.advanced.portchecker.controller to javafx.fxml;

    exports com.codedead.advanced.portchecker;
}
