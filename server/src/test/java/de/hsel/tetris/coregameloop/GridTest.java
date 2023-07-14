package de.hsel.tetris.coregameloop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridTest {

    @Test
    void shouldCreateDefaultGrid() {
        var grid = new Grid();

        assertEquals(grid.getCells().length, 21);

        for (var row : grid.getCells()) {
            assertEquals(16, row.length);
        }
    }

    @Test
    void shouldCreateCustomGrid() {
        var grid = new Grid(12, 21);
        assertEquals(grid.getCells().length, 24);
        for (var row : grid.getCells()) {
            assertEquals(18, row.length);
        }
    }

    @Test
    void shouldCreateMinimalGridWhenBelowMinimalSize() {
        var grid = new Grid(9, 17);

        assertEquals(grid.getCells().length, 21);

        for (var row : grid.getCells()) {
            assertEquals(16, row.length);
        }
    }

    @Test
    void shouldCreateMinimalGridWhenAboveMaximalSize() {
        var grid = new Grid(21, 37);

        assertEquals(grid.getCells().length, 21);

        for (var row : grid.getCells()) {
            assertEquals(16, row.length);
        }
    }

    @Test
    void shouldNotCollideWhenTetrominoInBounds() {
        var grid = new Grid();
        var x = 0;
        var y = 0;
        var matrix = new Integer[][]{{8}};

        assertFalse(grid.checkCollision(x, y, matrix));
    }

    @Test
    void shouldCollideWhenTetrominoOutOfBoundsLeft() {
        var grid = new Grid();
        var x = -1;
        var y = 0;
        var matrix = new Integer[][]{{8}};

        assertTrue(grid.checkCollision(x, y, matrix));
    }


    @Test
    void shouldCollideWhenTetrominoOutOfBoundsBottom() {
        var grid = new Grid();
        var x = 0;
        var y = grid.getHeight();
        var matrix = new Integer[][]{{8}};

        assertTrue(grid.checkCollision(x, y, matrix));
    }

    @Test
    void shouldCollideWhenTetrominoOutOfBoundsRight() {
        var grid = new Grid();
        var x = grid.getWidth();
        var y = 0;
        var matrix = new Integer[][]{{8}};

        assertTrue(grid.checkCollision(x, y, matrix));
    }
}