package de.hsel.tetrisclient.controller;

import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import de.hsel.tetrisclient.task.LoginTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**
 The class <b>LoginController</b> represents a Controller for the scene login.fxml.
 *
 */

public class LoginController {

    @FXML
    private TextField tf_username_lg;

    @FXML
    private PasswordField tf_password_lg;

    @FXML
    private Label label_info;

    public static User activeUser;

    @FXML
    protected void onBtnLoginClick(ActionEvent e1)  {
        //checks if all fields are filled
        if(!tf_username_lg.getText().trim().isEmpty() && !tf_password_lg.getText().trim().isEmpty()) {

            //starts new Thread to check username and password
            LoginTask loginTask = new LoginTask(tf_username_lg.getText(), tf_password_lg.getText());
            new Thread(loginTask).start();

            //login task response handler, to check if the login attempt was successful or not
            loginTask.setOnSucceeded((WorkerStateEvent event2) -> {
                activeUser = loginTask.getValue();

                if (activeUser == null) {
                    label_info.setText("Leider ist ein Fehler aufgetreten. \n Versuchen Sie es erneut.");
                    label_info.setStyle("-fx-text-fill: #900000;");
                    return;
                } else {
                    UtilsController.queryUserProfileImg(null);
                    UtilsController.queryUserHistory(null);
                }

                try {
                    Thread.sleep(1000);
                    UtilsController.changeScene(e1,"main.fxml", TetrisApplication.APP_TITLE);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        }
        else {
            label_info.setText("Leider ist die Eingabe falsch. \n Bitte fülle alle erforderlichen Felder aus.");
            label_info.setStyle("-fx-text-fill: #900000;");
        }
    }

    @FXML
    protected void onBtnSignupClick(ActionEvent e2) {
        UtilsController.changeScene(e2,"registration.fxml", TetrisApplication.APP_TITLE + " - Registrierung");
    }


    public void setSuccessRegistration() {
        label_info.setText("Deine Registrierung war erfolgreich. \n Jetzt einloggen!");
        label_info.setStyle("-fx-text-fill: #039000;");
    }

}