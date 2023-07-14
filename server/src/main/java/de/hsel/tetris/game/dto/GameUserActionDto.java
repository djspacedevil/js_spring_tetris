package de.hsel.tetris.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;

public class GameUserActionDto {

    @JsonProperty("action")
    @NotBlank
    private String action;

    @JsonProperty("payload")
    @NotBlank
    private HashMap<String, Integer> payload;


    public GameUserActionDto(@JsonProperty("action") final String action, @JsonProperty("payload") final HashMap<String, Integer> payload) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public HashMap<String, Integer> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<String, Integer> payload) {
        this.payload = payload;
    }
}
