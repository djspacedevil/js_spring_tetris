package de.hsel.tetrisclient.controller;

import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.History;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 The class <b>HistoryController</b> represents a Controller for the scene history.fxml.
 *
 */
public class HistoryController {

    @FXML
    private FlowPane historyPane;

    List<History> historyList;

    VBox vbox = new VBox();

    @FXML
    public void initialize() {
        historyPane.getChildren().clear();

        HBox hBoxTitle = new HBox();
        Label scoreTitle = new Label("Score");
        Label endStateTitle = new Label("Endstand");
        Label dateTitle = new Label("Datum");

        scoreTitle.setMinWidth(150.0);
        endStateTitle.setMinWidth(200.0);
        dateTitle.setMinWidth(200.0);

        hBoxTitle.getChildren().add(scoreTitle);
        hBoxTitle.getChildren().add(endStateTitle);
        hBoxTitle.getChildren().add(dateTitle);

        vbox.getChildren().add(hBoxTitle);

        Line line = new Line();
        line.setStartX(0.0f);
        //line.setStartY(0.0f);
        line.setEndX(550.0f);
        //line.setEndY(0.0f);
        vbox.getChildren().add(line);

        historyList = LoginController.activeUser.gameHistory;

        if(!historyList.isEmpty()) {
            for (History history: historyList) {
                HBox hboxInput = new HBox();
                Label score = new Label(history.score.toString());
                Label endState = new Label(history.end_state);
                String newDate = newPlayDate(history.play_date);
                Label date = new Label(newDate);

                score.setMinWidth(150.0);
                endState.setMinWidth(200.0);
                date.setMinWidth(200.0);

                hboxInput.getChildren().add(score);
                hboxInput.getChildren().add(endState);
                hboxInput.getChildren().add(date);
                vbox.getChildren().add(hboxInput);

            }
        } else {
            HBox hboxInput2 = new HBox();
            Label emptyResult = new Label("Keine Einträge vorhanden");
            hboxInput2.getChildren().add(emptyResult);
            vbox.getChildren().add(hboxInput2);
        }

        historyPane.getChildren().add(vbox);

    }

    /**
     * Helper method to display the date correctly
     *
     * @return the date in a new format
     */
    private String newPlayDate(Date playDate) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return new String(df.format(playDate));
    }


    @FXML
    protected void btnMainScene(ActionEvent event) {
        UtilsController.changeScene(event,"main.fxml", TetrisApplication.APP_TITLE);
    }
}
