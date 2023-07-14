package de.hsel.tetris.coregameloop;

import de.hsel.tetris.coregameloop.services.BlockService;
import de.hsel.tetris.coregameloop.tetrominos.I;
import de.hsel.tetris.coregameloop.tetrominos.Type;
import de.hsel.tetris.patterns.observer.Observer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static de.hsel.tetris.coregameloop.Color.RED;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardTest {
    @Mock
    private BlockService blockService;

    @Mock
    private Observer<Board.BoardState> observer;

    @Test
    public void shouldProcessQueuedCommandsAppropriatelyWhenUpdatingBoard() throws InterruptedException {
        // setup mocks
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var board = new Board(blockService);
        board.register(observer);
        board.startBoard();
        board.executeQueuedCommands();

        board.notifyObservers();

        verify(observer, times(1)).update(any());

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer).update(captor.capture());
        var boardState = captor.getValue();

        // board properties are initialized correctly
        assertThat(boardState.level()).isEqualTo(0);
        assertThat(boardState.score()).isEqualTo(0);
        assertThat(boardState.currentBlock().type()).isEqualTo(Type.I);
        assertThat(boardState.nextBlock().type()).isEqualTo(Type.I);
        assertThat(boardState.status()).isEqualTo(Board.BoardStatus.Interactive);

        Integer[][] merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        // Block is in initial state at the correct position
        assertThat(merged[2][3]).isEqualTo(RED.value);
        assertThat(merged[2][4]).isEqualTo(RED.value);
        assertThat(merged[2][5]).isEqualTo(RED.value);
        assertThat(merged[2][6]).isEqualTo(RED.value);


        // move block right
        board.right();
        board.executeQueuedCommands();
        board.notifyObservers();

        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(observer, times(2)).update(any());
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        verify(observer, times(2)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        // block is in correct position after right movement
        assertThat(merged[2][4]).isEqualTo(RED.value);
        assertThat(merged[2][5]).isEqualTo(RED.value);
        assertThat(merged[2][6]).isEqualTo(RED.value);
        assertThat(merged[2][7]).isEqualTo(RED.value);


        // rotate block
        board.rotate();
        board.executeQueuedCommands();
        board.notifyObservers();

        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(observer, times(3)).update(any());
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        verify(observer, times(3)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        // block is in correct position after rotation
        assertThat(merged[0][6]).isEqualTo(RED.value);
        assertThat(merged[1][6]).isEqualTo(RED.value);
        assertThat(merged[2][6]).isEqualTo(RED.value);
        assertThat(merged[3][6]).isEqualTo(RED.value);


        //move block left
        board.left();
        board.executeQueuedCommands();
        board.notifyObservers();

        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(observer, times(4)).update(any());
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        verify(observer, times(4)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        // block is in correct position after left movement
        assertThat(merged[0][5]).isEqualTo(RED.value);
        assertThat(merged[1][5]).isEqualTo(RED.value);
        assertThat(merged[2][5]).isEqualTo(RED.value);
        assertThat(merged[3][5]).isEqualTo(RED.value);


        // drop block
        board.drop();
        board.executeQueuedCommands();
        board.notifyObservers();

        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(observer, times(5)).update(any());
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        verify(observer, times(5)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        // block is in correct position after drop
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);


        // tick the board
        var lastRun = board.updateBoard(0, 0); // schedule and run tick, schedule endTurn
        Thread.sleep(17);
        lastRun = board.updateBoard(lastRun, 0); // run endTurn
        Thread.sleep(500);
        lastRun = board.updateBoard(lastRun, 0); // schedule startTurn
        Thread.sleep(500);
        board.updateBoard(lastRun, 0);

        verify(observer, times(8)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());

        prettyPrint(merged);

        // new block is in initial correct position
        assertThat(merged[2][3]).isEqualTo(RED.value);
        assertThat(merged[2][4]).isEqualTo(RED.value);
        assertThat(merged[2][5]).isEqualTo(RED.value);
        assertThat(merged[2][6]).isEqualTo(RED.value);

        // previous block is persisted to the grid
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);

        board.drop();
        board.executeQueuedCommands();
        board.notifyObservers();

        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(observer, times(9)).update(any());
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        verify(observer, times(9)).update(captor.capture());
        assertThat(captor.getValue()).isNotEqualTo(boardState);
        boardState = captor.getValue();

        merged = mergeWithOffset(boardState.grid(), boardState.currentBlock().blockMatrix(), boardState.currentBlock().x() + 3, boardState.currentBlock().y());


        // nee block drops only until collision
        assertThat(merged[13][3]).isEqualTo(RED.value);
        assertThat(merged[13][4]).isEqualTo(RED.value);
        assertThat(merged[13][5]).isEqualTo(RED.value);
        assertThat(merged[13][6]).isEqualTo(RED.value);

        // previous block still persisted in correct position
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);

    }

    @Test
    void shouldCorrectlyRemoveOneLine() {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        for (var i = 3; i < grid.getCells()[Grid.MIN_HEIGHT - 1].length - 3; i++) {
            if (i > 6) {
                grid.getCells()[Grid.MIN_HEIGHT - 1][i] = RED.value;
            }
        }

        board.register(observer);
        board.startBoard();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var merged = mergeWithOffset(
                boardState.grid(),
                boardState.currentBlock().blockMatrix(),
                boardState.currentBlock().x() + 3,
                boardState.currentBlock().y());

        assertThat(merged[17][3]).isEqualTo(RED.value);
        assertThat(merged[17][4]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);
        assertThat(merged[17][6]).isEqualTo(RED.value);
        assertThat(merged[17][7]).isEqualTo(RED.value);
        assertThat(merged[17][8]).isEqualTo(RED.value);
        assertThat(merged[17][9]).isEqualTo(RED.value);
        assertThat(merged[17][10]).isEqualTo(RED.value);
        assertThat(merged[17][11]).isEqualTo(RED.value);
        assertThat(merged[17][12]).isEqualTo(RED.value);

        board.updateBoard(0,0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.grid().length).isEqualTo(21);
        assertThat(boardState.grid()[17][3]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][4]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][6]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][7]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][8]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][9]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][10]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][11]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][12]).isEqualTo(WHITE.value);
        assertThat(boardState.score()).isEqualTo(40);
    }

    @Test
    void shouldCorrectlyRemoveTwoLines() {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 2; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var merged = mergeWithOffset(
                boardState.grid(),
                boardState.currentBlock().blockMatrix(),
                boardState.currentBlock().x() + 3,
                boardState.currentBlock().y());

        assertThat(merged[17][3]).isEqualTo(RED.value);
        assertThat(merged[17][4]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[17][6]).isEqualTo(RED.value);
        assertThat(merged[17][7]).isEqualTo(RED.value);
        assertThat(merged[17][8]).isEqualTo(RED.value);
        assertThat(merged[17][9]).isEqualTo(RED.value);
        assertThat(merged[17][10]).isEqualTo(RED.value);
        assertThat(merged[17][11]).isEqualTo(RED.value);
        assertThat(merged[17][12]).isEqualTo(RED.value);

        board.updateBoard(0,0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.grid().length).isEqualTo(21);
        assertThat(boardState.grid()[17][3]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][4]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][5]).isEqualTo(RED.value);
        assertThat(boardState.grid()[16][5]).isEqualTo(RED.value);
        assertThat(boardState.grid()[15][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[14][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][6]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][7]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][8]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][9]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][10]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][11]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][12]).isEqualTo(WHITE.value);
        assertThat(boardState.score()).isEqualTo(100);
    }


    @Test
    void shouldCorrectlyRemoveThreeLines() {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 3; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var merged = mergeWithOffset(
                boardState.grid(),
                boardState.currentBlock().blockMatrix(),
                boardState.currentBlock().x() + 3,
                boardState.currentBlock().y());

        assertThat(merged[17][3]).isEqualTo(RED.value);
        assertThat(merged[16][3]).isEqualTo(RED.value);
        assertThat(merged[15][3]).isEqualTo(RED.value);
        assertThat(merged[17][4]).isEqualTo(RED.value);
        assertThat(merged[16][4]).isEqualTo(RED.value);
        assertThat(merged[15][4]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[17][6]).isEqualTo(RED.value);
        assertThat(merged[16][6]).isEqualTo(RED.value);
        assertThat(merged[15][6]).isEqualTo(RED.value);
        assertThat(merged[17][7]).isEqualTo(RED.value);
        assertThat(merged[16][7]).isEqualTo(RED.value);
        assertThat(merged[15][7]).isEqualTo(RED.value);
        assertThat(merged[17][8]).isEqualTo(RED.value);
        assertThat(merged[16][8]).isEqualTo(RED.value);
        assertThat(merged[16][8]).isEqualTo(RED.value);
        assertThat(merged[17][9]).isEqualTo(RED.value);
        assertThat(merged[16][9]).isEqualTo(RED.value);
        assertThat(merged[15][9]).isEqualTo(RED.value);
        assertThat(merged[17][10]).isEqualTo(RED.value);
        assertThat(merged[16][10]).isEqualTo(RED.value);
        assertThat(merged[15][10]).isEqualTo(RED.value);
        assertThat(merged[17][11]).isEqualTo(RED.value);
        assertThat(merged[16][11]).isEqualTo(RED.value);
        assertThat(merged[15][11]).isEqualTo(RED.value);
        assertThat(merged[17][12]).isEqualTo(RED.value);
        assertThat(merged[16][12]).isEqualTo(RED.value);
        assertThat(merged[15][12]).isEqualTo(RED.value);

        board.updateBoard(0,0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.grid().length).isEqualTo(21);
        assertThat(boardState.grid()[17][3]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][4]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][5]).isEqualTo(RED.value);
        assertThat(boardState.grid()[16][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[15][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[14][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][6]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][7]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][8]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][9]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][10]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][11]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][12]).isEqualTo(WHITE.value);

        assertThat(boardState.score()).isEqualTo(300);
    }


    @Test
    void shouldCorrectlyRemoveFourLines() {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 4; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var merged = mergeWithOffset(
                boardState.grid(),
                boardState.currentBlock().blockMatrix(),
                boardState.currentBlock().x() + 3,
                boardState.currentBlock().y());

        assertThat(merged[17][3]).isEqualTo(RED.value);
        assertThat(merged[16][3]).isEqualTo(RED.value);
        assertThat(merged[15][3]).isEqualTo(RED.value);
        assertThat(merged[14][3]).isEqualTo(RED.value);
        assertThat(merged[17][4]).isEqualTo(RED.value);
        assertThat(merged[16][4]).isEqualTo(RED.value);
        assertThat(merged[15][4]).isEqualTo(RED.value);
        assertThat(merged[14][4]).isEqualTo(RED.value);
        assertThat(merged[17][5]).isEqualTo(RED.value);
        assertThat(merged[16][5]).isEqualTo(RED.value);
        assertThat(merged[15][5]).isEqualTo(RED.value);
        assertThat(merged[14][5]).isEqualTo(RED.value);
        assertThat(merged[17][6]).isEqualTo(RED.value);
        assertThat(merged[16][6]).isEqualTo(RED.value);
        assertThat(merged[15][6]).isEqualTo(RED.value);
        assertThat(merged[14][6]).isEqualTo(RED.value);
        assertThat(merged[17][7]).isEqualTo(RED.value);
        assertThat(merged[16][7]).isEqualTo(RED.value);
        assertThat(merged[15][7]).isEqualTo(RED.value);
        assertThat(merged[14][7]).isEqualTo(RED.value);
        assertThat(merged[17][8]).isEqualTo(RED.value);
        assertThat(merged[16][8]).isEqualTo(RED.value);
        assertThat(merged[15][8]).isEqualTo(RED.value);
        assertThat(merged[14][8]).isEqualTo(RED.value);
        assertThat(merged[17][9]).isEqualTo(RED.value);
        assertThat(merged[16][9]).isEqualTo(RED.value);
        assertThat(merged[15][9]).isEqualTo(RED.value);
        assertThat(merged[14][9]).isEqualTo(RED.value);
        assertThat(merged[17][10]).isEqualTo(RED.value);
        assertThat(merged[16][10]).isEqualTo(RED.value);
        assertThat(merged[15][10]).isEqualTo(RED.value);
        assertThat(merged[14][10]).isEqualTo(RED.value);
        assertThat(merged[17][11]).isEqualTo(RED.value);
        assertThat(merged[16][11]).isEqualTo(RED.value);
        assertThat(merged[15][11]).isEqualTo(RED.value);
        assertThat(merged[14][11]).isEqualTo(RED.value);
        assertThat(merged[17][12]).isEqualTo(RED.value);
        assertThat(merged[16][12]).isEqualTo(RED.value);
        assertThat(merged[15][12]).isEqualTo(RED.value);
        assertThat(merged[14][12]).isEqualTo(RED.value);

        board.updateBoard(0,0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.grid().length).isEqualTo(21);
        assertThat(boardState.grid()[17][3]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][4]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[16][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[15][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[14][5]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][6]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][7]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][8]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][9]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][10]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][11]).isEqualTo(WHITE.value);
        assertThat(boardState.grid()[17][12]).isEqualTo(WHITE.value);

        assertThat(boardState.score()).isEqualTo(1200);
    }

    @ParameterizedTest
    @CsvSource({"1,80", "15,640", "29,1200"})
    void shouldCorrectlyScoreOneLine(Integer level, Integer expectedScore) {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        ReflectionTestUtils.setField(board, "level", level);

        for (var i = 3; i < grid.getCells()[Grid.MIN_HEIGHT - 1].length - 3; i++) {
            if (i > 6) {
                grid.getCells()[Grid.MIN_HEIGHT - 1][i] = RED.value;
            }
        }

        board.register(observer);
        board.startBoard();
        board.drop();
        board.updateBoard(0, 0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var lastRun = board.updateBoard(0, 0);
        lastRun = board.updateBoard(lastRun, 0);
        board.updateBoard(lastRun, 0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.score()).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @CsvSource({"1,200", "15,1600", "29,3000"})
    void shouldCorrectlyScoreTwoLines(Integer level, Integer expectedScore) {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        ReflectionTestUtils.setField(board, "level", level);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 2; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var lastRun = board.updateBoard(0, 0);
        lastRun = board.updateBoard(lastRun, 0);
        board.updateBoard(lastRun, 0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.score()).isEqualTo(expectedScore);
    }


    @ParameterizedTest
    @CsvSource({"1,600", "15,4800", "29,9000"})
    void shouldCorrectlyScoreThreeLines(Integer level, Integer expectedScore) {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        ReflectionTestUtils.setField(board, "level", level);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 3; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var lastRun = board.updateBoard(0, 0);
        lastRun = board.updateBoard(lastRun, 0);
        board.updateBoard(lastRun, 0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.score()).isEqualTo(expectedScore);
    }


    @ParameterizedTest
    @CsvSource({"1,2400", "15,19200", "29,36000"})
    void shouldCorrectlyScoreFourLines(Integer level, Integer expectedScore) {
        when(blockService.getRandomBlock(anyInt(), anyInt())).thenAnswer(ignored -> new I(0, 0));

        var grid = new Grid();
        var board = new Board(blockService, grid);

        ReflectionTestUtils.setField(board, "level", level);

        for (var y = Grid.MIN_HEIGHT - 1; y >= Grid.MIN_HEIGHT - 4; y--) {
            for (var x = 3; x < grid.getCells()[y].length - 3; x++) {
                if (x != 5) {
                    grid.getCells()[y][x] = RED.value;
                }
            }
        }

        board.register(observer);
        board.startBoard();
        board.rotate();
        board.drop();
        board.updateBoard(0,0);

        var captor = ArgumentCaptor.forClass(Board.BoardState.class);
        verify(observer, times(1)).update(captor.capture());
        var boardState = captor.getValue();

        var lastRun = board.updateBoard(0, 0);
        lastRun = board.updateBoard(lastRun, 0);
        board.updateBoard(lastRun, 0);

        verify(observer, times(2)).update(captor.capture());
        boardState = captor.getValue();

        assertThat(boardState.score()).isEqualTo(expectedScore);
    }

    private Integer[][] mergeWithOffset(final Integer[][] arr1, final Integer[][] arr2, final Integer offsetX, final Integer offsetY) {
        var copy = Arrays.stream(Arrays.copyOf(arr1, arr1.length)).map(row -> Arrays.copyOf(row, row.length)).toArray(Integer[][]::new);
        for (var y = 0; y < arr2.length; y++) {
            for (var x = 0; x < arr2[y].length; x++) {
                if (copy[y + offsetY][x + offsetX] == 0) {
                    copy[y + offsetY][x + offsetX] = arr2[y][x];
                }
            }
        }
        return copy;
    }

    private String stringifyIntegerMatrix(final Integer[][] matrix) {
        var result = new StringBuilder();
        for (var row : matrix) {
            for (var col : row) {
                result.append(col.toString().replace("0", "_"));
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void prettyPrint(Integer[][] mat) {
        System.out.print(stringifyIntegerMatrix(mat));
    }
}