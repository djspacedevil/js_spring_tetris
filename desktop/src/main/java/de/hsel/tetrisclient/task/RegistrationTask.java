package de.hsel.tetrisclient.task;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import javafx.concurrent.Task;
import com.google.gson.JsonObject;

import java.io.IOException;

public class RegistrationTask extends Task<User> {

    private String username;

    private String password;

    private String email;

    public RegistrationTask(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    protected User call()  {
        String url = TetrisApplication.API_HOST + "/api/users";


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        try {
            HttpResponse<JsonNode> res = Unirest.post(url).header("Content-Type", "application/json").body(jsonObject.toString()).asJson();

            if (res.getStatus() != 201) {

                switch (res.getStatus()) {
                    case 400:
                        throw new IOException("\t\n" + "Request body does not match UserRegistrationDto spec");
                    case 409:
                        throw new IOException("\t\n" + "E-mail address and/or username are already taken");
                    case 422:
                        throw new IOException("\t\n" + "The email address is invalid");
                    case 500:
                        throw new IOException("\t\n" + "Something went wrong on the server");
                    default:
                        throw new IOException("The login failed with a result of: " + res.getStatus()
                                + " (" + res.getStatusText() + ")");
                }

            } else {
                return new User(username, password, email);
            }

        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
