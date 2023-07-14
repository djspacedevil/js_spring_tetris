package de.hsel.tetrisclient.model;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;


/**
 * The class <b>User</b> represents a Tetris user. This class
 * can create them and contains appropriate getters and setters.
 * A list of histories also kept to represent the user game history.
 *
 */

public class User {

    public Integer id;
    public String username, password, email, profile_picture, authToken;
    public transient Image image;
    public static List<History> gameHistory = new ArrayList<>();


    public User() {}

    public User(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgPath() {
        return profile_picture;
    }

    public void setImgPath(String profile_picture) {
        this.profile_picture = profile_picture;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String toString() {
        return "User{" + "Id=" + id +
                ", E-Mail-Adresse='" + email + '\'' +
                ", Name='" + username + '\'' +
                ", Profilbild='" + profile_picture + '\'' +
                ", Authtoken='" + authToken + '\'' +
                '}';
    }
}