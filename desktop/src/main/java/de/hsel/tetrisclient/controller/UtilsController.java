package de.hsel.tetrisclient.controller;

import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import de.hsel.tetrisclient.task.HistoryTask;
import de.hsel.tetrisclient.task.UserImgTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The class <b>UtilsController</b> represents utilities. This class
 * contains the methods for loading a new scene, to start special, concurrently
 * executed tasks to load a user history and user profile data.
 *
 */
public class UtilsController {

    public static Parent rootScene;

    public static void changeScene(ActionEvent event, String fxmlFile, String title) {

        Parent root = null;

        try {
            root = FXMLLoader.load(TetrisApplication.class.getResource(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        rootScene = root;

    }

    public static void queryUserHistory(Runnable runnable) {
        User user = LoginController.activeUser;
        HistoryTask historyTask = new HistoryTask(user);
        new Thread(historyTask).start();

        // history task response handler adds information to user list gameHistory
        historyTask.setOnSucceeded((WorkerStateEvent event2) -> {

            user.gameHistory.clear();
            user.gameHistory.addAll(historyTask.getValue());

            if (runnable != null) {
                new Thread(runnable).start();
            }
        });

    }

    public static void queryUserProfileImg(Runnable runnable) {
        User user = LoginController.activeUser;
        UserImgTask imgTask = new UserImgTask(user);
        new Thread(imgTask).start();

        // image task response handler adds image to user
        imgTask.setOnSucceeded((WorkerStateEvent event3) -> {
            user.image = imgTask.getValue();

            if (runnable != null) {
                new Thread(runnable).start();
            }
        });

    }

}
