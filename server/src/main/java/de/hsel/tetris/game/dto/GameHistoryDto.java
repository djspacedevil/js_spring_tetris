package de.hsel.tetris.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.hsel.tetris.game.GameHistory;

import java.util.Date;
import java.util.Optional;

public class GameHistoryDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("play_date")
    private Date playDate;

    @JsonProperty("end_state")
    private String endState;


    public GameHistoryDto(Optional<GameHistory> gameHistory) {
        this.id = gameHistory.get().getId();
        this.userId = gameHistory.get().getUserID();
        this.score = gameHistory.get().getScore();
        this.playDate = gameHistory.get().getPlayDate();
        this.endState = gameHistory.get().getEndState();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getPlayDate() {
        return playDate;
    }

    public void setPlayDate(Date playDate) {
        this.playDate = playDate;
    }

    public String getEndState() {
        return endState;
    }

    public void setEndState(String endState) {
        this.endState = endState;
    }
}
