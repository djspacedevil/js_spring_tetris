package de.hsel.tetrisclient.task;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import de.hsel.tetrisclient.TetrisApplication;
import de.hsel.tetrisclient.model.User;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.InputStream;

public class UserImgTask extends Task<Image> {

    private User user;

    public UserImgTask(User user) {
        this.user = user;
    }

    @Override
    protected Image call() throws Exception  {

        String url = TetrisApplication.API_HOST + user.profile_picture;
        HttpResponse<InputStream> resImg = Unirest.get(url).header("Accept", "application/octet-stream").asBinary();

        if (resImg.getStatus() == 200) {
            InputStream is = resImg.getBody();
            user.image = new Image(is);
            return user.image;
        }

        return null;
    }
}
