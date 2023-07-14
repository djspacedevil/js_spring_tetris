package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TetrominoTest {

    @Test
    void shouldMoveLeftWhenThereIsRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(1, 0);

        assertEquals(PlacementResult.Success, tetromino.moveLeft(grid));
    }

    @Test
    void shouldCollideOnMoveLeftWhenThereIsNoRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(0, 0);

        assertEquals(PlacementResult.Collision, tetromino.moveLeft(grid));
    }


    @Test
    void shouldMoveRightWhenThereIsRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(grid.getWidth() - 2, 0);

        assertEquals(PlacementResult.Success, tetromino.moveRight(grid));
    }

    @Test
    void shouldCollideOnMoveRightWhenThereIsNoRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(grid.getWidth() - 1, 0);

        assertEquals(PlacementResult.Collision, tetromino.moveRight(grid));
    }


    @Test
    void shouldMoveDownWhenThereIsRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(0, grid.getHeight() - 2);

        assertEquals(PlacementResult.Success, tetromino.moveDown(grid));
    }

    @Test
    void shouldCollideOnMoveDownWhenThereIsNoRoom() {
        var grid = new Grid();
        var tetromino = new TestTetromino(0, grid.getHeight() - 1);

        assertEquals(PlacementResult.Collision, tetromino.moveDown(grid));
    }

    @Test
    void shouldPositionAtTheLowestConflictFreePointInTheGridOnDrop() {
        var grid = new Grid();
        var tetromino = new TestTetromino(0, 0);

        tetromino.drop(grid);

        assertEquals(0, tetromino.x);
        assertEquals(grid.getHeight() - 1, tetromino.y);
    }
}