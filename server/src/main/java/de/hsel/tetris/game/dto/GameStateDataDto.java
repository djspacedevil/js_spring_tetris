package de.hsel.tetris.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameStateDataDto {

    @JsonProperty("user_id")
    private Integer userID;

    @JsonProperty("current_score")
    private Integer currentScore;

    @JsonProperty("grid")
    private String grid;


    public GameStateDataDto(final Integer userID, final Integer currentScore, final String grid) {
        this.userID = userID;
        this.currentScore = currentScore;
        this.grid = grid;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }
}
