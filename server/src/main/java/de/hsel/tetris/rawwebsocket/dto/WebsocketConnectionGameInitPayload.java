package de.hsel.tetris.rawwebsocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class WebsocketConnectionGameInitPayload {

    @JsonProperty("action")
    @NotBlank
    private String action;

    @JsonProperty("direction")
    private String direction;

    public WebsocketConnectionGameInitPayload(final String action, final String direction) {
        this.action = action;
        this.direction = direction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
