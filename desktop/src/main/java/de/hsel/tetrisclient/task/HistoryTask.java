package de.hsel.tetrisclient.task;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.History;
import de.hsel.tetrisclient.model.User;
import javafx.concurrent.Task;


import java.util.ArrayList;
import java.util.List;

public class HistoryTask extends Task<List<History>> {

    private User user;

    public HistoryTask(User user) {
        this.user = user;
    }

    @Override
    protected List<History> call() {

        String url = TetrisApplication.API_HOST + "/api/game-histories/user/" + user.id;

        try {
            HttpResponse<JsonNode> resJson = Unirest.get(url).header("Accept", "application/json").header("Authorization", "Bearer " + user.authToken).asJson();
            String json = resJson.getBody().toString();

            // Convert JSON File to Java Object
            if(resJson.getStatus() == 200) {
                List<History> userHistory = new Gson().fromJson(json, new TypeToken<ArrayList<History>>(){}.getType());
                return userHistory;
            }
        } catch (UnirestException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}