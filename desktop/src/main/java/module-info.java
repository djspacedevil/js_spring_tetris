module de.hsel.tetrisclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires com.almasb.fxgl.all;
    requires unirest.java;
    requires java.persistence;
    requires json;
    requires com.google.gson;
    requires java.desktop;
    requires httpcore;
    requires com.jfoenix;
    requires org.apache.tomcat.embed.websocket;


    opens de.hsel.tetrisclient to javafx.fxml;
    exports de.hsel.tetrisclient;
    exports de.hsel.tetrisclient.controller;
    opens de.hsel.tetrisclient.controller to javafx.fxml;
    exports de.hsel.tetrisclient.model to com.google.gson;
    opens de.hsel.tetrisclient.model to javafx.base;
}