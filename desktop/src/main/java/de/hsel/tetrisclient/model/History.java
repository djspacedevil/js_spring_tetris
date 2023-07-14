package de.hsel.tetrisclient.model;

import java.util.Date;

/**
 * The class <b>History</b> represents a user history. This class
 * can create them and contains appropriate getters and setters.
 *
 */
public class History {

    public Integer id, userid, score;

    public Date play_date = new Date() ;

    public String end_state;


    public History(Integer score, Date playDate, String endState) {
        this.score = score;
        this.play_date = playDate;
        this.end_state = endState;
    }

    public History(Integer score, Date playDate, String endState, Integer id, Integer userid) {
        this.score = score;
        this.play_date = playDate;
        this.end_state = endState;
        this.id = id;
        this.userid = userid;
    }

    public Integer idProperty() {
        return id;
    }

    public Integer useridProperty() {
        return userid;
    }

    public Date playDateProperty() {
        return play_date;
    }

    public Integer scoreProperty() { return score; }

    public String endStateProperty() {
        return end_state;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userID) {
        this.userid = userID;
    }

    public void setPlaydate(Date playDate) {
        this.play_date = playDate;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setEndState(String endState) {
        this.end_state = endState;
    }



    @Override
    public String toString() {
        return "History{" + "Score=" + score +
                ", Datum='" + play_date + '\'' +
                ", Result='" + end_state + '\'' +
                ", userID='" + userid + '\'' +
                ", ID='" + id + '\'' +
                '}';
    }
}
