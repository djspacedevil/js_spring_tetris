package de.hsel.tetris.coregameloop;

import de.hsel.tetris.coregameloop.services.BlockService;
import de.hsel.tetris.coregameloop.tetrominos.Tetromino;
import de.hsel.tetris.coregameloop.tetrominos.dto.TetrominoDto;
import de.hsel.tetris.patterns.observer.Observable;
import de.hsel.tetris.patterns.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

public class Board implements Observable<Board.BoardState>, Runnable {
    private final Set<Observer<BoardState>> observers = new CopyOnWriteArraySet<>();
    private final BlockingQueue<BoardCommand> commandQueue = new LinkedBlockingQueue<>();
    private final Rules rules;
    private Integer playerId = null;
    private Integer totalLineCounter = 0;
    private static final Logger LOG = LoggerFactory.getLogger(Board.class);
    private long entryDelayTimer = -1L;
    private final BlockService blockService;
    private Integer level;
    private Integer score;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private BoardStatus status;
    private final Grid grid;
    private Long lastTickTime = 0L;


    // TODO: clean up constructors
    public Board(final BlockService blockService, final Integer width, final Integer height) {
        this(blockService, new Grid(width, height), new Rules());
    }

    public Board(final BlockService blockService) {
        this(blockService, Grid.MIN_WIDTH, Grid.MIN_HEIGHT);
    }

    public Board(final BlockService blockService, final Grid grid) {
        this(blockService, grid, new Rules());
    }

    public Board(final BlockService blockService, final Grid grid, final Rules rules) {
        this.blockService = blockService;
        this.grid = grid;
        this.rules = rules;
        level = rules.getNewLevel(totalLineCounter);
        score = 0;
        status = BoardStatus.Frozen;
        currentTetromino = initNewTetromino();
        nextTetromino = initNewTetromino();
    }

    public Board(BlockService blockService, Integer playerId) {
        this(blockService);
        this.playerId = playerId;
    }

    private Tetromino initNewTetromino() {
        return blockService.getRandomBlock((grid.getWidth() / 2) - 2, 0);
    }

    // TODO: unused for now, since it wasn't reliably working to have every board in a separate thread;
    @Override
    public void run() {
        long desiredUpdateTime = 1000000000L / 60L;
        long previousTime = System.nanoTime();

        LOG.info("start board loop. status: {}", status);

        var inLoop = false;
        while (status != BoardStatus.Finished) {
            var currentTime = System.nanoTime();
            var elapsedTime = currentTime - previousTime;

            if (elapsedTime < desiredUpdateTime) {
                try {
                    Thread.sleep((desiredUpdateTime - elapsedTime) / 1000000L);
                } catch (InterruptedException e) {
                    LOG.error("InterruptedException: {}", e.getMessage());
                }
            }

            if (!inLoop) {
                LOG.info("board while loop started");
                inLoop = true;
            }

            previousTime = updateBoard(previousTime, desiredUpdateTime);
        }
    }

    public Long updateBoard(long previousTime, long desiredUpdateTime) {
        long currentTime = System.nanoTime();

        if (status == BoardStatus.Frozen) {
            return currentTime;
        }

        long elapsedTime = currentTime - previousTime;

        if (inBetweenTurns()) {
            LOG.info("In between turns");
            entryDelayTimer -= elapsedTime;
            commandQueue.clear();
            return currentTime;
        }

        LOG.info("Elapsed: {}", elapsedTime);

        if (elapsedTime >= desiredUpdateTime) {
            if (shouldStartTurn()) {
                LOG.info("Should Start Turn");
                commandQueue.add(BoardCommand.StartTurn);
            } else if (shouldTick(currentTime)) {
                LOG.info("Should Tick");
                commandQueue.add(BoardCommand.Tick);
            }

            executeQueuedCommands();
            notifyObservers();
        }

        return currentTime;
    }

    private boolean inBetweenTurns() {
        return entryDelayTimer >= 0;
    }

    private boolean shouldStartTurn() {
        return !inBetweenTurns() && currentTetromino == null;
    }

    private boolean shouldTick(final Long currentTime) {
        if (inBetweenTurns() || shouldStartTurn()) {
            return false;
        }
        var tickDuration = rules.getTickTimeForLevel(level);
        return currentTime - lastTickTime >= tickDuration;
    }

    // TODO: here exists a possibility, that commands get lost because it's tried to add to a blocked queue. Consider retry mechanism
    protected void queueCommand(final BoardCommand command) {
        var result = commandQueue.offer(command);

        if (!result) {
            LOG.warn("Command {} not added to Board {}.", command, this);
        }
    }

    @Override
    public void notifyObservers() {
        BoardState boardState = new BoardState(level, score, status, currentTetromino == null ? null : currentTetromino.serialize(), nextTetromino.serialize(), grid.serialize(), playerId);
        LOG.info("boardState: {}", boardState);
        for (var observer : observers) {
            observer.update(boardState);
        }

    }

    protected void executeQueuedCommands() {
        // to only run commands queued up until now, the queue is drained to a list which is then processed.
        // commands queued in the meantime are processed next time updateBoard is invoked
        var commands = new ArrayList<BoardCommand>();
        commandQueue.drainTo(commands);

        LOG.info("Commands: {}", commands);

        commands.forEach(this::runCommand);
    }

    private void runCommand(final BoardCommand command) {
        LOG.info("Run command: {}", command);
        if (status == BoardStatus.Finished) {
            return;
        }

        if (command == null) {
            return;
        }

        if (status == BoardStatus.Frozen) {
            return;
        }

        var cto = Optional.ofNullable(currentTetromino);

        switch (command) {
            case GiveUp -> finishBoard();
            case Rotate -> cto.ifPresent(t -> t.rotate(grid));
            case Left -> cto.ifPresent(t -> t.moveLeft(grid));
            case Right -> cto.ifPresent(t -> t.moveRight(grid));
            case Drop -> cto.ifPresent(t -> t.drop(grid));
            case StartTurn -> startTurn();
            case EndTurn -> endTurn();
            case Tick -> tick();
        }
    }


    public void startBoard() {
        LOG.info("start board");
        status = BoardStatus.Interactive;
    }

    public void finishBoard() {
        status = BoardStatus.Finished;
    }

    public void rotate() {
        queueCommand(BoardCommand.Rotate);
    }

    public void left() {
        queueCommand(BoardCommand.Left);
    }

    public void right() {
        queueCommand(BoardCommand.Right);
    }

    public void drop() {
        queueCommand(BoardCommand.Drop);
    }

    public void giveUp() {
        queueCommand(BoardCommand.GiveUp);
    }

    private void startTurn() {
        currentTetromino = nextTetromino;
        nextTetromino = initNewTetromino();

        if (grid.checkCollision(
                currentTetromino.getX(),
                currentTetromino.getY(),
                currentTetromino.getMatrix())) {
            status = BoardStatus.Finished;
        }
    }

    private void endTurn() {
        currentTetromino.place(grid);
        var linesRemoved = removeLines();
        updateScore(linesRemoved);
        updateLevel();
        totalLineCounter += linesRemoved;
        entryDelayTimer = rules.getEntryDelayTime();
        currentTetromino = null;
    }

    private void tick() {
        LOG.info("tick");
        lastTickTime = System.nanoTime();
        if (currentTetromino.moveDown(grid) == PlacementResult.Collision) {
            queueCommand(BoardCommand.EndTurn);
        }
    }

    private Integer removeLines() {
        return grid.removeLines();
    }

    private void updateScore(final Integer linesRemoved) {
        score = score + rules.getScoreForLinesByLevel(level, linesRemoved);
        LOG.info("Updated score to: {}", score);
    }

    private void updateLevel() {
        level = rules.getNewLevel(totalLineCounter);
        LOG.info("Updated level to {}", level);
    }

    @Override
    public void register(Observer<BoardState> observer) {
        if (observer == null) {
            return;
        }
        observers.add(observer);
    }

    @Override
    public void unregister(Observer<BoardState> observer) {
        if (observer == null) {
            return;
        }
        observers.remove(observer);
    }

    public BoardState getState() {
        return new BoardState(level, score, status, currentTetromino == null ? null : currentTetromino.serialize(), nextTetromino.serialize(), grid.serialize(), playerId);
    }

    public boolean isFinished() {
        return status == BoardStatus.Finished;
    }

    protected enum BoardCommand {
        Rotate,
        Left,
        Right,
        Drop,
        GiveUp,
        Tick,
        StartTurn,
        EndTurn
    }

    public enum BoardStatus {
        Interactive,
        Frozen,
        Finished,
    }

    public record BoardState(Integer level, Integer score, BoardStatus status, TetrominoDto currentBlock,
                             TetrominoDto nextBlock, Integer[][] grid, Integer playerId) {
    }
}
