package de.hsel.tetris.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsel.tetris.game.dto.GameStateDataDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Optional<Game> createNewGame(Integer ownerID, Integer maxPlayers) {
        var game = new Game();
        game.setOwnerID(ownerID);
        game.setPlayers(new ArrayList<>(List.of(ownerID)));
        game.setMaxPlayers(maxPlayers);

        Game newGame = gameRepository.save(game);
        if (newGame.getId().describeConstable().isPresent()) {
            return Optional.of(newGame);
        }

        return Optional.empty();
    }

    public Game getGame(Integer id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.orElse(null);
    }

    public String addPlayerToGame(Game game, Integer userID) {
        try {
            if(!game.getPlayers().contains(userID)) {
                List<Integer> players = game.getPlayers();
                if (game.getRunning()) return "running";
                if (players.size() < game.getMaxPlayers()) {
                    players.add(userID);
                    game.setPlayers(players);
                    gameRepository.save(game);
                    return "added";
                }
                return "full";
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
            }
        } catch (Exception e) {
            return "over";
        }
    }

    public String removePlayerFromGame(Game game, Integer userID) {
        try {
            if(game.getPlayers().contains(userID)) {
                List<Integer> players = game.getPlayers();
                if (game.getRunning()) return "running";
                players.remove(Integer.valueOf(userID));
                game.setPlayers(players);
                gameRepository.save(game);
                return "removed";
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String closeGame(Game game, Integer userID) {
        try {
            if (game.getRunning()) return "running";
            if (game.getOpen() && Objects.equals(game.getOwnerID(), userID)) {
                game.setOpen(false);
                game.setOver(true);
                game.setRunning(false);
                gameRepository.save(game);
                return "closed";
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<GameStateDataDto> getPlayerList(Game game) {
        try {
            if (game != null) {
                List<GameStateDataDto> playerList = new ArrayList<>();
                List<Integer> players = game.getPlayers();
                players.forEach(id -> {
                    GameStateDataDto player = null;
                    try {
                        player = getStateData(game, id);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    playerList.add(player);
                });
                return playerList;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public GameStateDataDto getStateData(Game game, Integer id) throws JsonProcessingException {
        List<GameStateDataDto> playersData = game.getGameStateData();
        for(GameStateDataDto player : playersData) {
            if(Objects.equals(player.getUserID(), id)) {
                return player;
            }
        }

        return null;
    }

    public void setPlayerData(Game game, List<GameStateDataDto> playerData) {
        game.setGameStateData(playerData);
        gameRepository.save(game);
    }
}
