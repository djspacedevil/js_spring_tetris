package de.hsel.tetris.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class CreateGameDto {

    @JsonProperty("owner")
    @NotBlank
    private Integer owner;

    @JsonProperty("owner_client_id")
    @NotBlank
    private  Integer ownerClientID;

    @JsonProperty("max_players")
    @NotBlank
    private Integer maxPlayers;

    public CreateGameDto(@JsonProperty("owner") final Integer owner, @JsonProperty("owner_client_id") final Integer ownerClientID, @JsonProperty("max_players") final Integer maxPlayers) {
        this.owner = owner;
        this.ownerClientID = ownerClientID;
        this.maxPlayers = maxPlayers;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getOwnerClientID() {
        return ownerClientID;
    }

    public void setOwnerClientID(Integer ownerClientID) {
        this.ownerClientID = ownerClientID;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
