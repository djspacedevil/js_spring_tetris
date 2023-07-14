package de.hsel.tetris.rawwebsocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class WebsocketConnectionGameInit {

    @JsonProperty("token")
    @NotBlank
    private String token;

    @JsonProperty("client_id")
    @NotBlank
    private Integer clientID;

    @JsonProperty("game_id")
    @NotBlank
    private Integer gameID;

    @JsonProperty("type")
    @NotBlank
    private String type = "join";

    @JsonProperty("payload")
    private String payload;

    public WebsocketConnectionGameInit(final String token, final Integer clientID, final Integer gameID, final String type, final String payload) {
        this.token = token;
        this.clientID = clientID;
        this.gameID = gameID;
        this.type = type;
        this.payload = payload;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
