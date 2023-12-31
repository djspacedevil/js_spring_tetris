package de.hsel.tetrisclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TetrisApplication extends Application {

    public static final String APP_TITLE = "Tetris Game";
    public static final String API_HOST = "http://localhost:8080";

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle(APP_TITLE + " - Login");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}