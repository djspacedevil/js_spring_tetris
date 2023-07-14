package de.hsel.tetrisclient.task;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import javafx.concurrent.Task;

import java.io.InputStream;

public class UpdateImageTask extends Task<User> {

    private User user;

    private InputStream image;

    private String imageName;

    public UpdateImageTask (User user, String imageName, InputStream image) {
        this.user = user;
        this.imageName = imageName;
        this.image = image;


    }



    @Override
    protected User call() throws Exception {

        String url = TetrisApplication.API_HOST + user.profile_picture;
        try {
            MultipartBody sendImg = Unirest.post(url).header("Authorization", "Bearer " + user.authToken)
                    .field("profile_picture", image, imageName);
            HttpResponse res = sendImg.asString();

            if(res.getStatus() == 204) {
                System.out.println("Erfolgreich Bild übertragen" + res.getStatus());
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
