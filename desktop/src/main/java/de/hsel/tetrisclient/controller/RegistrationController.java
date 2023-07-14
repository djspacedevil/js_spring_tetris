package de.hsel.tetrisclient.controller;

import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import de.hsel.tetrisclient.task.RegistrationTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * The class <b>RegistrationController</b> represents a Controller for the scene registration.fxml.
 *
 */
public class RegistrationController {

    @FXML
    private TextField tf_username_re;

    @FXML
    private PasswordField tf_password_re;

    @FXML
    private TextField tf_email_re;

    @FXML
    private Label label_info;

    public static User newUser;

    @FXML
    protected void onBtnLoginClick(ActionEvent event) {
        UtilsController.changeScene(event,"login.fxml", TetrisApplication.APP_TITLE + " - Login");
    }

    @FXML
    protected void onBtnSignupClick(ActionEvent event) {
        if(!tf_username_re.getText().trim().isEmpty() && !tf_password_re.getText().trim().isEmpty() && !tf_email_re.getText().trim().isEmpty()) {

            // execute registration request in async task
            RegistrationTask registrationTask = new RegistrationTask(tf_username_re.getText(), tf_password_re.getText(), tf_email_re.getText());
            new Thread(registrationTask).start();


            // registration task response handler
            registrationTask.setOnSucceeded((WorkerStateEvent event2) -> {

                newUser = registrationTask.getValue();
                if (newUser == null) {
                    label_info.setText("Ein Fehler ist aufgetreten. \n Eventuell existiert der Nutzer bereits!");
                } else {

                    // navigate to login view and show notice "successful registration"
                    Parent root = null;
                    try {
                        FXMLLoader loader = new FXMLLoader(TetrisApplication.class.getResource("login.fxml"));
                        root = loader.load();


                        LoginController labelCon = loader.getController();
                        labelCon.setSuccessRegistration();

                        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle(TetrisApplication.APP_TITLE + " - Login");
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            });


        }
        else {
            label_info.setText("Bitte fülle alle erforderlichen Daten aus! \n Oder prüfe deine Eingaben!");
        }
    }


}
