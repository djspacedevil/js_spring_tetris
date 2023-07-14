package de.hsel.tetris.coregameloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.hsel.tetris.coregameloop.services.BlockService;
import de.hsel.tetris.game.Game;
import de.hsel.tetris.patterns.observer.Observable;
import de.hsel.tetris.patterns.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TetrisGame implements Observer<Board.BoardState>, Observable<TetrisGame.GameState>, Runnable {
    private final Game game;
    private final Map<Integer, Board> players = new ConcurrentHashMap<>();
    private final Map<Integer, Board.BoardState> boardStates = new ConcurrentHashMap<>();
    private GameRunStatus status = GameRunStatus.Open;
    private final BlockService blockService;
    private final BlockingQueue<GameCommand> commandQueue = new LinkedBlockingQueue<>();
    private final Set<Observer<GameState>> observers = new CopyOnWriteArraySet<>();
    private static final Logger LOG = LoggerFactory.getLogger(TetrisGame.class);

    public TetrisGame(final Game game, BlockService blockService) {
        this.game = game;
        this.blockService = blockService;
    }

    public boolean isOpen() {
        return status == GameRunStatus.Open;
    }

    public boolean isRunning() {
        return status == GameRunStatus.Running;
    }

    public boolean isOver() {
        return status == GameRunStatus.Over;
    }

    public void start() {
        if (isOver()) {
            return;
        }

        LOG.info("Start Game");

        game.setState("running");
        status = GameRunStatus.Running;

        players.values().forEach(Board::startBoard);
    }

    public void finish() {
        if (isOver()) {
            return;
        }

        game.setState("over");
        status = GameRunStatus.Over;

        LOG.info("End Status: {}", status);

        players.values().forEach(Board::finishBoard);
    }

    public void addPlayer(final Integer playerId) {
        if (!isOpen()) {
            return;
        }

        LOG.info("Add Player " + playerId);

        var board = new Board(blockService, playerId);
        board.register(this);

        players.put(playerId, board);
        updatePlayersInGame();

//        executor.execute(board);
    }

    private void updatePlayersInGame() {
        game.setPlayers(players.keySet().stream().toList());
    }

    public void removePlayer(final Integer playerId) {
        if (!isOpen()) {
            return;
        }

        players.remove(playerId);
        updatePlayersInGame();
    }

    public void rotate(final Integer playerId) {
        queueCommand(new GameCommand(playerId, PlayerCommandType.Rotate));
    }

    public void left(final Integer playerId) {
        queueCommand(new GameCommand(playerId, PlayerCommandType.Left));
    }

    public void right(final Integer playerId) {
        queueCommand(new GameCommand(playerId, PlayerCommandType.Right));
    }

    public void drop(final Integer playerId) {
        queueCommand(new GameCommand(playerId, PlayerCommandType.Drop));
    }

    public void giveUp(final Integer playerId) {
        queueCommand(new GameCommand(playerId, PlayerCommandType.GiveUp));
    }

    // TODO: it is possible that commands get lost when trying to add to a blocked queue, consider a retry mechanism
    private void queueCommand(final GameCommand command) {
        var result = this.commandQueue.offer(command);
        if (!result) {
            LOG.warn("Could not queue {} command for player {}", command.playerId, command.type);
        }
    }

    @Override
    public void register(Observer<GameState> observer) {
        var result = observers.add(observer);
        if (!result) {
            LOG.warn("Could not add observer {} to TetrisGame {}", observer, this);
        }
    }

    @Override
    public void unregister(Observer<GameState> observer) {
        var result = observers.remove(observer);
        if (!result) {
            LOG.warn("Could not remove observer {} from TetrisGame {}", observer, this);
        }
    }

    @Override
    public void notifyObservers() {
        var gameState = new GameState(game.getId(), status, boardStates.values());
        LOG.info("gameState: {}", gameState);
        for (var observer : observers) {
            observer.update(gameState);
        }
    }

    @Override
    public void update(Board.BoardState data) {
        boardStates.put(data.playerId(), data);
    }

    @Override
    public void run() {
        var desiredUpdateTime = 1000000000L / 180L;
        var previousTime = System.nanoTime();
        var frameCounter = 0;

        LOG.info("start game loop");
        while (status != GameRunStatus.Over) {
            var currentTime = System.nanoTime();
            var elapsedTime = currentTime - previousTime;

            if (elapsedTime < desiredUpdateTime) {
                try {
                    Thread.sleep((desiredUpdateTime - elapsedTime) / 1000000L);
                    continue;
                } catch (InterruptedException e) {
                    LOG.error("InterruptedException: {}", e.getMessage());
                }
            }

            if(players.values().stream().allMatch(Board::isFinished)) {
                finish();
            }

            applyCommands();

            for (var board : players.values()) {
                board.updateBoard(previousTime, desiredUpdateTime);
                this.update(board.getState());
            }

            previousTime = currentTime;

            players.values().stream().map(Board::getState).forEach(this::update);

            if(frameCounter % 3 == 0) {
                notifyObservers();
                frameCounter = 0;
            }

            frameCounter++;
        }
        notifyObservers();
    }


    private void applyCommands() {
        LOG.info("update game");
        var commands = new ArrayList<GameCommand>();
        commandQueue.drainTo(commands);
        commands.forEach(this::runCommand);
    }

    private void runCommand(GameCommand command) {
        var board = players.get(command.playerId);

        if (board == null) {
            return;
        }

        switch (command.type) {
            case Rotate -> board.rotate();
            case Left -> board.left();
            case Right -> board.right();
            case Drop -> board.drop();
            case GiveUp -> board.giveUp();
        }
    }

    public enum GameRunStatus {
        Open,
        Running,
        Over
    }

    public enum PlayerCommandType {
        Rotate,
        Left,
        Right,
        Drop,
        GiveUp
    }

    public record GameCommand(Integer playerId, PlayerCommandType type) {
    }

    public record GameState(@JsonProperty("game_id") Integer gameId, @JsonProperty("status") GameRunStatus status,
                            @JsonProperty("players") Collection<Board.BoardState> boards) {
        @JsonProperty("type")
        public String type() {
            return "update";
        }
    }
}
