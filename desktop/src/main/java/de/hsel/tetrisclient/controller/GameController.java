package de.hsel.tetrisclient.controller;

import de.hsel.tetrisclient.TetrisApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import com.jfoenix.controls.JFXButton;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import javax.websocket.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 The class <b>GameController</b> represents a Controller for the scene game.fxml.
 *
 */
public class GameController {

    @FXML
    Pane boardFrame;


    public static final int SIZE = 25;
    public static int XMAX = SIZE * 10;
    public static int YMAX = SIZE * 16;
    public static int [][] BOARD = new int[YMAX/SIZE][XMAX/SIZE];

    private static final URI SERVER_URI = URI.create("ws://localhost:8080/connect");



    @FXML
    public void initialize() throws Exception {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(ClientEndpoint.class, SERVER_URI);

        boardFrame.setStyle("-fx-background-color: #0a1018");

        for (int[] x: BOARD) {
            Arrays.fill(x, 0);
        }

        //System.out.println(Arrays.deepToString(BOARD));

        //Score + Lines Text
        Line divider = new Line(XMAX, 0, XMAX, YMAX);
        divider.setStroke(Color.WHITE);

        Text scoreText = new Text("Punkte: ");
        scoreText.setY(50);
        scoreText.setX(XMAX + 15);
        scoreText.setFill(Color.WHITE);

        Text lines = new Text("Lines: ");
        lines.setY(75);
        lines.setX(XMAX + 15);
        lines.setFill(Color.WHITE);

        JFXButton btnEsc = new JFXButton("Abbrechen");
        btnEsc.setStyle("-fx-background-color: #e0e0e0;");
        btnEsc.setLayoutX(XMAX + 15);
        btnEsc.setLayoutY(YMAX - 50);
        btnEsc.setOnAction((ActionEvent e1) -> {
            UtilsController.changeScene(e1,"main.fxml", TetrisApplication.APP_TITLE);

        });

        boardFrame.getChildren().addAll(scoreText, divider, lines, btnEsc);

        //Make Pane focusable to respond on KeyEvents
        boardFrame.setFocusTraversable(true);
        addKeyHandler(boardFrame);

    }

    @javax.websocket.ClientEndpoint
    public static class ClientEndpoint {


        @OnOpen
        public void onOpen(Session session) {
            // Verbindung wurde hergestellt

            System.out.println("Connected to server " + session);


        }

        @OnMessage
        public void onMessage(String message) {
            // Empfange Nachricht vom Server
            System.out.println("Received message: " + message);
        }

        /*
        @OnClose
        public void onClose(Session session) throws IOException {
            System.out.println("Verbindung getrennt...");
            session.close();

        }
        */

    }


    private void addKeyHandler(Pane scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {

            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.RIGHT)) {
                System.out.println("RIGHT");
                return;
            }
            if (keyCode.equals(KeyCode.LEFT)) {
                System.out.println("LEFT");
                return;
            }
            if (keyCode.equals(KeyCode.UP)) {
                System.out.println("UP");
                return;
            }
            if (keyCode.equals(KeyCode.DOWN)) {
                System.out.println("DOWN");
                return;
            }

        });
    }


}
