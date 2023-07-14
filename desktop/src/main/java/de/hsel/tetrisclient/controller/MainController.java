package de.hsel.tetrisclient.controller;


import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.task.UpdateImageTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javax.swing.*;
import java.io.*;


/**
 The class <b>MainController</b> represents a Controller for the scene main.fxml.
 *
 */


public class MainController {
    @FXML
    private Label mainTitle;

    @FXML
    private ImageView userImg;

    private Circle circle = new Circle();

    final File[] newFile = new File[1];


    @FXML
    protected void initialize() {
        initializeCircle();
        initializeUserImg();


        mainTitle.setText("Willkommen " + LoginController.activeUser.username + "!");

    }

    /**
     * Helper method to initialize the user profile picture
     *
     */
    private void initializeUserImg() {
        userImg.setImage(LoginController.activeUser.image);
        userImg.setFitWidth(110);
        userImg.setFitHeight(110);
        userImg.setClip(circle);
    }

    /**
     * Helper method to display user profile picture round or rather in a circle
     *
     */
    private void initializeCircle() {
        circle.centerXProperty().bind(userImg.fitWidthProperty().multiply(0.5));
        circle.centerYProperty().bind(userImg.fitHeightProperty().multiply(0.5));
        circle.setRadius(50.0);
    }


    @FXML
    protected void onBtnSetNewImage() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Foto zum Hochladen auswählen");

        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            newFile[0] = fileChooser.getSelectedFile();

            if(newFile[0] == null) {
                System.out.println("Es wurde keine Datei gewählt");

            } else {

                try {
                    InputStream fileInput = new FileInputStream(newFile[0].getAbsolutePath());
                    String fileName = newFile[0].getName();

                    UpdateImageTask newImg = new UpdateImageTask(LoginController.activeUser, fileName, fileInput);
                    new Thread(newImg).start();

                    newImg.setOnSucceeded((WorkerStateEvent event2) -> {
                        Runnable r = () -> userImg.setImage(LoginController.activeUser.image);
                        UtilsController.queryUserProfileImg(r);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void onBtnGetHistory(ActionEvent event) {
        UtilsController.changeScene(event,"history.fxml", TetrisApplication.APP_TITLE + " - Spieleüberblick");
    }

    @FXML
    protected void onBtnPlayGame(ActionEvent event) {
        UtilsController.changeScene(event,"game.fxml", TetrisApplication.APP_TITLE);
    }

    @FXML
    protected void onBtnLogoutClick(ActionEvent event) {
        LoginController.activeUser = null;
        UtilsController.changeScene(event,"login.fxml", TetrisApplication.APP_TITLE + " - Login");
    }

}
