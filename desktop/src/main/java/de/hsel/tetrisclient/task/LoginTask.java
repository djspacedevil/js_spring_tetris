package de.hsel.tetrisclient.task;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import javafx.concurrent.Task;

import java.io.IOException;

public class LoginTask extends Task<User> {
    private String username;

    private String password;

    public LoginTask(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected User call() {

        String url = TetrisApplication.API_HOST + "/api/login";

        try {
            HttpResponse<String> loginResult = Unirest.post(url).basicAuth(username, password).asString();

            if (loginResult.getStatus() != 200) {

                switch (loginResult.getStatus()) {
                    case 401:
                        throw new IOException("\t\n" + "Credentials are invalid");
                    case 500:
                        throw new IOException("\t\n" + "Something went wrong on the server");
                    default:
                        throw new IOException("The login failed with a result of: " + loginResult.getStatus()
                                + " (" + loginResult.getStatusText() + ")");
                }

            } else {
                String authToken = loginResult.getBody();

                try {
                    HttpResponse<JsonNode> resJson = Unirest.get(TetrisApplication.API_HOST + "/api/users/me").header("Accept", "application/json").header("Authorization", "Bearer " + authToken).asJson();

                    if (loginResult.getStatus() != 200) {

                        switch (loginResult.getStatus()) {
                            case 401:
                                throw new IOException("\t\n" + "Access token is missing or invalid");
                            case 500:
                                throw new IOException("\t\n" + "Internal Server Error");
                            default:
                                throw new IOException("The login failed with a result of: " + loginResult.getStatus()
                                        + " (" + loginResult.getStatusText() + ")");
                        }
                    } else {
                        String json = resJson.getBody().toString();

                        // Convert JSON File to Java Object
                        User user = new Gson().fromJson(json, User.class);
                        user.setAuthToken(authToken);
                        return user;
                    }

                } catch (UnirestException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
