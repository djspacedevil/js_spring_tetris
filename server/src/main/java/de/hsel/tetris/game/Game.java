package de.hsel.tetris.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import de.hsel.tetris.game.dto.GameStateDataDto;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "owner_id")
    private Integer ownerID;

    @ElementCollection
    @Column(name = "player")
    private List<Integer> players = new ArrayList<Integer>();

    @Column(name = "is_open")
    private Boolean isOpen = true;

    @Column(name = "is_running")
    private Boolean isRunning = false;

    @Column(name = "is_over")
    private Boolean isOver = false;

    @Column(nullable = true)
    private String state = "open";

    @Column(name = "max_players")
    @JsonProperty("max_players")
    private Integer maxPlayers = 1;

    @Column(name = "game_state_data")
    @JsonProperty("game_state_data")
    private String gameStateData = "";

    public Game() {

    }

    public Game(Integer id, Integer ownerID, List<Integer> players, Boolean isOpen, Boolean isRunning, Boolean isOver, String state, Integer maxPlayers, JSONObject gameStateData) {
        this.id = id;
        this.ownerID = ownerID;
        this.players = players;
        this.isOpen = isOpen;
        this.isRunning = isRunning;
        this.isOver = isOver;
        this.state = state;
        this.maxPlayers = maxPlayers;
        this.gameStateData = gameStateData.toString();
    }

    public Integer getId() {
        return id;
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public Boolean getOver() {
        return isOver;
    }

    public void setOver(Boolean over) {
        isOver = over;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state.contains("open") || state.contains("running")) {
            this.state = state;
        } else {
            this.state = "over";
        }

    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        if (maxPlayers < 1) maxPlayers = 1;
        this.maxPlayers = maxPlayers;
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public void setPlayers(List<Integer> players) {
        this.players = players;
    }

    public List<GameStateDataDto> getGameStateData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(this.gameStateData, GameStateDataDto[].class));
    }

    public void setGameStateData(List<GameStateDataDto> playerData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.gameStateData = mapper.writeValueAsString(playerData);
        }catch (Exception e) {
            this.gameStateData = "";
        }
    }
}
