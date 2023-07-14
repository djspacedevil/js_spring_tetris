package de.hsel.tetris.rawwebsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsel.tetris.coregameloop.TetrisGame;
import de.hsel.tetris.game.Game;
import de.hsel.tetris.game.GameHistoryRepository;
import de.hsel.tetris.game.GameHistoryService;
import de.hsel.tetris.game.GameRepository;
import de.hsel.tetris.patterns.observer.Observer;
import org.jline.utils.Log;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ServerWebSocketGameState implements Observer<TetrisGame.GameState> {

    private final Game game;
    private final GameRepository gameRepository;
    private final List<WebSocketSession> sessions;
    private TetrisGame.GameState gameState;
    private final GameHistoryRepository gameHistoryRepository;


    public ServerWebSocketGameState(final Game game, final List<WebSocketSession> sessions, GameRepository gameRepository, GameHistoryRepository gameHistoryRepository) {
        this.game = game;
        this.sessions = sessions;
        this.gameRepository = gameRepository;
        this.gameHistoryRepository = gameHistoryRepository;
    }

    @Override
    public void update(TetrisGame.GameState data) {
        gameState = data;
        if (Objects.equals(gameState.status().name(), "Over")) {
            closeGame(game, gameState);
        }
        try {
            Iterator<WebSocketSession> iterator = sessions.iterator();
            ObjectMapper mapper = new ObjectMapper();
            var json = mapper.writeValueAsString(gameState);

            while (iterator.hasNext()) {
                var current = iterator.next();
                if(current.isOpen()) {
                    current.sendMessage(new TextMessage(json));
                    if (Objects.equals(gameState.status().name(), "Over")) {
                        current.sendMessage(new TextMessage("{\"error\": \"game is finished...\"}"));
                        current.close((CloseStatus.NORMAL.withReason("Game is over. Session close")));
                    }
                }
            }

        } catch(Exception e) {
            Log.error(e.getMessage());
        }
    }

    private void closeGame(Game game, TetrisGame.GameState gameState) {
        game.setState("over");
        game.setRunning(false);
        game.setOver(true);
        gameRepository.save(game);
        GameHistoryService gameHistoryService = new GameHistoryService(gameHistoryRepository);
        gameState.boards().forEach(boardState -> {
            gameHistoryService.addUserHistory(
                    boardState.playerId(), boardState.score(), new Date(), boardState.status().name()
            );
        });


    }

}
