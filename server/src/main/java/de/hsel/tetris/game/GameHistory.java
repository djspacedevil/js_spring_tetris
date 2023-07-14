package de.hsel.tetris.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "game_history")
public class GameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @JsonProperty("user_id")
    private Integer userID;

    @NotNull
    private Integer score = 0;

    @NotNull
    @Column(columnDefinition = "DATE")
    @JsonProperty("play_date")
    private Date playDate;

    @Column(nullable = true)
    @JsonProperty("end_state")
    private String endState;

    public GameHistory(Integer userID, Integer score, Date playDate, String endState) {
        this.userID = userID;
        this.score = score;
        this.playDate = playDate;
        this.endState = endState;
    }

    public GameHistory() {

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
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
